package apple_sauce.models;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
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

    public FBISDoc(String filename, String docNo, String headerTag, String author, String date, String text,
            ArrayList<String> titles) {
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

    public Document toDocument() {
        StringBuilder titles = new StringBuilder();
        for (String title : this.titles) {
            titles.append(title + " ");
        }
        String titlesStr = titles.toString();

        Document result = new Document();
        result.add(new StringField("FILENAME", this.filename, Field.Store.YES));
        result.add(new StringField("DOCNO", this.docNo, Field.Store.YES));
        result.add(new TextField("HEADER TAG", this.headerTag, Field.Store.YES));
        result.add(new TextField("AUTHOR", this.author, Field.Store.YES));
        result.add(new TextField("DATE", this.date, Field.Store.YES));
        result.add(new TextField("TITLES", titlesStr, Field.Store.YES));
        result.add(new TextField("TEXT", this.text, Field.Store.YES));
        return result;
    }
}
