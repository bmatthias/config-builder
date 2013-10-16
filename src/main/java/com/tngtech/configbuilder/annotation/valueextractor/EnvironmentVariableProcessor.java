package com.tngtech.configbuilder.annotation.valueextractor;

import com.tngtech.configbuilder.configuration.BuilderConfiguration;

import java.lang.annotation.Annotation;

/**
 * Processes EnvironmentVariableValue annotations, implements IValueExtractorProcessor
 */
public class EnvironmentVariableProcessor implements IValueExtractorProcessor {
    public String getValue(Annotation annotation, BuilderConfiguration builderConfiguration) {
        return System.getenv(((EnvironmentVariableValue) annotation).value());
    }
}
