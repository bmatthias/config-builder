package com.tngtech.configbuilder.annotation.valueextractor;

import com.tngtech.configbuilder.util.ConfigBuilderFactory;

import java.lang.annotation.Annotation;

/**
 * Processes {@link EnvironmentVariableValue} annotations
 */
public class EnvironmentVariableProcessor implements ValueExtractorProcessor {
    public String getValue(Annotation annotation, ConfigBuilderFactory configBuilderFactory) {
        return System.getenv(((EnvironmentVariableValue) annotation).value());
    }
}
