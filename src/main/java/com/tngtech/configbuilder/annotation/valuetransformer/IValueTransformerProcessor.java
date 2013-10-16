package com.tngtech.configbuilder.annotation.valuetransformer;


import java.lang.annotation.Annotation;

/**
 * Implementations of this interface transform a String value to any Object
 * @param <T> return type
 */
public interface IValueTransformerProcessor<T> {
    public T transformString(Annotation annotation, String argument);
}
