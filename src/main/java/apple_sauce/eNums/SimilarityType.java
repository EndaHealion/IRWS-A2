package apple_sauce.eNums;

import apple_sauce.Util;
import org.apache.lucene.search.similarities.*;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public enum SimilarityType {
    CLASSIC(1, new ClassicSimilarity(), "Classic (VSM)"),
    BM25(2, new BM25Similarity(), "BM25"),
    BOOLEAN(3, new BooleanSimilarity(), "Boolean"),
    LMDIRICHLET(4, new LMDirichletSimilarity(), "LMDirichlet"),
    IBS(5, new IBSimilarity(new DistributionLL(), new LambdaDF(), new NormalizationH1()) , "IBS");


    private final int choice;
    private final Similarity similarity;
    private final String name;

    SimilarityType(int choice, Similarity similarity, String name) {
        this.choice = choice;
        this.similarity = similarity;
        this.name = name;
    }

    public static SimilarityType getSimilarityTypeByChoice(Scanner scanner) {
        Util.printInfo("Please select the type of Similarity:\n" +
                "1. Classic (VSM)\n" +
                "2. BM25\n" +
                "3. Boolean\n" +
                "4. LMDirichlet\n" +
                "5. IBS");

        while (true) {
            try {
                int choice = scanner.nextInt();
                if (choice >= 1 && choice <= 5) {
                    for (SimilarityType type : values()) {
                        if (type.choice == choice) {
                            Util.printInfo("Selected " + type.name + " for scoring.");
                            return type;
                        }
                    }
                } else {
                    Util.printError("Invalid choice. Please enter a number between 1 and 5.");
                }
            } catch (InputMismatchException e) {
                Util.printError("Invalid input. Please enter a number.");
                scanner.next();
            }
        }
    }

    public Similarity getSimilarity() {
        return similarity;
    }

    public String getName() {
        return name;
    }
}
