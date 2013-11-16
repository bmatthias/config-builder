package com.tngtech.configbuilder.annotation.typetransformer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CharacterSeparatedStringToStringSetTransformer extends TypeTransformer<String, Set<String>> {

    @Override
    public Set<String> transform(String argument) {

        HashSet<String> collection = Sets.newHashSet();
        for (String value : argument.split(",")) {
            collection.add(value);
        }
        return collection;
    }
}
