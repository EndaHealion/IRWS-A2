package apple_sauce;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import apple_sauce.eNums.*;
import apple_sauce.models.*;
import apple_sauce.parsers.*;

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

        Util.printInfo("Parsing LA Times documents...");
        ArrayList<LATimesDoc> laTimesDocs = LATimesParser.getDocInformation();
        Util.printInfo("LA Times finished parsing!");

        Util.printInfo("Parsing Financial Times documents...");
        ArrayList<FinancialTimesDoc> ftDocs = FinancialTimesParser.getDocInformation();
        Util.printInfo("Financial Times finished parsing!");

        Util.printInfo("Parsing Federal Register documents...");
        ArrayList<FederalRegisterDoc> frDocs = FRParser.getDocInformation();
        Util.printInfo("Federal Register finished parsing!");

        Util.printInfo("Parsing FBIS documents...");
        ArrayList<FBISDoc> fbisDocs = FBISParser.getDocInformation();
        Util.printInfo("FBIS finished parsing!");

        CustomIndexer.createIndex(fbisDocs, frDocs, ftDocs, laTimesDocs, analyzerEnum, similarityEnum);
        CustomIndexer.queryIndex(topics, analyzerEnum, similarityEnum);
    }
}
