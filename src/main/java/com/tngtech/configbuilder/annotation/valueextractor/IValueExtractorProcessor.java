package com.tngtech.configbuilder.annotation.valueextractor;


import com.tngtech.configbuilder.configuration.BuilderConfiguration;

import java.lang.annotation.Annotation;

/**
 * This interface is implemented by annotation processors that get String values from annotations.
 */
public interface IValueExtractorProcessor {
    public String getValue(Annotation annotation, BuilderConfiguration argument);
}
