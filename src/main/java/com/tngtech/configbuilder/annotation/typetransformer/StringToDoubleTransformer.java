package com.tngtech.configbuilder.annotation.typetransformer;

public class StringToDoubleTransformer extends ITypeTransformer<String, Double> {
    @Override
    public Double transform(String argument) {
        return Double.parseDouble(argument);
    }
}
