package apple_sauce.parsers;

import apple_sauce.models.FinancialTimesDoc;

import java.io.File;
import java.util.ArrayList;

public class FinancialTimesParser {
    public static ArrayList<FinancialTimesDoc> getDocInformation() throws Exception {
        ArrayList<FinancialTimesDoc> documents = new ArrayList<>();

        String datasetDir = "resources/dataset/ft/";
        File mainDirectory = new File(datasetDir);
        if (!mainDirectory.exists() || !mainDirectory.isDirectory()) {
            throw new Exception("Invalid Financial Times dataset directory.");
        }

        File[] subDirectories = mainDirectory.listFiles(File::isDirectory);

        if (subDirectories != null) {
            for (File subDirectory : subDirectories) {
                File[] files = subDirectory.listFiles();

                if (files != null) {
                    for (File file : files) {
                        if (!file.isFile() || file.getName().contains(".")) {
                            continue;
                        }

                        String filename = subDirectory.getPath() + "/" + file.getName();
                        SGMLNode docRoot = SGMLParser.parseSGML(filename);
                        ArrayList<SGMLNode> sgmlDocs = docRoot.children;

                        for (SGMLNode c : sgmlDocs) {
                            ArrayList<SGMLNode> docValues = c.children;
                            SGMLNode docNoNode = SGMLParser.seekTag(docValues, "DOCNO");
                            SGMLNode dateNode = SGMLParser.seekTag(docValues, "DATE");
                            SGMLNode headlineNode = SGMLParser.seekTag(docValues, "HEADLINE");
                            SGMLNode textNode = SGMLParser.seekTag(docValues, "TEXT");

                            String docNo = (docNoNode != null) ? docNoNode.toStringValue() : "";
                            String date = (dateNode != null) ? dateNode.toStringValue() : "";
                            String headline = (headlineNode != null) ? headlineNode.toStringValue() : "";
                            String text = (textNode != null) ? textNode.toStringValue() : "";

                            FinancialTimesDoc doc = new FinancialTimesDoc(filename, docNo, date, headline, text);
                            documents.add(doc);
                        }
                    }
                }
            }
        }

        return documents;
    }
}
