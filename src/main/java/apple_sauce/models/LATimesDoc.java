package apple_sauce.models;

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
}
