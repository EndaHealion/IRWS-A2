package apple_sauce.models;

public class FinancialTimesDoc {
    String filename;
    String docNo;
    String date;
    String headline;
    String text;

    public FinancialTimesDoc() {
        this.filename = "";
        this.docNo = "";
        this.date = "";
        this.headline = "";
        this.text = "";
    }

    public FinancialTimesDoc(String filename, String docNo, String date, String headline, String text) {
        this.filename = filename;
        this.docNo = docNo;
        this.date = date;
        this.headline = headline;
        this.text = text;
    }

    public void print() {
        System.out.println("FILENAME: " + this.filename);
        System.out.println("DOCNO: " + this.docNo);
        System.out.println("DATE: " + this.date);
        System.out.println("HEADLINE: " + this.headline);
        System.out.println("TEXT: " + this.text);
    }
}
