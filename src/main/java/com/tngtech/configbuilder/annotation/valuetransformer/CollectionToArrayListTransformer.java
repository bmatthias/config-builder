package com.tngtech.configbuilder.annotation.valuetransformer;


import com.google.common.collect.Lists;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;

public class CollectionToArrayListTransformer extends ValueTransformer<Collection,ArrayList> {

    @Override
    public ArrayList transform(Collection argument) {
        ArrayList result = Lists.newArrayList();
        for(Object value : argument) {
            result.add(fieldValueTransformer.performApplicableTransformations(value, ((ParameterizedType) targetType).getActualTypeArguments()[0]));
        }
        return result;
    }
}
