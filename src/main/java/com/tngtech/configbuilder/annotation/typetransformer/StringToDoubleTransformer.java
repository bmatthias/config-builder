package com.tngtech.configbuilder.annotation.typetransformer;

public class StringToDoubleTransformer implements ITypeTransformer<String, Double> {
    @Override
    public Double transform(String argument) {
        return Double.parseDouble(argument);
    }
}
