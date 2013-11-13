package com.tngtech.configbuilder.annotation.typetransformer;


import com.google.common.collect.Lists;
import com.tngtech.configbuilder.util.FieldValueTransformer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

public class CollectionTransformer extends ITypeTransformer<Collection,ArrayList> {

    private final FieldValueTransformer fieldValueTransformer;
    Type targetType;
    ArrayList<Class> availableTransformers;

    public CollectionTransformer(FieldValueTransformer fieldValueTransformer, Type targetType, ArrayList<Class> availableTransformers) {
        this.fieldValueTransformer = fieldValueTransformer;
        this.targetType = targetType;
        this.availableTransformers = availableTransformers;
    }

    @Override
    public ArrayList transform(Collection argument) {
        ArrayList result = Lists.newArrayList();
        for(Object value : argument ) {
            result.add(fieldValueTransformer.performNecessaryTransformations(value, targetType, availableTransformers));
        }
        return result;
    }
}
