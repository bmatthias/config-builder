package com.tngtech.configbuilder.annotation.propertyloaderconfiguration;

import com.tngtech.propertyloader.PropertyLoader;

import java.lang.annotation.Annotation;

public interface PropertyLoaderConfigurationProcessor {
    void configurePropertyLoader(Annotation annotation, PropertyLoader propertyLoader);
}
