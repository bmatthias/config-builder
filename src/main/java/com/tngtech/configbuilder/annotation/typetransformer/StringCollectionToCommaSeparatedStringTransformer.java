package com.tngtech.configbuilder.annotation.typetransformer;

import com.google.common.base.Joiner;

import java.util.Collection;

public class StringCollectionToCommaSeparatedStringTransformer extends TypeTransformer<Collection<String>, String> {
    
    @Override
    public String transform(Collection<String> argument) {
        Joiner joiner = Joiner.on((String)additionalOptions[0]);
        return joiner.join(argument);
    }
}
