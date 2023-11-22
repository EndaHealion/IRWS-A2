package apple_sauce.models;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import apple_sauce.Util;
import apple_sauce.eNums.AnalyzerType;
import apple_sauce.eNums.SimilarityType;

public class CustomIndexer {
    public static final String INDEX_PATH = "index";
    public static final String OUTPUT_PATH = "output/";
    public static final String EVALUATION_RESULT_NAME = "eval_results.txt";
    public static final int MAX_QUERY_RESULTS = 1000;

    private static String topicToQueryString(Topic t) {
        StringBuilder builder = new StringBuilder();
        builder.append(t.description + " ");
        builder.append(t.narrative + " ");
        builder.append(t.title + " ");
        return builder.toString();
    }

        public static void createIndex(ArrayList<FBISDoc> fbisDocs, ArrayList<FederalRegisterDoc> frDocs,
            ArrayList<FinancialTimesDoc> ftDocs, ArrayList<LATimesDoc> latimesDocs, AnalyzerType analyzerEnum, SimilarityType similarityEnum) throws Exception {

        ArrayList<Document> documents = new ArrayList<Document>();

        Util.printInfo("Creating index...");
        Util.printInfo("Adding parsed documents to index.");

        for (FBISDoc d : fbisDocs) {
            documents.add(d.toDocument());
        }
        for (FederalRegisterDoc d : frDocs) {
            documents.add(d.toDocument());
        }
        for (FinancialTimesDoc d : ftDocs) {
            documents.add(d.toDocument());
        }
        for (LATimesDoc d : latimesDocs) {
            documents.add(d.toDocument());
        }

        // Configure index writer.
        Analyzer analyzer = analyzerEnum.getAnalyzer();
        Similarity similarity = similarityEnum.getSimilarity();
        Directory indexDir = FSDirectory.open(Paths.get(INDEX_PATH));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        config.setSimilarity(similarity);

        // Create index.
        Util.printInfo("Writing index to disk.");
        IndexWriter iwriter = new IndexWriter(indexDir, config);
        iwriter.addDocuments(documents);

        // Clean up.
        iwriter.close();
        indexDir.close();
        Util.printInfo("Finished creating index.");
    }

        public static void queryIndex(List<Topic> topics, AnalyzerType analyzerEnum, SimilarityType similarityEnum) throws Exception {
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
        MultiFieldQueryParser parser = new MultiFieldQueryParser(searchFields, analyzer);

        // Iterate over all topics and query the index.
        ArrayList<String> results = new ArrayList<String>();
        for (Topic topic : topics) {
            Query query = parser.parse(QueryParser.escape(topicToQueryString(topic)));
            ScoreDoc[] hits = isearcher.search(query, MAX_QUERY_RESULTS).scoreDocs;

            // Gather scores of results results.
            for (int i = 0; i < hits.length; i++) {
                Document hitDoc = isearcher.doc(hits[i].doc);
                StringBuilder resultBuilder = new StringBuilder();
                resultBuilder.append(topic.number);
                resultBuilder.append(" 0 ");
                resultBuilder.append(hitDoc.get("DOCNO"));
                resultBuilder.append(" " + (i + 1) + " ");
                resultBuilder.append(hits[i].score);
                resultBuilder.append(" " + analyzerEnum.getName());
                results.add(resultBuilder.toString());
            }
        }

        // Write results to file.
        BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_PATH + analyzerEnum.getName()+ "_" + similarityEnum.getName() + "_" + EVALUATION_RESULT_NAME));
        for (String line : results) {
            writer.write(line);
            writer.newLine();
        }

        // Clean up.
        writer.close();
        ireader.close();
        indexDir.close();
        Util.printInfo("Finished evaluating index...");
    }
}

