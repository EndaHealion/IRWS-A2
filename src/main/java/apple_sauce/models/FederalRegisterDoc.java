package apple_sauce.models;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

public class FederalRegisterDoc {
    String filename = "";
    String docNo = "";
    String parent = "";
    String text = "";
    String date = "";

    public FederalRegisterDoc() {
        this.filename = "";
        this.docNo = "";
        this.parent = "";
        this.text = "";
        this.date = "";
    }

    public FederalRegisterDoc(String filename, String docNo, String parent, String text, String date) {
        this.filename = filename;
        this.docNo = docNo;
        this.parent = parent;
        this.text = text;
        this.date = date;
    }

    public void print() {
        System.out.println("FILENAME: " + this.filename);
        System.out.println("DOCNO: " + this.docNo);
        System.out.println("PARENT: " + this.parent);
        System.out.println("TEXT: " + this.text);
        System.out.println("DATE: " + this.date);
    }

    public Document toDocument() {
        Document result = new Document();
        result.add(new StringField("FILENAME", this.filename, Field.Store.YES));
        result.add(new StringField("DOCNO", this.docNo, Field.Store.YES));
        result.add(new TextField("PARENT", this.parent, Field.Store.YES));
        result.add(new TextField("TEXT", this.text, Field.Store.YES));
        result.add(new TextField("DATE", this.date, Field.Store.YES));
        return result;
    }
}
