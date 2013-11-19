package com.tngtech.configbuilder.annotation.valuetransformer;

import com.google.common.collect.Sets;

import java.util.HashSet;

public class CharacterSeparatedStringToStringSetTransformer extends ValueTransformer<String, HashSet<String>> {

    @Override
    public HashSet<String> transform(String argument) {

        HashSet<String> collection = Sets.newHashSet();
        for (String value : argument.split((String)additionalOptions[0])) {
            collection.add(value);
        }
        return collection;
    }
}
