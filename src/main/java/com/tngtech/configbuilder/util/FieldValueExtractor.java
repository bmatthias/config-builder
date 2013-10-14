package com.tngtech.configbuilder.util;

import com.tngtech.configbuilder.annotation.valueextractor.IValueExtractorProcessor;
import com.tngtech.configbuilder.annotation.valuetransformer.IValueTransformerProcessor;
import com.tngtech.configbuilder.annotation.configuration.LoadingOrder;
import com.tngtech.configbuilder.annotation.valueextractor.ValueExtractorAnnotation;
import com.tngtech.configbuilder.annotation.valuetransformer.ValueTransformer;
import com.tngtech.configbuilder.annotation.valuetransformer.ValueTransformerAnnotation;
import com.tngtech.configbuilder.configuration.BuilderConfiguration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

@Component
public class FieldValueExtractor {

    private final static Logger log = Logger.getLogger(FieldValueExtractor.class);

    private final AnnotationHelper annotationHelper;
    private final BeanFactory beanFactory;

    @Autowired
    public FieldValueExtractor(AnnotationHelper annotationHelper, BeanFactory beanFactory) {
        this.annotationHelper = annotationHelper;
        this.beanFactory = beanFactory;
    }


    public Object extractValue(Field field, BuilderConfiguration builderConfiguration) {
        String value = extractString(field, builderConfiguration);
        return getTransformedFieldValueIfApplicable(field, value);
    }

    private String extractString(Field field, BuilderConfiguration builderConfiguration){
        String value = null;
        Class<? extends Annotation>[] annotationOrderOfField = field.isAnnotationPresent(LoadingOrder.class) ? field.getAnnotation(LoadingOrder.class).value() : builderConfiguration.getAnnotationOrder();
        Class<? extends IValueExtractorProcessor> processor;

        for (Annotation annotation : annotationHelper.getAnnotationsInOrder(field, annotationOrderOfField)) {
            log.debug(String.format("trying to find string value for field %s with %s annotation", field.getName(), annotation.annotationType()));
            processor = annotation.annotationType().getAnnotation(ValueExtractorAnnotation.class).value();
            value = beanFactory.getBean(processor).getValue(annotation, builderConfiguration);
            if (value != null) {
                log.debug(String.format("found string value \"%s\" for field %s from %s annotation", value, field.getName(), annotation.annotationType()));
                break;
            }
        }
        return value;
    }

    private Object getTransformedFieldValueIfApplicable(Field field, String value) {
        if(field.isAnnotationPresent(ValueTransformer.class)) {
            return transformStringWithTransformer(field, value);
        }
        else {
            return transformStringToPrimitiveIfApplicable(field.getType(), value);
        }
    }

    private Object transformStringWithTransformer(Field field, String value) {
        Class<? extends IValueTransformerProcessor<Object>> processorClass;
        Object fieldValue = value;
        for(Annotation annotation : annotationHelper.getAnnotationsAnnotatedWith(field.getDeclaredAnnotations(), ValueTransformerAnnotation.class)){
            log.debug(String.format("transorming string value(s) for field %s with %s annotation", field.getName(), annotation.annotationType()));

            processorClass = annotation.annotationType().getAnnotation(ValueTransformerAnnotation.class).value();
            IValueTransformerProcessor processor = (IValueTransformerProcessor)beanFactory.getBean(processorClass);

            fieldValue = processor.transformString(annotation,value);

        }
        return fieldValue;
    }

    private Object transformStringToPrimitiveIfApplicable(Class<?> targetType, String text) {
        PropertyEditor editor = PropertyEditorManager.findEditor(targetType);
        editor.setAsText(text);
        return editor.getValue();
    }
}