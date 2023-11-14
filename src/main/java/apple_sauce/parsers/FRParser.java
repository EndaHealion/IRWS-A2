package apple_sauce.parsers;

import java.io.File;
import java.util.ArrayList;

import apple_sauce.models.FederalRegisterDoc;

public class FRParser {

    public static String parseFRDoc(String filename, String content) {
        if (filename.contains("fr94")) {
            content = content.replaceAll("<!--.+-->", "");
            content = content.replaceAll("&.+;", "").replaceAll("(?m)^[ \t]*\n", "");
            return content;
        }
        else {
            return content;
        }
    }

    public static ArrayList<FederalRegisterDoc> getDocInformation() throws Exception {
        ArrayList<FederalRegisterDoc> documents = new ArrayList<>();

        String datasetDir = "resources/dataset/fr94/";
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
                        if (!file.isFile() || file.getName().contains("read")) {
                            continue;
                        }

                        String filename = subDirectory.getPath() + "/" + file.getName();
                        SGMLNode docRoot = SGMLParser.parseSGML(filename);
                        ArrayList<SGMLNode> sgmlDocs = docRoot.children;

                        for (SGMLNode c : sgmlDocs) {
                            ArrayList<SGMLNode> docValues = c.children;
                            SGMLNode docNoNode = SGMLParser.seekTag(docValues, "DOCNO");
                            SGMLNode dateNode = SGMLParser.seekTag(docValues, "DATE");
                            SGMLNode parentNode = SGMLParser.seekTag(docValues, "PARENT");
                            SGMLNode textNode = SGMLParser.seekTag(docValues, "TEXT");

                            String docNo = (docNoNode != null) ? docNoNode.toStringValue() : "";
                            String date = (dateNode != null) ? dateNode.toStringValue() : "";
                            String parent = (parentNode != null) ? parentNode.toStringValue() : "";
                            String text = (textNode != null) ? textNode.toStringValue() : "";

                            FederalRegisterDoc doc = new FederalRegisterDoc(filename, docNo, parent, text, date);
                            documents.add(doc);
                        }
                    }
                }
            }
        }
        return documents;
    }
}
