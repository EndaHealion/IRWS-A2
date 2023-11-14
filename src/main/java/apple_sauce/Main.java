package apple_sauce;

import java.util.ArrayList;

import apple_sauce.models.FBISDoc;
import apple_sauce.models.FederalRegisterDoc;
import apple_sauce.models.FinancialTimesDoc;
import apple_sauce.models.LATimesDoc;
// import apple_sauce.models.Topic;
import apple_sauce.parsers.*;

public class Main {
    public static void main(String[] args) throws Exception {
        // for (Topic topic : TopicsParser.parseTopics("resources/topics")) {
        // System.out.println(topic);
        // }
        System.out.println("Starting parsing of LA Times...");
        ArrayList<LATimesDoc> laTimesDocs = LATimesParser.getDocInformation();
        System.out.println("LA Times finished parsing!");
        System.out.println("Starting parsing of Financial Times...");
        ArrayList<FinancialTimesDoc> ftDocs = FinancialTimesParser.getDocInformation();
        System.out.println("Financial Times finished parsing!");
        ArrayList<FederalRegisterDoc> frDocs = FRParser.getDocInformation();
        System.out.println("Federal Register finished parsing!");
        System.out.println("Starting parsing of FBIS...");
        ArrayList<FBISDoc> fbisDocs = FBISParser.getDocInformation();
        System.out.println("FBIS finished parsing!");

//        for (FBISDoc d : fbisDocs) {
//            d.print();
//            System.out.println();
//        }
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
