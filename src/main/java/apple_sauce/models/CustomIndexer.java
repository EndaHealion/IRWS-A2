package apple_sauce.models;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import apple_sauce.parsers.FBISParser;
import apple_sauce.parsers.FRParser;
import apple_sauce.parsers.FinancialTimesParser;
import apple_sauce.parsers.LATimesParser;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
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
    private static final int NUM_THREADS = 4;
    private static String topicToQueryString(Topic t) {
        StringBuilder builder = new StringBuilder();
        builder.append(t.description + " ");
        builder.append(t.narrative + " ");
        builder.append(t.title + " ");
        return builder.toString();
    }

    public static void createIndex(AnalyzerType analyzerEnum, SimilarityType similarityEnum) throws Exception {
        long startTime = System.currentTimeMillis();
        Util.printInfo("Creating index using multi-threading...");

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        executor.submit(() -> {
            try {
                indexDocumentsInSeparateDirectory(LATimesParser.getDocInformation(), "latimesIndex", analyzerEnum, similarityEnum);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        executor.submit(() -> {
            try {
                indexDocumentsInSeparateDirectory(FinancialTimesParser.getDocInformation(), "ftIndex", analyzerEnum, similarityEnum);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        executor.submit(() -> {
            try {
                indexDocumentsInSeparateDirectory(FRParser.getDocInformation(), "frIndex", analyzerEnum, similarityEnum);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        executor.submit(() -> {
            try {
                indexDocumentsInSeparateDirectory(FBISParser.getDocInformation(), "fbisIndex", analyzerEnum, similarityEnum);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        Util.printInfo("Finished creating separate indexes. Merging...");
        mergeIndexes(new String[] {"latimesIndex", "ftIndex", "frIndex", "fbisIndex"}, INDEX_PATH, analyzerEnum);

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        Util.printInfo("Finished creating index. Total time: " + totalTime + " ms");
        Util.printInfo("Total indexing time: " + totalTime + " ms");
    }

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
        fieldWeights.put("TITLES", 0.0f);
        fieldWeights.put("AUTHOR", 1.0f);
        fieldWeights.put("HEADER TAG", 1.0f);
        MultiFieldQueryParser parser = new MultiFieldQueryParser(searchFields, analyzer, fieldWeights);

        ArrayList<QueryResult> results = new ArrayList<>();
        for (Topic topic : topics) {
            String queryString = QueryParser.escape(topicToQueryString(topic));
            Query query = parser.parse(queryString);
            ScoreDoc[] hits = isearcher.search(query, MAX_QUERY_RESULTS).scoreDocs;

            for (int i = 0; i < hits.length; i++) {
                Document hitDoc = isearcher.doc(hits[i].doc);
                QueryResult result = new QueryResult(
                        String.valueOf(topic.number),
                        hitDoc.get("DOCNO"),
                        i + 1,
                        hits[i].score,
                        analyzerEnum.getName()
                );
                results.add(result);
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

    private static ArrayList<Document> convertToDocuments(ArrayList<?> docs) {
        ArrayList<Document> documents = new ArrayList<>();
        for (Object doc : docs) {
            if (doc instanceof FBISDoc) {
                documents.add(((FBISDoc) doc).toDocument());
            } else if (doc instanceof FederalRegisterDoc) {
                documents.add(((FederalRegisterDoc) doc).toDocument());
            } else if (doc instanceof FinancialTimesDoc) {
                documents.add(((FinancialTimesDoc) doc).toDocument());
            } else if (doc instanceof LATimesDoc) {
                documents.add(((LATimesDoc) doc).toDocument());
            }
        }
        return documents;
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

    private static void indexDocumentsInSeparateDirectory(ArrayList<?> docs, String indexPath, AnalyzerType analyzerEnum, SimilarityType similarityEnum) throws IOException {
        ArrayList<Document> documents = convertToDocuments(docs);
        Util.printInfo("Opening directory for index at: " + indexPath);

        Directory indexDir = FSDirectory.open(Paths.get(indexPath));
        IndexWriterConfig config = new IndexWriterConfig(analyzerEnum.getAnalyzer());
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        config.setSimilarity(similarityEnum.getSimilarity());

        Util.printInfo("Configuring IndexWriter for: " + indexPath);
        IndexWriter iwriter = new IndexWriter(indexDir, config);

        Util.printInfo("Starting to index " + documents.size() + " documents in " + indexPath);
        for (Document doc : documents) {
            iwriter.addDocument(doc);
        }

        Util.printInfo("Indexing completed for " + indexPath);
        iwriter.close();
        indexDir.close();
        Util.printInfo("Closed index directory for " + indexPath);
    }

    private static void mergeIndexes(String[] indexPaths, String mergedIndexPath, AnalyzerType analyzerEnum) throws IOException {
        Directory mergedIndexDir = FSDirectory.open(Paths.get(mergedIndexPath));
        IndexWriterConfig config = new IndexWriterConfig(analyzerEnum.getAnalyzer());
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter iwriter = new IndexWriter(mergedIndexDir, config);

        for (String indexPath : indexPaths) {
            Directory indexDir = FSDirectory.open(Paths.get(indexPath));
            iwriter.addIndexes(indexDir);
            indexDir.close();
        }

        iwriter.close();
    }

}