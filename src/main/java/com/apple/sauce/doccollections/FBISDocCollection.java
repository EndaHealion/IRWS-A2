package com.apple.sauce.doccollections;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FBISDocCollection {
    public static Document toDoc(Element jsoupDoc) {
        Document luceneDoc = new Document();

        luceneDoc.add(new StringField("docno", trimData(jsoupDoc, FBISTags.DOCNO), Field.Store.YES));
        luceneDoc.add(new StringField("ht", trimData(jsoupDoc, FBISTags.HT), Field.Store.YES));
        luceneDoc.add(new StringField("h2", trimData(jsoupDoc, FBISTags.H2), Field.Store.YES));
        luceneDoc.add(new StringField("date", trimData(jsoupDoc, FBISTags.DATE1), Field.Store.YES));
        luceneDoc.add(new StringField("h3", trimData(jsoupDoc, FBISTags.H3), Field.Store.YES));
        luceneDoc.add(new TextField("headline", trimData(jsoupDoc, FBISTags.TI), Field.Store.YES));
        luceneDoc.add(new TextField("text", trimData(jsoupDoc, FBISTags.TEXT), Field.Store.YES));

        return luceneDoc;
    }

    private static String trimData(Element doc, FBISTags tag) {
        Elements element = doc.getElementsByTag(tag.name());
        // Elements tmpElement = element.clone();
        // remove any nested
        removeNestedTags(element, tag);
        String data = element.toString();

        // remove any instance of "\n"
        if (data.contains("\n"))
            data = data.replaceAll("\\s+", " ");
        // remove start and end tags
        if (data.contains(("<" + tag.name() + ">").toLowerCase()))
            data = data.replaceAll("<" + tag.name().toLowerCase() + ">", "");
        if (data.contains(("</" + tag.name() + ">").toLowerCase()))
            data = data.replaceAll("</" + tag.name().toLowerCase() + ">", "");

        return data.strip();
    }

    private static void removeNestedTags(Elements element, FBISTags currTag) {
        for (final FBISTags tag : FBISTags.values()) {
            if (!tag.equals(currTag)
                    && element.toString().contains("<" + tag.name().toLowerCase() + ">")
                    && element.toString().contains("</" + tag.name().toLowerCase() + ">")) {
                element.select(tag.toString()).remove();
            }
        }
    }

    enum FBISTags {
        DOCNO("<DOCNO>"),
        HT("<HT>"),
        HEADER("<HEADER>"),
        H2("<H2>"),
        DATE1("<DATE1>"),
        H3("<H3>"),
        TI("<TI>"),
        TEXT("<TEXT>");

        final String tag;

        FBISTags(String tag) {
            this.tag = tag;
        }
    }
}
