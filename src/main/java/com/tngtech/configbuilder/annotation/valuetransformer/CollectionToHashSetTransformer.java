package com.tngtech.configbuilder.annotation.valuetransformer;

import com.google.common.collect.Sets;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashSet;

public class CollectionToHashSetTransformer extends ValueTransformer<Collection,HashSet> {

    @Override
    public HashSet transform(Collection argument) {
        HashSet result = Sets.newHashSet();
        for(Object value : argument) {
            result.add(fieldValueTransformer.performApplicableTransformations(value, ((ParameterizedType) targetType).getActualTypeArguments()[0]));
        }
        return result;
    }
}
