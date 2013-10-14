package com.tngtech.configbuilder.annotation.valueextractor;

import com.tngtech.configbuilder.configuration.BuilderConfiguration;

import java.lang.annotation.Annotation;

public class SystemPropertyProcessor implements IValueExtractorProcessor {
    public String getValue(Annotation annotation, BuilderConfiguration builderConfiguration) {
        return System.getProperty(((SystemProperty) annotation).value());
    }
}
