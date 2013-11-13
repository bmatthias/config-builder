package com.tngtech.configbuilder.annotation.typetransformer;


import com.google.common.collect.Lists;
import com.tngtech.configbuilder.util.FieldValueTransformer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

public class CollectionTransformer extends ITypeTransformer<Collection,ArrayList> {

    @Override
    public ArrayList transform(Collection argument) {
        ArrayList result = Lists.newArrayList();
        for(Object value : argument) {
            result.add(this.getFieldValueTransformer().performNecessaryTransformations(value, ((ParameterizedType)this.getTargetType()).getActualTypeArguments()[0], this.getAvailableTransformers()));
        }
        return result;
    }

    @Override
    public boolean isMatching(Class<?> sourceClass, Class<?> targetClass) {
        return Collection.class.isAssignableFrom(targetClass) && Collection.class.isAssignableFrom(sourceClass);
    }
}
