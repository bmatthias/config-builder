package com.tngtech.configbuilder.annotation.typetransformer;

import com.google.common.collect.Sets;

import java.util.HashSet;

public class CharacterSeparatedStringToStringSetTransformer extends TypeTransformer<String, HashSet<String>> {

    @Override
    public HashSet<String> transform(String argument) {
        return Sets.newHashSet(argument.split((String)additionalOptions[0]));
    }
}
