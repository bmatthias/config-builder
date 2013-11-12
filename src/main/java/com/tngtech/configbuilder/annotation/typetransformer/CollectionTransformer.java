package com.tngtech.configbuilder.annotation.typetransformer;


import com.google.common.collect.Lists;
import com.tngtech.configbuilder.util.FieldValueTransformer;

import java.lang.reflect.ParameterizedType;
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
            result.add(fieldValueTransformer.performApplicableTransformations(targetType, value, availableTransformers));
        }
        return result;
    }

    @Override
    public boolean isMatching(Class<?> sourceClass, Class<?> targetClass) {

        boolean isMatching = false;
        if(Collection.class.isAssignableFrom(sourceClass) && Collection.class.isAssignableFrom(targetClass)) {
            /*for(ITypeTransformer transformer : availableTransformers) {
                isMatching |= transformer.isMatching(sourceClass,targetClass);
            }*/
            return true;
        };
        return isMatching;
    }

    private Class castTypeToClass(Type object) {
        if(object.getClass().equals(Class.class)) {
            return (Class<?>) object;
        } else {
            return (Class<?>) ((ParameterizedType) object).getRawType();
        }
    }
}
