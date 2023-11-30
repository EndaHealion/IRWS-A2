package com.apple.sauce.models;

import com.apple.sauce.doccollections.DocCollection;

import java.util.List;

public record DocCollectionConf(
        String path,
        List<String> ignoreFileNames,
        DocCollection docCollection
) {
}
