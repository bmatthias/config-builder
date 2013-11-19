package com.tngtech.configbuilder.annotation.valuetransformer;

import com.google.common.base.Joiner;

import java.util.Collection;

public class StringCollectionToCommaSeparatedStringTransformer extends ValueTransformer<Collection<String>, String> {
    
    @Override
    public String transform(Collection<String> argument) {
        Joiner joiner = Joiner.on((String)additionalOptions[0]);
        return joiner.join(argument);
    }
}
