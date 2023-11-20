package apple_sauce.eNums;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.util.Scanner;

public enum AnalyzerType {
    STANDARD(1, new StandardAnalyzer(), "StandardAnalyzer"),
    ENGLISH(2, new EnglishAnalyzer(), "EnglishAnalyzer");

    private final int choice;
    private final Analyzer analyzer;
    private final String name;

    AnalyzerType(int choice, Analyzer analyzer, String name) {
        this.choice = choice;
        this.analyzer = analyzer;
        this.name = name;
    }

    public static AnalyzerType getAnalyzerTypeByChoice() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Please select the type of Analyzer:\n" +
                    "1. Standard Analyzer\n" +
                    "2. English Analyzer");

            int choice = 0;
            boolean validChoice = false;

            while (!validChoice) {
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    if (choice == 1 || choice == 2) {
                        validChoice = true;
                    } else {
                        System.out.println("Invalid choice. Please enter 1 or 2.");
                    }
                } else {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.next();
                }
            }

            for (AnalyzerType type : values()) {
                if (type.choice == choice) {
                    System.out.println("Selected " + type.name);
                    return type;
                }
            }
        }

        System.out.println("Default selected - Standard Analyzer");
        return STANDARD;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public String getName() {
        return name;
    }
}
