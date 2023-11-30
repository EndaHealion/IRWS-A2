package com.apple.sauce;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class AppleIndexBuilder {
    public static void buildDocumentIndex(
            String indexDirectoryPath,
            Analyzer analyzer,
            List<Document> documents
    ) throws IOException {
        final Directory directory = FSDirectory.open(Paths.get(indexDirectoryPath));
        final IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        final IndexWriter writer = new IndexWriter(directory, config);
        writer.addDocuments(documents);
        writer.close();
        directory.close();
    }
}