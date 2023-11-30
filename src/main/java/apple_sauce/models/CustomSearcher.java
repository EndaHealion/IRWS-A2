package apple_sauce.models;

import apple_sauce.Util;
import apple_sauce.eNums.AnalyzerType;
import apple_sauce.eNums.SimilarityType;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomSearcher {
    public static final String INDEX_PATH = "index";
    public static final String OUTPUT_PATH = "output/";
    public static final String EVALUATION_RESULT_NAME = "eval_results.txt";
    public static final int MAX_QUERY_RESULTS = 1000;

    public static void queryIndex(List<Topic> topics, AnalyzerType analyzerEnum, SimilarityType similarityEnum) throws Exception {
        long startTime = System.currentTimeMillis();
        Util.printInfo("Evaluating index...");
        // Setup index reader and searcher.
        Analyzer analyzer = analyzerEnum.getAnalyzer();
        Similarity similarity = similarityEnum.getSimilarity();
        Directory indexDir = FSDirectory.open(Paths.get(INDEX_PATH));
        DirectoryReader ireader = DirectoryReader.open(indexDir);
        IndexSearcher isearcher = new IndexSearcher(ireader);
        isearcher.setSimilarity(similarity);

        // Setup query parser.
        String[] searchFields = new String[] {
                "DATE", "SECTION",
                "HEADLINE", "TEXT",
                "TITLES", "AUTHOR",
                "HEADER TAG"
        };

        HashMap<String, Float> fieldWeights = new HashMap<String, Float>();
        fieldWeights.put("DATE", 1.0f);
        fieldWeights.put("SECTION", 1.0f);
        fieldWeights.put("HEADLINE", 1.0f);
        fieldWeights.put("TEXT", 18.0f);
        fieldWeights.put("TITLES", 0.8f);
        fieldWeights.put("AUTHOR", 1.0f);
        fieldWeights.put("HEADER TAG", 1.0f);
        MultiFieldQueryParser parser = new MultiFieldQueryParser(searchFields, analyzer, fieldWeights);

        ArrayList<QueryResult> results = new ArrayList<>();
        for (Topic topic : topics) {
            HashMap<String, String> splitNarrative = splitNarrativeIntoRelevantAndIrrelevantParts(topic.narrative);
            String relevantNarr = splitNarrative.get("relevant").trim();
            String irrelevantNarr = splitNarrative.get("irrelevant").trim();

            BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();

            if (!topic.getTitle().isEmpty()) {
                Query titleQuery = parser.parse(QueryParser.escape(topic.getTitle()));
                Query descriptionQuery = parser.parse(QueryParser.escape(topic.getDescription()));

                if (!relevantNarr.isEmpty()) {
                    Query narrativeQuery = parser.parse(QueryParser.escape(relevantNarr));
                    booleanQuery.add(new BoostQuery(narrativeQuery, 1.2f), BooleanClause.Occur.SHOULD);
                }
                if (!irrelevantNarr.isEmpty()) {
                    Query irrelevantNarrativeQuery = parser.parse(QueryParser.escape(irrelevantNarr));
                    booleanQuery.add(new BoostQuery(irrelevantNarrativeQuery, 2f), BooleanClause.Occur.FILTER);
                }

                booleanQuery.add(new BoostQuery(titleQuery, 4f), BooleanClause.Occur.SHOULD);
                booleanQuery.add(new BoostQuery(descriptionQuery, 1.7f), BooleanClause.Occur.SHOULD);

                ScoreDoc[] hits = isearcher.search(booleanQuery.build(), MAX_QUERY_RESULTS).scoreDocs;

                for (int i = 0; i < hits.length; i++) {
                    Document hitDoc = isearcher.doc(hits[i].doc);
                    QueryResult result = new QueryResult(
                            String.valueOf(topic.number),
                            hitDoc.get("DOCNO"),
                            i + 1,
                            hits[i].score,
                            analyzerEnum.getName()+"_"+similarityEnum.getName()
                    );
                    results.add(result);
                }
            }
        }
        outputResults(results, analyzerEnum, similarityEnum);

        // Clean up.
        ireader.close();
        indexDir.close();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        Util.printInfo("Finished evaluating index. Total time: " + totalTime + " ms");
    }

    private static HashMap<String, String> splitNarrativeIntoRelevantAndIrrelevantParts(String narrative) {
        StringBuilder relevantPart = new StringBuilder();
        StringBuilder irrelevantPart = new StringBuilder();
        HashMap<String, String> splitNarrative = new HashMap<>();

        BreakIterator sentenceIterator = BreakIterator.getSentenceInstance();
        sentenceIterator.setText(narrative);
        int startIndex = 0;

        while (sentenceIterator.next() != BreakIterator.DONE) {
            String sentence = narrative.substring(startIndex, sentenceIterator.current());

            if (!sentence.toLowerCase().contains("not relevant") && !sentence.toLowerCase().contains("irrelevant")) {
                relevantPart.append(cleanSentence(sentence, true));
            } else {
                irrelevantPart.append(cleanSentence(sentence, false));
            }
            startIndex = sentenceIterator.current();
        }

        splitNarrative.put("relevant", relevantPart.toString().trim());
        splitNarrative.put("irrelevant", irrelevantPart.toString().trim());
        return splitNarrative;
    }

    private static String cleanSentence(String sentence, boolean isRelevant) {
        if (isRelevant) {
            return sentence.replaceAll(
                    "a relevant document identifies|a relevant document could|a relevant document may|a relevant document must|a relevant document will|a document will|to be relevant|relevant documents|a document must|relevant|will contain|will discuss|will provide|must cite|must discuss",
                    "").trim();
        } else {
            return sentence.replaceAll(
                    "are also not relevant|are not relevant|are irrelevant|is not relevant|not|NOT|not discuss|not contain",
                    "").trim();
        }
    }

    private static void outputResults(ArrayList<QueryResult> results, AnalyzerType analyzerEnum, SimilarityType similarityEnum) throws IOException {
        String outputPath = OUTPUT_PATH + analyzerEnum.getName() + "_" + similarityEnum.getName() + "_" + EVALUATION_RESULT_NAME;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            for (QueryResult result : results) {
                writer.write(result.toString());
                writer.newLine();
            }
        }
    }
}
