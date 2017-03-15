package com.tngtech.configbuilder.annotation.typetransformer;

import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Set;

public class CharacterSeparatedStringToStringSetTransformer extends TypeTransformer<String, HashSet<String>> {

    @Override
    public HashSet<String> transform(String argument) {

        HashSet<String> collection = Sets.newHashSet();
        for (String value : argument.split((String)additionalOptions[0])) {
            collection.add(value);
        }
        return collection;
    }
}
