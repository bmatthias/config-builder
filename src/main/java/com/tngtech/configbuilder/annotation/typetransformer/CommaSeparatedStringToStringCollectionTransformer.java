package com.tngtech.configbuilder.annotation.typetransformer;

import com.google.common.collect.Lists;

import java.util.ArrayList;

public class CommaSeparatedStringToStringCollectionTransformer extends TypeTransformer<String, ArrayList<String>> {

    @Override
    public ArrayList<String> transform(String argument) {

        ArrayList<String> collection = Lists.newArrayList();
        for (String value : argument.split(",")) {
            collection.add(value);
        }
        return collection;
    }
}
