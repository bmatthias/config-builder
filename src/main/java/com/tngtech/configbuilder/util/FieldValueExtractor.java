package com.tngtech.configbuilder.util;

import com.tngtech.configbuilder.annotation.configuration.DoNotLogValue;
import com.tngtech.configbuilder.annotation.configuration.LoadingOrder;
import com.tngtech.configbuilder.annotation.valueextractor.ValueExtractorAnnotation;
import com.tngtech.configbuilder.annotation.valueextractor.ValueExtractorProcessor;
import com.tngtech.configbuilder.configuration.BuilderConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class FieldValueExtractor {

    private final static Logger log = LoggerFactory.getLogger(FieldValueExtractor.class);

    private final AnnotationHelper annotationHelper;
    private final ConfigBuilderFactory configBuilderFactory;

    public FieldValueExtractor(ConfigBuilderFactory configBuilderFactory) {
        this.configBuilderFactory = configBuilderFactory;
        this.annotationHelper = configBuilderFactory.getInstance(AnnotationHelper.class);
    }

    public Object extractValue(Field field, BuilderConfiguration builderConfiguration) {
        Object value = null;
        boolean doNotLogValue = field.isAnnotationPresent(DoNotLogValue.class);
        Class<? extends Annotation>[] annotationOrderOfField = field.isAnnotationPresent(LoadingOrder.class) ? field.getAnnotation(LoadingOrder.class).value() : builderConfiguration.getAnnotationOrder();
        for (Annotation annotation : annotationHelper.getAnnotationsInOrder(field, annotationOrderOfField)) {
            log.debug("trying to find a value for field {} with {} annotation", field.getName(), annotation.annotationType());
            Class<? extends ValueExtractorProcessor> processor = annotation.annotationType().getAnnotation(ValueExtractorAnnotation.class).value();
            value = configBuilderFactory.getInstance(processor).getValue(annotation, configBuilderFactory);
            if (value != null) {
                if (doNotLogValue) {
                    log.debug("found value for field {} from {} annotation", field.getName(), annotation.annotationType());
                } else {
                    log.debug("found value \"{}\" for field {} from {} annotation", value, field.getName(), annotation.annotationType());
                }
                break;
            }
        }
        return value;
    }
}