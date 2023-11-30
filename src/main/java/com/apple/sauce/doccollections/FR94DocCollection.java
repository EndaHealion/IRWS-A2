package com.apple.sauce.doccollections;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.jsoup.nodes.Element;

import java.util.Arrays;
import java.util.List;

public class FR94DocCollection {
    public static Document toDoc(Element jsoupDoc) {
        final List<String> removeTags = Arrays.asList("ADDRESS", "SIGNER", "SIGNJOB", "BILLING", "FRFILING", "DATE", "RINDOCK");
        for (final String tag : removeTags) {
            jsoupDoc.select(tag).remove();
        }

        final String docno = jsoupDoc.select("DOCNO").text();
        final String text = jsoupDoc.select("TEXT").text();
        final String title = jsoupDoc.select("DOCTITLE").text();

        Document luceneDoc = new Document();
        luceneDoc.add(new StringField("docno", docno, Field.Store.YES));
        luceneDoc.add(new TextField("text", text, Field.Store.YES));
        luceneDoc.add(new TextField("headline", title, Field.Store.YES));
        return luceneDoc;
    }
}
