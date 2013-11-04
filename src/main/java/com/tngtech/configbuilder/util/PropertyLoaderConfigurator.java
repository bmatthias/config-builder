package com.tngtech.configbuilder.util;


import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.IPropertyLoaderConfigurationProcessor;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertyLoaderConfigurationAnnotation;
import com.tngtech.propertyloader.PropertyLoader;

import java.lang.annotation.Annotation;

public class PropertyLoaderConfigurator {
    private AnnotationHelper annotationHelper;
    private ConfigBuilderFactory configBuilderFactory;

    public PropertyLoaderConfigurator(ConfigBuilderFactory configBuilderFactory) {
        this.annotationHelper = configBuilderFactory.getInstance(AnnotationHelper.class);
        this.configBuilderFactory = configBuilderFactory;
    }

    public PropertyLoader configurePropertyLoader(Class<?> configClass) {

        PropertyLoader propertyLoader = configBuilderFactory.createInstance(PropertyLoader.class).withDefaultConfig();
        for (Annotation annotation : annotationHelper.getAnnotationsAnnotatedWith(configClass.getDeclaredAnnotations(), PropertyLoaderConfigurationAnnotation.class)) {
            Class<? extends IPropertyLoaderConfigurationProcessor> processorClass = annotation.annotationType().getAnnotation(PropertyLoaderConfigurationAnnotation.class).value();
            configBuilderFactory.getInstance(processorClass).configurePropertyLoader(annotation, propertyLoader);
        }
        return propertyLoader;
    }
}
