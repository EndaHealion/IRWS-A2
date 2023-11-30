package com.apple.sauce.doccollections;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.jsoup.nodes.Element;

public class LATimesDocCollection {
    public static Document toDoc(Element luceneDoc) {
        final String docNo = luceneDoc.select("DOCNO").text();
        final String headline = luceneDoc.select("HEADLINE").select("P").text();
        final String text = luceneDoc.select("TEXT").select("P").text();

        Document document = new Document();
        document.add(new StringField("docno", docNo, Field.Store.YES));
        document.add(new TextField("headline", headline, Field.Store.YES));
        document.add(new TextField("text", text, Field.Store.YES));
        return document;
    }
}
