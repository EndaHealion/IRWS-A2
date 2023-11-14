package apple_sauce.parsers;

import apple_sauce.models.LATimesDoc;

import java.io.File;
import java.util.ArrayList;

public class LATimesParser {
    public static ArrayList<LATimesDoc> getDocInformation() throws Exception {
        ArrayList<LATimesDoc> documents = new ArrayList<LATimesDoc>();

        String datasetDir = "resources/dataset/latimes/";
        File directory = new File(datasetDir);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new Exception("Invalid LA Times dataset directory.");
        }

        File[] files = directory.listFiles();
        for (File file : files) {
            // NOTE: All LA Times data files don't have an extension, so I want to filter
            // irrelevant files.
            if (!file.isFile() || file.getName().contains(".")) {
                continue;
            }

            String filename = datasetDir + file.getName();
            String content = SGMLParser.readEntireFile(filename);
            SGMLNode docRoot = SGMLParser.parseSGML(content);
            ArrayList<SGMLNode> sgmlDocs = docRoot.children;
            for (SGMLNode c : sgmlDocs) {
                ArrayList<SGMLNode> docValues = c.children;
                SGMLNode docNoNode = SGMLParser.seekTag(docValues, "DOCNO");
                SGMLNode dateNode = SGMLParser.seekTag(docValues, "DATE");
                SGMLNode sectionNode = SGMLParser.seekTag(docValues, "SECTION");
                SGMLNode headlineNode = SGMLParser.seekTag(docValues, "HEADLINE");
                SGMLNode textNode = SGMLParser.seekTag(docValues, "TEXT");

                String docNo = (docNoNode != null) ? docNoNode.toStringValue() : "";
                String date = (dateNode != null) ? dateNode.toStringValue() : "";
                String section = (sectionNode != null) ? sectionNode.toStringValue() : "";
                String headline = (headlineNode != null) ? headlineNode.toStringValue() : "";
                String text = (textNode != null) ? textNode.toStringValue() : "";

                LATimesDoc doc = new LATimesDoc(filename, docNo, date, section, headline, text);
                documents.add(doc);
            }
        }

        return documents;
    }
}
