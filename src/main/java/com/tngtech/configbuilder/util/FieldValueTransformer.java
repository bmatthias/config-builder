package com.tngtech.configbuilder.util;

import com.google.common.collect.Lists;
import com.tngtech.configbuilder.annotation.typetransformer.*;
import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.exception.TypeTransformerException;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class FieldValueTransformer {

    private final static Logger log = Logger.getLogger(FieldValueTransformer.class);

    private final ConfigBuilderFactory configBuilderFactory;
    private final ErrorMessageSetup errorMessageSetup;
    private final ClassCastingHelper classCastingHelper;
    
    private final ArrayList<Class> defaultTransformers = Lists.newArrayList(new Class[]{
            CommaSeparatedStringToStringCollectionTransformer.class,
            StringCollectionToCommaSeparatedStringTransformer.class,
            StringToPathTransformer.class,
            CollectionTransformer.class,
            StringOrPrimitiveToPrimitiveTransformer.class});

    public FieldValueTransformer(ConfigBuilderFactory configBuilderFactory) {
        this.configBuilderFactory = configBuilderFactory;
        this.errorMessageSetup = configBuilderFactory.getInstance(ErrorMessageSetup.class);
        this.classCastingHelper = configBuilderFactory.getInstance(ClassCastingHelper.class);
    }

    //TODO: Better algorithm for when to apply user suggested transformers (e.g. allow null), maybe introduce content transformers?
    public Object transformFieldValue(Field field, Object sourceValue) {
        sourceValue = performNecessaryTransformations(sourceValue, field.getGenericType(), getAllTransformers(field));
        if(!getUserSuggestedTransformers(field).isEmpty() && sourceValue != null) {
            Class<?> sourceClass = classCastingHelper.getWrapperClassForPrimitive(sourceValue.getClass());
            ITypeTransformer<Object, ?> transformer = findApplicableTransformer(sourceClass, field.getGenericType(), getUserSuggestedTransformers(field));
            if(transformer != null) {
                sourceValue = transformer.transform(sourceValue);
            }
        }
        return sourceValue;
    }

    public Object performNecessaryTransformations(Object sourceValue, Type targetType, ArrayList<Class> allTransformers) {
        if(classCastingHelper.typesMatch(sourceValue, targetType)) {
            return sourceValue;
        }

        Class<?> sourceClass = classCastingHelper.getWrapperClassForPrimitive(sourceValue.getClass());
        Class<?> targetClass = classCastingHelper.castTypeToClass(targetType);

        log.info(String.format("Searching for a transformer from %s to %s", sourceClass.toString(), targetClass.toString()));

        ITypeTransformer<Object, ?> transformer = findApplicableTransformer(sourceClass, targetType, allTransformers);
        if(transformer == null) {
            throw new TypeTransformerException(errorMessageSetup.getErrorMessage(TypeTransformerException.class, sourceClass.toString(), targetClass.toString()));
        }
        sourceValue = transformer.transform(sourceValue);
        return performNecessaryTransformations(sourceValue, targetType, allTransformers);
    }

    //TODO: Bring back generics
    private ITypeTransformer findApplicableTransformer(Class<?> sourceClass, Type targetType, ArrayList<Class> availableTransformerClasses) {
        Class<?> targetClass = classCastingHelper.getWrapperClassForPrimitive(classCastingHelper.castTypeToClass(targetType));
        for(Class<ITypeTransformer> clazz: availableTransformerClasses) {
            //TODO: Do not instantiate new transformers all the time & Make this work for transformers that are inner classes (i.e. cannot be instantiated by configBuilderFactory)
            ITypeTransformer transformer = configBuilderFactory.getInstance(clazz);
            transformer.setClassCastingHelper(classCastingHelper);
            if(transformer.isMatching(sourceClass, targetClass)) {
                transformer.setFieldValueTransformer(this);
                transformer.setErrorMessageSetup(errorMessageSetup);
                transformer.setTargetType(targetType);
                transformer.setAvailableTransformers(availableTransformerClasses);
                return transformer;
            }
        }
        return null;
    }

    private ArrayList<Class> getUserSuggestedTransformers(Field field) {
        if(field.isAnnotationPresent(TypeTransformers.class)) {
            TypeTransformers annotation =  field.getAnnotation(TypeTransformers.class);
            return Lists.newArrayList(annotation.value());
        } else {
            return Lists.newArrayList();
        }
    }

    private ArrayList<Class> getAllTransformers(Field field) {
        ArrayList<Class> allTransformers = getUserSuggestedTransformers(field);
        allTransformers.addAll(defaultTransformers);
        return  allTransformers;
    }
}
