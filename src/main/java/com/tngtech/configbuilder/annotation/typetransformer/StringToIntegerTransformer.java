package com.tngtech.configbuilder.annotation.typetransformer;

public class StringToIntegerTransformer extends ITypeTransformer<String, Integer> {
    @Override
    public Integer transform(String argument) {
        return Integer.parseInt(argument);
    }
}
