package com.tngtech.configbuilder.annotation.typetransformer;

import com.google.common.base.Joiner;

import java.util.Collection;

public class StringCollectionToCommaSeparatedStringTransformer extends ITypeTransformer<Collection<String>, String> {
    
    @Override
    public String transform(Collection<String> argument) {
        Joiner joiner = Joiner.on(",");
        return joiner.join(argument);
    }
}
