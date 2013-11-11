package com.tngtech.configbuilder.annotation.typetransformer;

public class IntegerToDoubleTransformer extends ITypeTransformer<Integer, Double> {
    @Override
    public Double transform(Integer argument) {
        return (double) argument;
    }
}
