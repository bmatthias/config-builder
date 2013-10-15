package com.tngtech.configbuilder.util;

import com.google.common.collect.Lists;
import com.tngtech.configbuilder.annotation.configuration.CollectionType;
import com.tngtech.configbuilder.annotation.configuration.LoadingOrder;
import com.tngtech.configbuilder.annotation.valueextractor.IValueExtractorProcessor;
import com.tngtech.configbuilder.annotation.valueextractor.ValueExtractorAnnotation;
import com.tngtech.configbuilder.annotation.valuetransformer.IValueTransformerProcessor;
import com.tngtech.configbuilder.annotation.valuetransformer.ValueTransformer;
import com.tngtech.configbuilder.annotation.valuetransformer.ValueTransformerAnnotation;
import com.tngtech.configbuilder.configuration.BuilderConfiguration;
import com.tngtech.configbuilder.context.ConfigBuilderFactory;
import org.apache.log4j.Logger;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

public class FieldValueExtractor {

    private final static Logger log = Logger.getLogger(FieldValueExtractor.class);

    private final AnnotationHelper annotationHelper;
    private final ConfigBuilderFactory configBuilderFactory;

    public FieldValueExtractor(AnnotationHelper annotationHelper, ConfigBuilderFactory configBuilderFactory) {
        this.annotationHelper = annotationHelper;
        this.configBuilderFactory = configBuilderFactory;
    }


    public Object extractValue(Field field, BuilderConfiguration builderConfiguration) {
        String value = extractString(field, builderConfiguration);
        if (field.isAnnotationPresent(CollectionType.class) && value != null) {
            String[] values = value.split(field.getAnnotation(CollectionType.class).value());
            return buildCollection(field, values);
        } else {
            return field.isAnnotationPresent(ValueTransformer.class) ? transformStringWithTransformer(field, value) : transformStringToPrimitiveIfApplicable(field.getType(), value);
        }

    }

    private Object buildCollection(Field field, String[] values) {
        field.getGenericType();
        List<Object> collection = Lists.newArrayList();
        for (String value : values) {
            collection.add(transformStringWithTransformer(field, value));
        }
        return collection;
    }

    private String extractString(Field field, BuilderConfiguration builderConfiguration) {
        String value = null;
        Class<? extends Annotation>[] annotationOrderOfField = field.isAnnotationPresent(LoadingOrder.class) ? field.getAnnotation(LoadingOrder.class).value() : builderConfiguration.getAnnotationOrder();
        Class<? extends IValueExtractorProcessor> processor;

        for (Annotation annotation : annotationHelper.getAnnotationsInOrder(field, annotationOrderOfField)) {
            log.debug(String.format("trying to find string value for field %s with %s annotation", field.getName(), annotation.annotationType()));
            processor = annotation.annotationType().getAnnotation(ValueExtractorAnnotation.class).value();
            value = configBuilderFactory.getInstance(processor).getValue(annotation, builderConfiguration);
            if (value != null) {
                log.debug(String.format("found string value \"%s\" for field %s from %s annotation", value, field.getName(), annotation.annotationType()));
                break;
            }
        }
        return value;
    }

    private Object transformStringWithTransformer(Field field, String value) {
        Class<? extends IValueTransformerProcessor<Object>> processorClass;
        Object fieldValue = value;
        for (Annotation annotation : annotationHelper.getAnnotationsAnnotatedWith(field.getDeclaredAnnotations(), ValueTransformerAnnotation.class)) {
            log.debug(String.format("transorming string value(s) for field %s with %s annotation", field.getName(), annotation.annotationType()));
            processorClass = annotation.annotationType().getAnnotation(ValueTransformerAnnotation.class).value();
            IValueTransformerProcessor processor = (IValueTransformerProcessor) configBuilderFactory.getInstance(processorClass);
            fieldValue = processor.transformString(annotation, value);
        }
        return fieldValue;
    }

    private Object transformStringToPrimitiveIfApplicable(Class<?> targetType, String text) {
        PropertyEditor editor = PropertyEditorManager.findEditor(targetType);
        if (editor != null) {
            editor.setAsText(text);
            return editor.getValue();
        } else {
            return text;
        }
    }
}