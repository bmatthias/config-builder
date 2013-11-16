package com.tngtech.configbuilder.annotation.typetransformer;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CharacterSeparatedStringToStringListTransformer extends TypeTransformer<String, List<String>> {

    @Override
    public List<String> transform(String argument) {

        ArrayList<String> collection = Lists.newArrayList();
        for (String value : argument.split(",")) {
            collection.add(value);
        }
        return collection;
    }
}
