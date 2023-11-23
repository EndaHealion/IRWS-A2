package apple_sauce;

import apple_sauce.eNums.AnalyzerType;
import apple_sauce.eNums.SimilarityType;
import apple_sauce.models.*;
import apple_sauce.parsers.TopicsParser;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        Util.printInfo("Running Apple Sauce in directory: " + new File(".").getAbsolutePath());
        AnalyzerType analyzerEnum = AnalyzerType.getAnalyzerTypeByChoice(scanner);
        SimilarityType similarityEnum = SimilarityType.getSimilarityTypeByChoice(scanner);
        scanner.close();

        Util.printInfo("Parsing topics...");
        List<Topic> topics = TopicsParser.parseTopics("resources/topics");
        Util.printInfo("Topics finished parsing!");

        CustomIndexer.createIndex(analyzerEnum, similarityEnum);
        CustomIndexer.queryIndex(topics, analyzerEnum, similarityEnum);
    }
}
