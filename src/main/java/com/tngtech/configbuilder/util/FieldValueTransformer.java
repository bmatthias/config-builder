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
    
    private final ArrayList defaultTransformers = Lists.newArrayList(StringToIntegerTransformer.class, StringToBooleanTransformer.class, StringToDoubleTransformer.class, IntegerToDoubleTransformer.class);

    public FieldValueTransformer(ConfigBuilderFactory configBuilderFactory) {
        this.configBuilderFactory = configBuilderFactory;
        this.fieldValueExtractor = configBuilderFactory.getInstance(FieldValueExtractor.class);
        this.errorMessageSetup = configBuilderFactory.getInstance(ErrorMessageSetup.class);
        this.classCastingHelper = configBuilderFactory.getInstance(ClassCastingHelper.class);
    }
    
    public <TargetClass> TargetClass transformedFieldValue(Field field, BuilderConfiguration builderConfiguration) {
        Object sourceValue = fieldValueExtractor.extractValue(field, builderConfiguration);
        Class sourceClass = sourceValue.getClass();
        Class targetClass = getNonPrimitiveTargetClass(field);
        
        log.info(String.format("Searching for a transformer from %s to %s", sourceClass.toString(), targetClass.toString()));
        
        if(sourceClass.isAssignableFrom(targetClass)) {
            return (TargetClass) sourceValue;
        }
        
        ArrayList<Class> allTransformers = getAllTransformers(field);
        ITypeTransformer<Object, TargetClass> transformer = findApplicableTransformer(sourceClass, targetClass, allTransformers);
        
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
        ArrayList<Class> allTransformers = addDefaultTransformersTo(suggestedTransformers);
        return allTransformers;
    }
    
    private ArrayList getUserSuggestedTransformers(Field field) {
        if(field.isAnnotationPresent(TypeTransformers.class)) {
            TypeTransformers annotation =  field.getAnnotation(TypeTransformers.class);
            return Lists.newArrayList(annotation.value());
        } else {
            return Lists.newArrayList();
        }
    }

    private ArrayList<Class> addDefaultTransformersTo(ArrayList<Class> additionalTransformerClasses) {
        ArrayList<Class> allTransformers = new ArrayList<>();
        allTransformers.addAll(additionalTransformerClasses);
        allTransformers.addAll(defaultTransformers);

        return allTransformers;
    }
    
    private <S, T> ITypeTransformer<S, T> findApplicableTransformer(Class<S> sourceClass, Class<T> targetClass, ArrayList<Class> availableTransformerClasses) {
        for(Class clazz: availableTransformerClasses) {
            Type[] typeOfInterface = clazz.getGenericInterfaces();
            Type[] genericTypes = ((ParameterizedType) typeOfInterface[0]).getActualTypeArguments();
            
            Class transformerSourceClass = classCastingHelper.castTypeToClass(genericTypes[0]);
            Class transformerTargetClass = classCastingHelper.castTypeToClass(genericTypes[1]);
            
            if(transformerSourceClass.isAssignableFrom(sourceClass) && targetClass.isAssignableFrom(transformerTargetClass)) {
                ITypeTransformer<S, T> transformer = (ITypeTransformer<S, T>) configBuilderFactory.createInstance(clazz);
                return transformer;
            }
        }

        throw new TypeTransformerException(errorMessageSetup.getErrorMessage(TypeTransformerException.class, sourceClass.toString(), targetClass.toString()));
    }
}
