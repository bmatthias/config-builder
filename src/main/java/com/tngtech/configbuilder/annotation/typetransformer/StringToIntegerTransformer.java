package com.tngtech.configbuilder.annotation.typetransformer;

public class StringToIntegerTransformer implements ITypeTransformer<String, Integer> {
    @Override
    public Integer transform(String argument) {
        return Integer.parseInt(argument);
    }
}
