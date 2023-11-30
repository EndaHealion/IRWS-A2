package com.apple.sauce;

import com.apple.sauce.models.Topic;
import me.tongfei.progressbar.ProgressBar;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppleIndexSearcher {
    public static void searchDocumentIndex(
            Similarity similarity,
            Analyzer analyzer,
            List<Topic> topics,
            String resultFilePath,
            String indexDirectoryPath
    ) throws IOException, ParseException {
        final Directory directory = FSDirectory.open(Paths.get(indexDirectoryPath));
        final DirectoryReader reader = DirectoryReader.open(directory);
        final IndexSearcher searcher = new IndexSearcher(reader);
        searcher.setSimilarity(similarity);
        final QueryParser queryParser = new MultiFieldQueryParser(new String[]{"headline", "text"}, analyzer, boost);

        List<String> results = new ArrayList<>();
        for (final Topic topic : ProgressBar.wrap(topics, "Searching")) {
            final Query query = getQuery(queryParser, topic);
            final ScoreDoc[] hits = searcher.search(query, MAX_RESULTS).scoreDocs;
            final StoredFields fields = searcher.storedFields();
            for (final ScoreDoc hit : hits) {
                Document doc = fields.document(hit.doc);
                results.add(STR. "\{ topic.number() } 0 \{ doc.get("docno") } 0 \{ hit.score } 0" );
            }
        }

        reader.close();
        directory.close();

        Files.write(Paths.get(resultFilePath), results);
    }

    private static Query getQuery(QueryParser queryParser, Topic topic) throws ParseException {
        final String[] splitNarrative = splitNarrative(topic.narrative());
        final String relevantNarrative = splitNarrative[0];
        final String irrelevantNarrative = splitNarrative[1];

        final BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();

        final Query titleQuery = queryParser.parse(QueryParser.escape(topic.title()));
        final Query descriptionQuery = queryParser.parse(QueryParser.escape(topic.description()));
        booleanQuery.add(new BoostQuery(titleQuery, 4f), BooleanClause.Occur.SHOULD);
        booleanQuery.add(new BoostQuery(descriptionQuery, 1.7f), BooleanClause.Occur.SHOULD);

        if (!relevantNarrative.isEmpty()) {
            Query narrativeQuery = queryParser.parse(QueryParser.escape(relevantNarrative));
//            if (narrativeQuery != null)
            booleanQuery.add(new BoostQuery(narrativeQuery, 1.2f), BooleanClause.Occur.SHOULD);
        } else if (!irrelevantNarrative.isEmpty()) {
            Query irrelevantNarrativeQuery = queryParser.parse(QueryParser.escape(irrelevantNarrative));
//            if (irrelevantNarrativeQuery != null)
            booleanQuery.add(new BoostQuery(irrelevantNarrativeQuery, 2f), BooleanClause.Occur.FILTER);
        }

        return booleanQuery.build();
    }

    private static String[] splitNarrative(String narrative) {
        final StringBuilder relevantNarrative = new StringBuilder();
        final StringBuilder irrelevantNarrative = new StringBuilder();

        final BreakIterator bi = BreakIterator.getSentenceInstance();
        bi.setText(narrative);
        int index = 0;
        while (bi.next() != BreakIterator.DONE) {
            final String sentence = narrative.substring(index, bi.current());

            if (!sentence.contains("not relevant") && !sentence.contains("irrelevant")) {
                relevantNarrative.append(sentence.replaceAll(relevantRegex, ""));
            } else {
                irrelevantNarrative.append(sentence.replaceAll(irrelevantRegex, ""));
            }
            index = bi.current();
        }

        final String[] result = new String[2];
        result[0] = relevantNarrative.toString().strip();
        result[1] = irrelevantNarrative.toString().strip();
        return result;
    }

    private static final int MAX_RESULTS = 1000;
    private static final String relevantRegex = "(?i)a relevant document identifies|a relevant document could|a relevant document may|a relevant document must|a relevant document will|a document will|to be relevant|relevant documents|a document must|relevant|will contain|will discuss|will provide|must cite";
    private static final String irrelevantRegex = "(?i)are also not relevant|are not relevant|are irrelevant|is not relevant|not";
    private static final Map<String, Float> boost = Map.of(
            "headline", 0.08f,
            "text", 0.92f
    );
}
