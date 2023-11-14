package apple_sauce.models;

import java.util.ArrayList;

public class FBISDoc {
    String filename;
    String docNo;
    String headerTag;
    String author;
    String date;
    String text;
    ArrayList<String> titles;

    public FBISDoc() {
        this.filename = "";
        this.docNo = "";
        this.headerTag = "";
        this.author = "";
        this.date = "";
        this.text = "";
        this.titles = new ArrayList<>();
    }

    public FBISDoc(String filename, String docNo, String headerTag, String author, String date, String text, ArrayList<String> titles) {
        this.filename = filename;
        this.docNo = docNo;
        this.headerTag = headerTag;
        this.author = author;
        this.date = date;
        this.text = text;
        this.titles = titles;
    }

    public void print() {
        System.out.println("FILENAME: " + this.filename);
        System.out.println("DOCNO: " + this.docNo);
        System.out.println("HEADER TAG: " + this.headerTag);
        System.out.println("AUTHOR: " + this.author);
        System.out.println("DATE: " + this.date);
        for (String title : titles) {
            System.out.println("TITLE: " + title);
        }
        System.out.println("TEXT: " + this.text);
    }
}
