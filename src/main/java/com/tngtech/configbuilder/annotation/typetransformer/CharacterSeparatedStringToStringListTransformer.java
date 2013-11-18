package com.tngtech.configbuilder.annotation.typetransformer;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CharacterSeparatedStringToStringListTransformer extends TypeTransformer<String, ArrayList<String>> {

    @Override
    public ArrayList<String> transform(String argument) {

        ArrayList<String> collection = Lists.newArrayList();
        for (String value : argument.split((String)additionalOptions[0])) {
            collection.add(value);
        }
        return collection;
    }
}
