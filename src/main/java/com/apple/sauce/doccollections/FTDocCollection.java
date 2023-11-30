package com.apple.sauce.doccollections;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FTDocCollection {
    public static Document toDoc(Element jsoupDoc) {
        Document luceneDoc = new Document();

        luceneDoc.add(new StringField("docno", trimData(jsoupDoc, FTTags.DOCNO), Field.Store.YES));
        luceneDoc.add(new StringField("profile", trimData(jsoupDoc, FTTags.PROFILE), Field.Store.YES));
        luceneDoc.add(new StringField("date", trimData(jsoupDoc, FTTags.DATE), Field.Store.YES));
        luceneDoc.add(new TextField("headline", trimData(jsoupDoc, FTTags.HEADLINE), Field.Store.YES));
        luceneDoc.add(new TextField("pub", trimData(jsoupDoc, FTTags.PUB), Field.Store.YES));
        luceneDoc.add(new TextField("page", trimData(jsoupDoc, FTTags.PAGE), Field.Store.YES));
        luceneDoc.add(new TextField("byline", trimData(jsoupDoc, FTTags.BYLINE), Field.Store.YES));
        luceneDoc.add(new TextField("dateline", trimData(jsoupDoc, FTTags.DATELINE), Field.Store.YES));
        luceneDoc.add(new TextField("text", trimData(jsoupDoc, FTTags.TEXT), Field.Store.YES));

        return luceneDoc;
    }

    private static String trimData(Element doc, FTTags tag) {
        Elements element = doc.getElementsByTag(tag.name());
        //Elements tmpElement = element.clone();
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

    private static void removeNestedTags(Elements element, FTTags currTag) {
        for (FTTags tag : FTTags.values()) {
            if (!tag.equals(currTag)
                    && element.toString().contains("<" + tag.name().toLowerCase() + ">")
                    && element.toString().contains("</" + tag.name().toLowerCase() + ">")) {
                element.select(tag.toString()).remove();
            }
        }
    }

    enum FTTags {
        DOCNO("<DOCNO>"),
        PROFILE("<PROFILE>"),
        HEADLINE("<HEADLINE>"),
        BYLINE("<BYLINE>"),
        DATELINE("<DATELINE>"),
        DATE("<DATE>"),
        PUB("<PUB>"),
        PAGE("<PAGE>"),
        TEXT("<TEXT>");

        final String tag;

        FTTags(String tag) {
            this.tag = tag;
        }
    }
}
