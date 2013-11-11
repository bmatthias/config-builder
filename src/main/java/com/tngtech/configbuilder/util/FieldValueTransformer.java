package com.tngtech.configbuilder.util;

import com.google.common.collect.Lists;
import com.tngtech.configbuilder.annotation.typetransformer.*;
import com.tngtech.configbuilder.configuration.BuilderConfiguration;
import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.exception.TypeTransformerException;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class FieldValueTransformer {

    private final static Logger log = Logger.getLogger(FieldValueTransformer.class);

    private final ConfigBuilderFactory configBuilderFactory;
    private final FieldValueExtractor fieldValueExtractor;
    private final ErrorMessageSetup errorMessageSetup;
    private final ClassCastingHelper classCastingHelper;
    
    private final ArrayList defaultTransformers = Lists.newArrayList(
            CommaSeparatedStringToStringCollectionTransformer.class,
            StringCollectionToCommaSeparatedStringTransformer.class,
            StringToIntegerTransformer.class,
            StringToBooleanTransformer.class,
            StringToDoubleTransformer.class,
            IntegerToDoubleTransformer.class,
            StringToPathTransformer.class,
            CollectionTransformer.class);

    public FieldValueTransformer(ConfigBuilderFactory configBuilderFactory) {
        this.configBuilderFactory = configBuilderFactory;
        this.fieldValueExtractor = configBuilderFactory.getInstance(FieldValueExtractor.class);
        this.errorMessageSetup = configBuilderFactory.getInstance(ErrorMessageSetup.class);
        this.classCastingHelper = configBuilderFactory.getInstance(ClassCastingHelper.class);
    }
    
    public Object transformedFieldValue(Field field, BuilderConfiguration builderConfiguration) {
        Object sourceValue = fieldValueExtractor.extractValue(field, builderConfiguration);

        if(sourceValue == null) {
            return sourceValue;
        }

        Class sourceClass = sourceValue.getClass();
        Class targetClass = getNonPrimitiveTargetClass(field);
        
        log.info(String.format("Searching for a transformer from %s to %s", sourceClass.toString(), targetClass.toString()));
        
        if(targetClass.isAssignableFrom(sourceClass)) {
            return sourceValue;
        }

        ArrayList<Class> allTransformers = getAllTransformers(field);
        ITypeTransformer<Object, ?> transformer = findApplicableTransformer(sourceClass, targetClass, allTransformers);

        Type typeOfInterface = transformer.getClass().getGenericSuperclass();
        Type[] genericTypes = ((ParameterizedType) typeOfInterface).getActualTypeArguments();

        if(!classCastingHelper.typeMatches((ParameterizedType)genericTypes[1],(ParameterizedType)field.getGenericType())) {
            sourceValue = transformer.transform(sourceValue);
            sourceClass = sourceValue.getClass();
            transformer = findApplicableTransformer(sourceClass, targetClass, allTransformers);
        }

        return transformer.transform(sourceValue);
    }

    private Class getNonPrimitiveTargetClass(Field field) {
        Class targetClass = field.getType();

        if(targetClass.isPrimitive()) {
            targetClass = classCastingHelper.getWrapperClassForPrimitive(targetClass);
        }
        
        return targetClass;
    }
    
    private ArrayList<Class> getAllTransformers(Field field) {
        ArrayList<Class> suggestedTransformers = getUserSuggestedTransformers(field);
        suggestedTransformers.addAll(defaultTransformers);
        return suggestedTransformers;
    }
    
    private ArrayList getUserSuggestedTransformers(Field field) {
        if(field.isAnnotationPresent(TypeTransformers.class)) {
            TypeTransformers annotation =  field.getAnnotation(TypeTransformers.class);
            return Lists.newArrayList(annotation.value());
        } else {
            return Lists.newArrayList();
        }
    }
    
    private <S, T> ITypeTransformer<S, T> findApplicableTransformer(Class<?> sourceClass, Class<?> targetClass, ArrayList<Class> availableTransformerClasses) {
        ITypeTransformer<S, T> transformer = null;
        for(Class clazz: availableTransformerClasses) {
            transformer = (ITypeTransformer<S, T>) configBuilderFactory.getInstance(clazz);
            if(transformer.isMatching(sourceClass, targetClass)) {
                return transformer;
            }
        }
        throw new TypeTransformerException(errorMessageSetup.getErrorMessage(TypeTransformerException.class, sourceClass.toString(), targetClass.toString()));
    }
}
