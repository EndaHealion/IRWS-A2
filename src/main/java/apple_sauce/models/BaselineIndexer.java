package apple_sauce.models;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
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

public class BaselineIndexer {
    public static final String INDEX_PATH = "index";
    public static final String OUTPUT_PATH = "output/";
    public static final String EVALUATION_RESULT_NAME = "eval_results.txt";
    public static final int MAX_QUERY_RESULTS = 1000;

    private static String topicToQueryString(Topic t) {
        return t.description + " " +
                t.narrative + " " +
                t.title + " ";
    }

    public static void createIndex(ArrayList<FBISDoc> fbisDocs, ArrayList<FederalRegisterDoc> frDocs,
            ArrayList<FinancialTimesDoc> ftDocs, ArrayList<LATimesDoc> latimesDocs) throws Exception {
        long startTime = System.currentTimeMillis();
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
        Analyzer analyzer = new EnglishAnalyzer();
        Similarity similarity = new BM25Similarity();
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
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        Util.printInfo("Finished creating index. Total time: " + totalTime + " ms");
    }

    public static void queryIndex(List<Topic> topics) throws Exception {
        long startTime = System.currentTimeMillis();
        Util.printInfo("Evaluating index...");
        // Setup index reader and searcher.
        Analyzer analyzer = new EnglishAnalyzer();
        Similarity similarity = new BM25Similarity();
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

        // Iterate over all topics and query the index.
        HashMap<String, Float> fieldWeights = new HashMap<String, Float>();
        fieldWeights.put("DATE", 1.0f);
        fieldWeights.put("SECTION", 1.0f);
        fieldWeights.put("HEADLINE", 1.0f);
        fieldWeights.put("TEXT", 18.0f);
        fieldWeights.put("TITLES", 0.0f);
        fieldWeights.put("AUTHOR", 1.0f);
        fieldWeights.put("HEADER TAG", 1.0f);
        MultiFieldQueryParser parser = new MultiFieldQueryParser(searchFields, analyzer, fieldWeights);

        ArrayList<String> results = new ArrayList<String>();
        for (Topic topic : topics) {
            String queryString = QueryParser.escape(topicToQueryString(topic));
            Query query = parser.parse(queryString);
            ScoreDoc[] hits = isearcher.search(query, MAX_QUERY_RESULTS).scoreDocs;

            // Gather scores of results.
            for (int i = 0; i < hits.length; i++) {
                Document hitDoc = isearcher.doc(hits[i].doc);
                String resultBuilder = topic.number +
                        " 0 " +
                        hitDoc.get("DOCNO") +
                        " " + (i + 1) + " " +
                        hits[i].score +
                        " STANDARD";
                results.add(resultBuilder);
            }
        }

        // Write results to file.
        String outputPath = OUTPUT_PATH + "baseline_EnglishAnalyzer_BM25Similarity_" + EVALUATION_RESULT_NAME;
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));

        for (String line : results) {
            writer.write(line);
            writer.newLine();
        }

        // Clean up.
        writer.close();
        ireader.close();
        indexDir.close();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        Util.printInfo("Finished evaluating index. Total time: " + totalTime + " ms");
    }
}
