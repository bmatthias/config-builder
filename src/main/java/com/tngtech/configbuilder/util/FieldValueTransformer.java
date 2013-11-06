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
import java.util.Arrays;

public class FieldValueTransformer {

    private final static Logger log = Logger.getLogger(FieldValueTransformer.class);

    private final ConfigBuilderFactory configBuilderFactory;
    private final FieldValueExtractor fieldValueExtractor;
    private final ErrorMessageSetup errorMessageSetup;
    private final ClassCastingHelper classCastingHelper;
    
    private final ArrayList defaultTransformers;

    public FieldValueTransformer(ConfigBuilderFactory configBuilderFactory) {
        this.configBuilderFactory = configBuilderFactory;
        this.fieldValueExtractor = configBuilderFactory.getInstance(FieldValueExtractor.class);
        this.errorMessageSetup = configBuilderFactory.getInstance(ErrorMessageSetup.class);
        this.classCastingHelper = configBuilderFactory.getInstance(ClassCastingHelper.class);
        
        defaultTransformers = Lists.newArrayList(StringToIntegerTransformer.class, StringToBooleanTransformer.class, StringToDoubleTransformer.class, IntegerToDoubleTransformer.class);
    }
    
    public <TargetClass> TargetClass transformedFieldValue(Field field, BuilderConfiguration builderConfiguration) {
        Object sourceValue = fieldValueExtractor.extractValue(field, builderConfiguration);
        Class sourceClass = sourceValue.getClass();
        Class targetClass = field.getType();
        
        if(targetClass.isPrimitive()) {
            targetClass = classCastingHelper.getWrapperClassForPrimitive(targetClass);
        }
        
        log.debug(String.format("Searching for a transformer from %s to %s", sourceClass.toString(),  targetClass.toString()));
        
        if(sourceClass.isAssignableFrom(targetClass)) {
            return (TargetClass) sourceValue;
        }
        
        Class[] suggestedTransformers = getSuggestedTransformers(field);
        ITypeTransformer<Object, TargetClass> transformer = findApplicableTransformer(sourceClass, targetClass, suggestedTransformers);
        log.debug(String.format("Transformer found: %s", transformer.toString()));
        
        TargetClass transformedValue = transformer.transform(sourceValue);
        return transformedValue; 
    }
    
    private Class[] getSuggestedTransformers(Field field) {
        if(field.isAnnotationPresent(TypeTransformers.class)) {
            TypeTransformers annotation =  field.getAnnotation(TypeTransformers.class);
            return annotation.value();
        } else {
            return new Class[]{};
        }
    }
    
    private <S, T> ITypeTransformer<S, T> findApplicableTransformer(Class<S> sourceClass, Class<T> targetClass, Class[] suggestedTransformerClasses) {
        ArrayList<Class> allTransformers = new ArrayList<>();
        allTransformers.addAll(Arrays.asList(suggestedTransformerClasses));
        allTransformers.addAll(defaultTransformers);
        
        log.debug(String.format("All available transformers: %s", allTransformers.toString()));
        
        for(Class clazz: allTransformers) {
            Type[] typeOfInterface = clazz.getGenericInterfaces();
            Type[] genericTypes = ((ParameterizedType) typeOfInterface[0]).getActualTypeArguments();
            
            Class transformerSourceClass = classCastingHelper.castTypeToClass(genericTypes[0]);
            Class transformerTargetClass = classCastingHelper.castTypeToClass(genericTypes[1]);
            
            if(transformerSourceClass.isAssignableFrom(sourceClass) && targetClass.isAssignableFrom(transformerTargetClass)) {
                log.debug(String.format("Found a transformer: %s", clazz));
                ITypeTransformer<S, T> transformer = (ITypeTransformer<S, T>) configBuilderFactory.getInstance(clazz);
                return transformer;
            }
        }

        throw new TypeTransformerException(errorMessageSetup.getErrorMessage(TypeTransformerException.class, sourceClass.toString(), targetClass.toString()));
    }
}
