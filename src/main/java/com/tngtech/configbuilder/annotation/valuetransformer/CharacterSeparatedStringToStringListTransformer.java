package com.tngtech.configbuilder.annotation.valuetransformer;

import com.google.common.collect.Lists;

import java.util.ArrayList;

public class CharacterSeparatedStringToStringListTransformer extends ValueTransformer<String, ArrayList<String>> {

    @Override
    public ArrayList<String> transform(String argument) {

        ArrayList<String> collection = Lists.newArrayList();
        for (String value : argument.split((String)additionalOptions[0])) {
            collection.add(value);
        }
        return collection;
    }
}
