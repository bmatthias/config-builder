package com.tngtech.configbuilder.annotation.valueextractor;

import com.tngtech.configbuilder.util.ConfigBuilderFactory;

import java.lang.annotation.Annotation;

/**
 * Processes DefaultValue annotations, implements ValueExtractorProcessor
 */
public class DefaultValueProcessor implements ValueExtractorProcessor {

    public String getValue(Annotation annotation, ConfigBuilderFactory configBuilderFactory) {
        return ((DefaultValue) annotation).value();
    }
}
