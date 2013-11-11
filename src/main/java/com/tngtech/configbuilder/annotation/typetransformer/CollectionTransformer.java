package com.tngtech.configbuilder.annotation.typetransformer;


import com.google.common.collect.Lists;
import com.tngtech.configbuilder.util.ConfigBuilderFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionTransformer extends ITypeTransformer<Collection,ArrayList> {

    private final List<ITypeTransformer> availableTransformers = new ArrayList<>();
    private final ConfigBuilderFactory configBuilderFactory;
    private final ArrayList availableTransformerClasses;

    public CollectionTransformer(ConfigBuilderFactory configBuilderFactory) {
        this.configBuilderFactory = configBuilderFactory;
        availableTransformerClasses = Lists.newArrayList(
                CommaSeparatedStringToStringCollectionTransformer.class,
                StringCollectionToCommaSeparatedStringTransformer.class,
                StringToIntegerTransformer.class,
                StringToBooleanTransformer.class,
                StringToDoubleTransformer.class,
                IntegerToDoubleTransformer.class,
                StringToPathTransformer.class,
                CollectionTransformer.class);

        for(Object clazz : availableTransformerClasses) {
            availableTransformers.add((ITypeTransformer)configBuilderFactory.getInstance((Class)clazz));
        }
    }

    @Override
    public ArrayList transform(Collection argument) {
        ITypeTransformer transformer = findApplicableTransformer(String.class,Path.class,availableTransformerClasses);
        ArrayList result = Lists.newArrayList();
        for(Object value : argument ) {
            result.add(transformer.transform(value));
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

    private <S, T> ITypeTransformer<S, T> findApplicableTransformer(Class<?> sourceClass, Class<?> targetClass, ArrayList<Class> availableTransformerClasses) {
        for(ITypeTransformer<S,T> transformer : availableTransformers) {
            if(transformer.isMatching(sourceClass, targetClass)) {
                return transformer;
            }
        }
        return null;
        //throw new TypeTransformerException(errorMessageSetup.getErrorMessage(TypeTransformerException.class, sourceClass.toString(), targetClass.toString()));
    }
}
