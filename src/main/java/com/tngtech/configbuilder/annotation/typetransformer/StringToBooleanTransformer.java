package com.tngtech.configbuilder.annotation.typetransformer;

public class StringToBooleanTransformer implements ITypeTransformer<String, Boolean>{
    @Override
    public Boolean transform(String argument) {
        return Boolean.parseBoolean(argument);
    }
}
