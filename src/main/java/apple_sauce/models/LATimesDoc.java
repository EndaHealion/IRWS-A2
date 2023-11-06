package apple_sauce.models;

public class LATimesDoc {
    String docNo = "";
    String date = "";
    String section = "";
    String headline = "";
    String text = "";

    public LATimesDoc() {
    }

    public LATimesDoc(String docNo, String date, String section, String headline, String text) {
        this.docNo = docNo;
        this.date = date;
        this.section = section;
        this.headline = headline;
        this.text = text;
    }
}
