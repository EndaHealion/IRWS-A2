package com.apple.sauce.parsers;

import com.apple.sauce.doccollections.*;
import com.apple.sauce.models.DocCollectionConf;
import me.tongfei.progressbar.ProgressBar;
import org.apache.lucene.document.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Stream;

public class DocCollectionParser {
    public static List<org.apache.lucene.document.Document> parseDocCollection(final DocCollectionConf docCollectionConf) throws IOException, InterruptedException {
        final List<String> pathNames = new ArrayList<>();
        final List<org.apache.lucene.document.Document> luceneDocs = new CopyOnWriteArrayList<>(); // This is a thread-safe list

        // Remove all files in the ignore list
        try (final Stream<Path> stream = Files.walk(Paths.get(docCollectionConf.path()))) {
            stream.filter(Files::isRegularFile)
                    .filter(c -> docCollectionConf.ignoreFileNames().stream().noneMatch(c.getFileName().toString()::contains))
                    .forEach(c -> pathNames.add(c.toString()));
        }

        final String fileName = docCollectionConf.path().substring(docCollectionConf.path().lastIndexOf('/') + 1);
        try (final var executor = Executors.newVirtualThreadPerTaskExecutor();
             final var progressBar = new ProgressBar(STR. "Parsing \{ fileName }" , pathNames.size())) {
            final CountDownLatch latch = new CountDownLatch(pathNames.size());
            for (final String pathName : pathNames) {
                executor.submit(() -> {
                    final org.jsoup.nodes.Document jsoupDoc;
                    try {
                        jsoupDoc = Jsoup.parse(new File(pathName));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    final Elements jsoupDocs = jsoupDoc.select("DOC");

                    for (final Element element : jsoupDocs) {
                        final org.apache.lucene.document.Document luceneDoc = docCollectionConf.docCollection().toDoc(element);
                        luceneDocs.add(luceneDoc);
                    }
                    progressBar.step();
                    latch.countDown();
                });
            }
            latch.await();
        }
        return luceneDocs;
    }
}
