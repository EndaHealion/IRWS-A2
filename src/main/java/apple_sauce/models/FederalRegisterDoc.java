package apple_sauce.models;

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
}
