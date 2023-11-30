package com.apple.sauce.doccollections;

import org.apache.lucene.document.Document;
import org.jsoup.nodes.Element;
public interface DocCollection {
    Document toDoc(Element jsoupDoc);
}
