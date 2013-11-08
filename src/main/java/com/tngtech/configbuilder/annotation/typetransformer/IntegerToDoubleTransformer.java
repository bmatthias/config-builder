package com.tngtech.configbuilder.annotation.typetransformer;

public class IntegerToDoubleTransformer implements ITypeTransformer<Integer, Double> {
    @Override
    public Double transform(Integer argument) {
        return (double) argument;
    }
}
