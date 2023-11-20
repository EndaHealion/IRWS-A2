package apple_sauce.models;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

public class LATimesDoc {
    String filename = "";
    String docNo = "";
    String date = "";
    String section = "";
    String headline = "";
    String text = "";

    public LATimesDoc() {
        this.filename = "";
        this.docNo = "";
        this.date = "";
        this.section = "";
        this.headline = "";
        this.text = "";
    }

    public LATimesDoc(String filename, String docNo, String date, String section, String headline, String text) {
        this.filename = filename;
        this.docNo = docNo;
        this.date = date;
        this.section = section;
        this.headline = headline;
        this.text = text;
    }

    public void print() {
        System.out.println("FILENAME: " + this.filename);
        System.out.println("DOCNO: " + this.docNo);
        System.out.println("DATE: " + this.date);
        System.out.println("SECTION: " + this.section);
        System.out.println("HEADLINE: " + this.headline);
        System.out.println("TEXT: " + this.text);
    }

    public Document toDocument() {
        Document result = new Document();
        result.add(new StringField("FILENAME", this.filename, Field.Store.YES));
        result.add(new StringField("DOCNO", this.docNo, Field.Store.YES));
        result.add(new TextField("DATE", this.date, Field.Store.YES));
        result.add(new TextField("SECTION", this.section, Field.Store.YES));
        result.add(new TextField("HEADLINE", this.headline, Field.Store.YES));
        result.add(new TextField("TEXT", this.text, Field.Store.YES));
        return result;
    }
}
