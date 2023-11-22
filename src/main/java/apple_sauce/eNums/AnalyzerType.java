package apple_sauce.eNums;

import apple_sauce.Util;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;
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

    public static AnalyzerType getAnalyzerTypeByChoice(Scanner scanner) {
        Util.printInfo("Please select the type of Analyzer:\n" +
                "1. Standard Analyzer\n" +
                "2. English Analyzer");

        while (true) {
            try {
                int choice = scanner.nextInt();
                if (choice == 1 || choice == 2) {
                    for (AnalyzerType type : values()) {
                        if (type.choice == choice) {
                            Util.printInfo("Selected " + type.name);
                            return type;
                        }
                    }
                } else {
                    Util.printError("Invalid choice. Please enter 1 or 2.");
                }
            } catch (InputMismatchException e) {
                Util.printError("Invalid input. Please enter a number.");
                scanner.next();
            }
        }
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public String getName() {
        return name;
    }
}
