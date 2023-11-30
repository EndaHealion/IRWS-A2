package apple_sauce.models;

import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import apple_sauce.Util;

public class BaselineIndexer {
    public static final String INDEX_PATH = "index";

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
}
