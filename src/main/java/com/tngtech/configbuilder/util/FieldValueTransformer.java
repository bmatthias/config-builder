package com.tngtech.configbuilder.util;

import com.google.common.collect.Lists;
import com.tngtech.configbuilder.annotation.typetransformer.*;
import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.exception.TypeTransformerException;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;

//TODO: Content transformers (transform even if types already match, allow null as argument) & bring back generics
public class FieldValueTransformer {

    private final static Logger log = Logger.getLogger(FieldValueTransformer.class);

    private final ConfigBuilderFactory configBuilderFactory;
    private final ErrorMessageSetup errorMessageSetup;
    private final GenericsAndCastingHelper genericsAndCastingHelper;
    
    private final ArrayList<Class> defaultTransformers = Lists.newArrayList(new Class[]{
            StringOrPrimitiveToPrimitiveTransformer.class,
            CommaSeparatedStringToStringCollectionTransformer.class,
            CollectionTransformer.class,
            StringCollectionToCommaSeparatedStringTransformer.class,
            StringToPathTransformer.class});

    public FieldValueTransformer(ConfigBuilderFactory configBuilderFactory) {
        this.configBuilderFactory = configBuilderFactory;
        this.errorMessageSetup = configBuilderFactory.getInstance(ErrorMessageSetup.class);
        this.genericsAndCastingHelper = configBuilderFactory.getInstance(GenericsAndCastingHelper.class);
    }

    public Object transformFieldValue(Field field, Object sourceValue) {
        sourceValue = performNecessaryTransformations(sourceValue, field.getGenericType(), getAllTransformers(field));
        return sourceValue;
    }

    public Object performNecessaryTransformations(Object sourceValue, Type targetType, ArrayList<Class> allTransformers) {
        if(genericsAndCastingHelper.typesMatch(sourceValue, targetType)) {
            return sourceValue;
        }
        Class<?> sourceClass = genericsAndCastingHelper.getWrapperClassForPrimitive(sourceValue.getClass());
        Class<?> targetClass = genericsAndCastingHelper.castTypeToClass(targetType);

        log.info(String.format("Searching for a transformer from %s to %s", sourceClass.toString(), targetClass.toString()));

        TypeTransformer<Object, ?> transformer = findApplicableTransformer(sourceClass, targetType, allTransformers);
        sourceValue = transformer.transform(sourceValue);
        return performNecessaryTransformations(sourceValue, targetType, allTransformers);
    }

    private TypeTransformer findApplicableTransformer(Class<?> sourceClass, Type targetType, ArrayList<Class> availableTransformerClasses) {
        Class<?> targetClass = genericsAndCastingHelper.getWrapperClassForPrimitive(genericsAndCastingHelper.castTypeToClass(targetType));
        for(Class<TypeTransformer> clazz: availableTransformerClasses) {
            TypeTransformer transformer = configBuilderFactory.getInstance(clazz);
            transformer.setGenericsAndCastingHelper(genericsAndCastingHelper);
            if(transformer.isMatching(sourceClass, targetClass)) {
                transformer.initialize(this, configBuilderFactory, targetType, availableTransformerClasses);
                return transformer;
            }
        }
        throw new TypeTransformerException(errorMessageSetup.getErrorMessage(TypeTransformerException.class, sourceClass.toString(), targetClass.toString()));
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
