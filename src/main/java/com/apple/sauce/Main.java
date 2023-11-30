package com.apple.sauce;

import com.apple.sauce.doccollections.FBISDocCollection;
import com.apple.sauce.doccollections.FR94DocCollection;
import com.apple.sauce.doccollections.FTDocCollection;
import com.apple.sauce.doccollections.LATimesDocCollection;
import com.apple.sauce.models.DocCollectionConf;
import com.apple.sauce.parsers.DocCollectionParser;
import com.apple.sauce.parsers.TopicsParser;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.similarities.BM25Similarity;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;


public class Main {
    public static void main(String[] args) throws IOException, ParserConfigurationException, ParseException {
        final Path path = Paths.get(indexDirectoryPath);
        if (Files.isDirectory(path)) {
            try (final Stream<Path> walk = Files.walk(path)) {
                walk.sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(file -> {
                            if (!file.delete()) {
                                System.err.println("Failed to delete " + file);
                            }
                        });
            }
        }

        for (DocCollectionConf docCollectionConf : docCollectionConfigs) {
            try (Analyzer analyzer = new EnglishAnalyzer()) {
                List<Document> docCollection = DocCollectionParser.parseDocCollection(docCollectionConf);
                AppleIndexBuilder.buildDocumentIndex(indexDirectoryPath, analyzer, docCollection);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        AppleIndexSearcher.searchDocumentIndex(
                new BM25Similarity(),
                new EnglishAnalyzer(),
                TopicsParser.parseTopics(topicsFilePath),
                resultFilePath,
                indexDirectoryPath
        );
    }

    private static final String topicsFilePath = "src/main/resources/topics";
    private static final String resultFilePath = "src/main/resources/result";
    private static final String indexDirectoryPath = "src/main/resources/indexes";
    private static final List<DocCollectionConf> docCollectionConfigs;

    static {
        docCollectionConfigs = List.of(
                new DocCollectionConf(
                        "src/main/resources/doc-collections/fbis",
                        List.of("readchg.txt", "readmefb.txt"),
                        FBISDocCollection::toDoc
                ),
                new DocCollectionConf(
                        "src/main/resources/doc-collections/fr94",
                        List.of("readchg", "readmefr"),
                        FR94DocCollection::toDoc
                ),
                new DocCollectionConf(
                        "src/main/resources/doc-collections/ft",
                        List.of("readfrcg", "readmeft"),
                        FTDocCollection::toDoc
                ),
                new DocCollectionConf(
                        "src/main/resources/doc-collections/latimes",
                        List.of("readchg.txt", "readmela.txt"),
                        LATimesDocCollection::toDoc
                )
        );
    }
}