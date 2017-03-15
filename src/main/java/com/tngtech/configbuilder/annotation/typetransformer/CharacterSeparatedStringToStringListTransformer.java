package com.tngtech.configbuilder.annotation.typetransformer;

import com.google.common.collect.Lists;

import java.util.ArrayList;

public class CharacterSeparatedStringToStringListTransformer extends TypeTransformer<String, ArrayList<String>> {

    @Override
    public ArrayList<String> transform(String argument) {
        return Lists.newArrayList(argument.split((String)additionalOptions[0]));
    }
}
