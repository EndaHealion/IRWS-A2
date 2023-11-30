package apple_sauce.models;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import apple_sauce.eNums.*;
import apple_sauce.parsers.*;
import apple_sauce.Util;

public class CustomIndexer {
    public static final String INDEX_PATH = "index";

    private static final int NUM_THREADS = 4;

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
        mergeIndexes(new String[] {"latimesIndex", "ftIndex", "frIndex", "fbisIndex"}, analyzerEnum);

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        Util.printInfo("Finished creating index. Total time: " + totalTime + " ms");
        Util.printInfo("Total indexing time: " + totalTime + " ms");
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

    private static void mergeIndexes(String[] indexPaths, AnalyzerType analyzerEnum) throws IOException {
        Directory mergedIndexDir = FSDirectory.open(Paths.get(CustomIndexer.INDEX_PATH));
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