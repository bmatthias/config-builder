package com.tngtech.configbuilder.annotation.typetransformer;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

public class CommaSeparatedStringToStringCollectionTransformer implements ITypeTransformer<String, Collection<String>> {

    @Override
    public Collection<String> transform(String argument) {

        List<String> collection = Lists.newArrayList();
        for (String value : argument.split(",")) {
            collection.add(value);
        }
        return collection;
    }
}
