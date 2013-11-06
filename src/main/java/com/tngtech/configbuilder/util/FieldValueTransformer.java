package com.tngtech.configbuilder.util;

import com.tngtech.configbuilder.annotation.typetransformer.ITypeTransformer;
import com.tngtech.configbuilder.annotation.typetransformer.TypeTransformers;
import com.tngtech.configbuilder.configuration.BuilderConfiguration;
import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.exception.TypeTransformerException;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class FieldValueTransformer {

    private final static Logger log = Logger.getLogger(FieldValueTransformer.class);

    private final ConfigBuilderFactory configBuilderFactory;
    private final FieldValueExtractor fieldValueExtractor;
    private ErrorMessageSetup errorMessageSetup;

    public FieldValueTransformer(ConfigBuilderFactory configBuilderFactory) {
        this.configBuilderFactory = configBuilderFactory;
        this.fieldValueExtractor = configBuilderFactory.getInstance(FieldValueExtractor.class);
        
        this.errorMessageSetup = configBuilderFactory.getInstance(ErrorMessageSetup.class);
    }
    
    public <TargetClass> TargetClass transformedFieldValue(Field field, BuilderConfiguration builderConfiguration) {
        Object sourceValue = fieldValueExtractor.extractValue(field, builderConfiguration);
        Class sourceClass = sourceValue.getClass();
        Class targetClass = field.getType();
        
        log.debug(String.format("Searching for a transformer from %s to %s", sourceClass.toString(),  targetClass.toString()));
        
        if(sourceClass.isAssignableFrom(targetClass)) {
            return (TargetClass) sourceValue;
        }
        
        if(!field.isAnnotationPresent(TypeTransformers.class)) {
            throw new TypeTransformerException(errorMessageSetup.getErrorMessage(TypeTransformerException.class, sourceClass.toString(), targetClass.toString()));
        }
            
        Class[] suggestedTransformers = getSuggestedTransformers(field);
        ITypeTransformer<Object, TargetClass> transformer = findApplicableTransformer(sourceClass, targetClass, suggestedTransformers);
        log.debug(String.format("Transformer found: %s", transformer.toString()));
        
        TargetClass transformedValue = transformer.transform(sourceValue);
        return transformedValue; 
    }
    
    private Class[] getSuggestedTransformers(Field field) {
        TypeTransformers annotation =  field.getAnnotation(TypeTransformers.class);
        return annotation.value();
    }
    
    private <S, T> ITypeTransformer<S, T> findApplicableTransformer(Class<S> sourceClass, Class<T> targetClass, Class[] suggestedTransformerClasses) {
        for(int i=0; i < suggestedTransformerClasses.length; i++) {
            Class clazz = suggestedTransformerClasses[i];
            Type[] interfaceType = clazz.getGenericInterfaces();
            Type[] genericTypes = ((ParameterizedType) interfaceType[0]).getActualTypeArguments();
            
            Class transformerSourceClass = castToClass(genericTypes[0]);
            Class transformerTargetClass = castToClass(genericTypes[1]);
            
            if(transformerSourceClass.isAssignableFrom(sourceClass) && targetClass.isAssignableFrom(transformerTargetClass)) {
                ITypeTransformer<S, T> transformer = (ITypeTransformer<S, T>) configBuilderFactory.getInstance(clazz);
                return transformer;
            }
        }

        throw new TypeTransformerException(errorMessageSetup.getErrorMessage(TypeTransformerException.class, sourceClass.toString(), targetClass.toString()));
    }
    
    private static Class castToClass(Type object) {
        if(object.getClass().equals(Class.class)) {
            return (Class<?>) object;
        } else {
           return (Class<?>) ((ParameterizedType) object).getRawType(); 
        }
    }
}
