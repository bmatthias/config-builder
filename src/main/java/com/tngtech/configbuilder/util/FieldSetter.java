package com.tngtech.configbuilder.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.tngtech.configbuilder.annotation.valueextractor.ValueExtractorAnnotation;
import com.tngtech.configbuilder.configuration.BuilderConfiguration;
import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.exception.ConfigBuilderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;

public class FieldSetter<T> {

    private final static Logger log = LoggerFactory.getLogger(FieldSetter.class);

    private final FieldValueTransformer fieldValueTransformer;
    private final FieldValueExtractor fieldValueExtractor;
    private final ErrorMessageSetup errorMessageSetup;
    private final AnnotationHelper annotationHelper;

    public FieldSetter(ConfigBuilderFactory configBuilderFactory) {
        this.annotationHelper = configBuilderFactory.getInstance(AnnotationHelper.class);
        this.errorMessageSetup = configBuilderFactory.getInstance(ErrorMessageSetup.class);
        this.fieldValueTransformer = configBuilderFactory.getInstance(FieldValueTransformer.class);
        this.fieldValueExtractor = configBuilderFactory.getInstance(FieldValueExtractor.class);
    }

    public void setFields(T instanceOfConfigClass, BuilderConfiguration builderConfiguration) {

        for (Field field : getInheritedPrivateFields(instanceOfConfigClass.getClass())) {
            if (annotationHelper.fieldHasAnnotationAnnotatedWith(field, ValueExtractorAnnotation.class)) {
                Object value = fieldValueExtractor.extractValue(field, builderConfiguration);
                value = fieldValueTransformer.transformFieldValue(field, value);
                setField(instanceOfConfigClass, field, value);
            } else {
                log.debug("field {} is not annotated with any ValueExtractorAnnotation: skipping field", field.getName());
            }
        }
    }

    public static List<Field> getInheritedPrivateFields(Class type) {
        final ImmutableList.Builder<Field> listBuilder = ImmutableList.builder();

        Class currentType = type;
        while (currentType != null && currentType != Object.class) {
            listBuilder.addAll(Lists.newArrayList(currentType.getDeclaredFields()));
            currentType = currentType.getSuperclass();
        }

        return listBuilder.build();
    }

    private void setField(T instanceOfConfigClass, Field field, Object value) {
        try {
            field.setAccessible(true);
            if(value == null && field.getType().isPrimitive()) {
                log.warn("no value found for field {} of primitive type {}: field will be initialized to default", field.getName(), field.getType().getName());
            }
            else {
                field.set(instanceOfConfigClass, value);
                log.debug("set field {} of type {} to a value of type {}", field.getName(), field.getType().getName(), value == null ? "null" : value.getClass().getName());
            }
        } catch (Exception e) {
            throw new ConfigBuilderException(errorMessageSetup.getErrorMessage(e, field.getName(), field.getType().getName(), value == null ? "null" : value.toString()), e);
        }
    }
}