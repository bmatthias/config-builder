package com.tngtech.configbuilder.annotation.valueextractor;

import com.tngtech.configbuilder.util.ConfigBuilderFactory;

import java.lang.annotation.Annotation;

/**
 * Processes {@link SystemPropertyValue} annotations
 */
public class SystemPropertyProcessor implements ValueExtractorProcessor {
    public String getValue(Annotation annotation, ConfigBuilderFactory configBuilderFactory) {
        return System.getProperty(((SystemPropertyValue) annotation).value());
    }
}
