package com.tngtech.configbuilder.util;

import com.google.common.collect.Lists;
import com.tngtech.configbuilder.annotation.typetransformer.*;
import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.exception.TypeTransformerException;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class FieldValueTransformer {

    private final static Logger log = Logger.getLogger(FieldValueTransformer.class);

    private final ConfigBuilderFactory configBuilderFactory;
    private final ErrorMessageSetup errorMessageSetup;
    private final ClassCastingHelper classCastingHelper;
    
    private final ArrayList<Class> defaultTransformers = Lists.newArrayList(new Class[]{
            CommaSeparatedStringToStringCollectionTransformer.class,
            StringCollectionToCommaSeparatedStringTransformer.class,
            StringToPathTransformer.class});

    public FieldValueTransformer(ConfigBuilderFactory configBuilderFactory) {
        this.configBuilderFactory = configBuilderFactory;
        this.errorMessageSetup = configBuilderFactory.getInstance(ErrorMessageSetup.class);
        this.classCastingHelper = configBuilderFactory.getInstance(ClassCastingHelper.class);
    }
    
    public Object transformFieldValue(Field field, Object sourceValue) {
        sourceValue = applyUserSuggestedTransformers(field, sourceValue);
        return performApplicableTransformations(field.getGenericType(), sourceValue, defaultTransformers);
    }

    //TODO: Make this work for transformers that are inner classes (cannot be instantiated by configBuilderFactory)
    public Object applyUserSuggestedTransformers(Field field, Object sourceValue) {
        for(Class clazz : getUserSuggestedTransformers(field)) {
            ITypeTransformer transformer = (ITypeTransformer)configBuilderFactory.createInstance(clazz);
            sourceValue = transformer.transform(sourceValue);
        }
        return sourceValue;
    }

    //TODO: Rearrange if statements, maybe call this recursively
    public Object performApplicableTransformations(Type targetType, Object sourceValue, ArrayList<Class> allTransformers) {

        if(sourceValue == null) {
            return sourceValue;
        }

        Class sourceClass = sourceValue.getClass();
        Class targetClass = classCastingHelper.castTypeToClass(targetType);

        log.info(String.format("Searching for a transformer from %s to %s", sourceClass.toString(), targetClass.toString()));

        if(targetClass.isAssignableFrom(sourceClass)) {
            return sourceValue;
        }

        if(classCastingHelper.isPrimitiveOrWrapper(targetClass) && (String.class.equals(sourceClass) || classCastingHelper.isPrimitiveOrWrapper(sourceClass))) {
            return new StringToPrimitiveTransformer(targetClass).transform(String.valueOf(sourceValue));
        }

        if(Collection.class.isAssignableFrom(targetClass) && String.class.equals(sourceClass)) {
            ITypeTransformer<String,ArrayList<String>> transformer = new CommaSeparatedStringToStringCollectionTransformer();
            sourceValue = transformer.transform((String)sourceValue);
            sourceClass = sourceValue.getClass();
        }

        if(Collection.class.isAssignableFrom(targetClass) && Collection.class.isAssignableFrom(sourceClass)) {
            ITypeTransformer<Collection,ArrayList> transformer = new CollectionTransformer(this, ((ParameterizedType)targetType).getActualTypeArguments()[0], allTransformers);
            return transformer.transform((Collection)sourceValue);
        }

        else {
            ITypeTransformer<Object, ?> transformer = findApplicableTransformer(sourceClass, targetClass, allTransformers);
            return transformer.transform(sourceValue);
        }
    }
    
    private ArrayList<Class> getUserSuggestedTransformers(Field field) {
        if(field.isAnnotationPresent(TypeTransformers.class)) {
            TypeTransformers annotation =  field.getAnnotation(TypeTransformers.class);
            return Lists.newArrayList(annotation.value());
        } else {
            return Lists.newArrayList();
        }
    }

    private <S, T> ITypeTransformer<S, T> findApplicableTransformer(Class<?> sourceClass, Class<?> targetClass, ArrayList<Class> availableTransformerClasses) {
        for(Class clazz: availableTransformerClasses) {
            Type typeOfInterface = clazz.getGenericSuperclass();
            Type[] genericTypes = ((ParameterizedType) typeOfInterface).getActualTypeArguments();

            Class transformerSourceClass = classCastingHelper.castTypeToClass(genericTypes[0]);
            Class transformerTargetClass = classCastingHelper.castTypeToClass(genericTypes[1]);
            if(transformerSourceClass.isAssignableFrom(sourceClass) && targetClass.isAssignableFrom(transformerTargetClass)) {
                return (ITypeTransformer)configBuilderFactory.createInstance(clazz);
            }
        }
        throw new TypeTransformerException(errorMessageSetup.getErrorMessage(TypeTransformerException.class, sourceClass.toString(), targetClass.toString()));
    }
}
