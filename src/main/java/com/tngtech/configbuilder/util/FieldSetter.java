package com.tngtech.configbuilder.util;

import com.tngtech.configbuilder.annotation.valueextractor.ValueExtractorAnnotation;
import com.tngtech.configbuilder.configuration.BuilderConfiguration;
import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.exception.ConfigBuilderException;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;

public class FieldSetter<T> {

    private final static Logger log = Logger.getLogger(FieldSetter.class);

    private final FieldValueTransformer fieldValueTransformer;
    private final ErrorMessageSetup errorMessageSetup;
    private final AnnotationHelper annotationHelper;

    public FieldSetter(FieldValueTransformer fieldValueTransformer, ErrorMessageSetup errorMessageSetup, AnnotationHelper annotationHelper) {
        this.fieldValueTransformer = fieldValueTransformer;
        this.errorMessageSetup = errorMessageSetup;
        this.annotationHelper = annotationHelper;
    }

    public void setFields(T instanceOfConfigClass, BuilderConfiguration builderConfiguration) {

        for (Field field : instanceOfConfigClass.getClass().getDeclaredFields()) {
            if (annotationHelper.fieldHasAnnotationAnnotatedWith(field, ValueExtractorAnnotation.class)) {
                Object value = fieldValueTransformer.transformedFieldValue(field, builderConfiguration);
                setField(instanceOfConfigClass, field, value);
            } else {
                log.debug(String.format("field %s is not annotated with any ValueExtractorAnnotation: skipping field", field.getName()));
            }
        }
    }

    public void setEmptyFields(T instanceOfConfigClass, BuilderConfiguration builderConfiguration) {
        try {
            for (Field field : instanceOfConfigClass.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (annotationHelper.fieldHasAnnotationAnnotatedWith(field, ValueExtractorAnnotation.class) && (field.getType().isPrimitive() || field.get(instanceOfConfigClass) == null)) {
                    Object value = fieldValueTransformer.transformedFieldValue(field, builderConfiguration);
                    setField(instanceOfConfigClass, field, value);
                } else {
                    log.debug(String.format("skipping field %s", field.getName()));
                }
            }
        } catch (IllegalAccessException e) {
            throw new ConfigBuilderException("THIS CAN NOT HAPPEN", e);
        }
    }

    private void setField(T instanceOfConfigClass, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(instanceOfConfigClass, value);
            log.info(String.format("set field %s of type %s to a value of type %s", field.getName(), field.getType().getName(), value == null ? "null" : value.getClass().getName()));
        } catch (Exception e) {
            throw new ConfigBuilderException(errorMessageSetup.getErrorMessage(e, field.getName(), field.getType().getName(), value == null ? "null" : value.toString()), e);
        }
    }
}