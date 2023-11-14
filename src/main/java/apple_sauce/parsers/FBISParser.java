package apple_sauce.parsers;

import apple_sauce.models.FBISDoc;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FBISParser {

    public static ArrayList<FBISDoc> getDocInformation() throws Exception {
        ArrayList<FBISDoc> documents = new ArrayList<>();

        String datasetDir = "resources/dataset/fbis/";
        File mainDirectory = new File(datasetDir);
        if (!mainDirectory.exists() || !mainDirectory.isDirectory()) {
            throw new Exception("Invalid FBIS dataset directory.");
        }

        File[] files = mainDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.isFile() || file.getName().contains(".")) {
                    continue;
                }

                String filename = file.getPath();
                String content = SGMLParser.readEntireFile(filename);
                SGMLNode docRoot = SGMLParser.parseSGML(content);
                ArrayList<SGMLNode> sgmlDocs = docRoot.children;

                for (SGMLNode c : sgmlDocs) {
                    ArrayList<SGMLNode> docValues = c.children;
                    SGMLNode docNoNode = SGMLParser.seekTag(docValues, "DOCNO");
                    SGMLNode htNode = SGMLParser.seekTag(docValues, "HT");
                    SGMLNode auNode = SGMLParser.seekTag(docValues, "AU");
                    SGMLNode date1Node = SGMLParser.seekTag(docValues, "DATE1");
                    SGMLNode textNode = SGMLParser.seekTag(docValues, "TEXT");
                    List<SGMLNode> titleNodes = SGMLParser.seekAllTags(docValues, "TI");

                    String docNo = (docNoNode != null) ? docNoNode.toStringValue() : "";
                    String ht = (htNode != null) ? htNode.toStringValue() : "";
                    String au = (auNode != null) ? auNode.toStringValue() : "";
                    String date1 = (date1Node != null) ? date1Node.toStringValue() : "";
                    String text = (textNode != null) ? textNode.toStringValue() : "";
                    ArrayList<String> titles = new ArrayList<>();
                    for (SGMLNode titleNode : titleNodes) {
                        titles.add(titleNode.toStringValue());
                    }

                    FBISDoc doc = new FBISDoc(filename, docNo, ht, au, date1, text, titles);
                    documents.add(doc);
                }
            }
        }

        return documents;
    }
}
