package com.tngtech.configbuilder.annotation.valuetransformer;

import com.tngtech.configbuilder.FieldValueProvider;
import com.tngtech.configbuilder.exception.ValueTransformerException;

import java.lang.annotation.Annotation;

public class ValueTransformerProcessor implements IValueTransformerProcessor<Object> {

    public Object transformString(Annotation annotation, String fieldString) {
        Class<? extends FieldValueProvider> valueProvidingClass = ((ValueTransformer) annotation).value();
        try {
            return valueProvidingClass.newInstance().getValue(fieldString);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new ValueTransformerException(e);
        }
    }
}
