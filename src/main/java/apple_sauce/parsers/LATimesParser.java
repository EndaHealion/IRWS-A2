package apple_sauce.parsers;

import apple_sauce.models.LATimesDoc;

import java.io.File;
import java.util.ArrayList;

public class LATimesParser {
    public static ArrayList<LATimesDoc> getDocInformation() throws Exception {
        ArrayList<LATimesDoc> results = new ArrayList<LATimesDoc>();

        String datasetDir = "resources/dataset/latimes";
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

        }

        return results;
    }
}
