package com.tngtech.configbuilder.annotation.valueextractor;

import com.tngtech.configbuilder.configuration.BuilderConfiguration;

import java.lang.annotation.Annotation;

public class EnvironmentVariableProcessor implements IValueExtractorProcessor {
    public String getValue(Annotation annotation, BuilderConfiguration builderConfiguration) {
        return System.getenv(((EnvironmentVariableValue) annotation).value());
    }
}
