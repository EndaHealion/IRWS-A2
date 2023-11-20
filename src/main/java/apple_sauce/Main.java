package apple_sauce;

import java.util.ArrayList;
import java.util.List;

import apple_sauce.models.BaselineIndexer;
import apple_sauce.models.FBISDoc;
import apple_sauce.models.FederalRegisterDoc;
import apple_sauce.models.FinancialTimesDoc;
import apple_sauce.models.LATimesDoc;
import apple_sauce.models.Topic;
import apple_sauce.parsers.*;

public class Main {
    public static void main(String[] args) throws Exception {
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

        BaselineIndexer.createIndex(fbisDocs, frDocs, ftDocs, laTimesDocs);
        BaselineIndexer.queryIndex(topics);

        // for (FBISDoc d : fbisDocs) {
        // d.print();
        // System.out.println();
        // }
        // for (LATimesDoc d : laTimesDocs) {
        // d.print();
        // System.out.println();
        // }
        // for (FinancialTimesDoc d : ftDocs) {
        // d.print();
        // System.out.println();
        // }
        // for (FederalRegisterDoc d : frDocs) {
        // d.print();
        // System.out.println();
        // }
        // SGMLNode root = SGMLParser.parseSGML("resources/dataset/latimes/la011890");
        // root.print();
    }
}
