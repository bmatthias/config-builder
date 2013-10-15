package com.tngtech.configbuilder.annotation.propertyloaderconfiguration;

import com.tngtech.propertyloader.PropertyLoader;

import java.lang.annotation.Annotation;

public class PropertyExtensionProcessor implements IPropertyLoaderConfigurationProcessor {

    public void configurePropertyLoader(Annotation annotation, PropertyLoader propertyLoader) {
        String fileExtension = ((PropertyExtension) annotation).value();
        propertyLoader.withExtension(fileExtension);
    }
}
