package com.tngtech.configbuilder.annotation.propertyloaderconfiguration;


import com.tngtech.propertyloader.PropertyLoader;

import java.lang.annotation.Annotation;

public class PropertySuffixProcessor implements IPropertyLoaderConfigurationProcessor {

    public void configurePropertyLoader(Annotation annotation, PropertyLoader propertyLoader) {
        propertyLoader.getSuffixes().clear();
        String[] suffixes = ((PropertySuffixes) annotation).extraSuffixes();
        for (String suffix : suffixes) {
            propertyLoader.getSuffixes().addSuffix(suffix);
        }
        if (((PropertySuffixes) annotation).hostNames()) {
            propertyLoader.getSuffixes().addLocalHostNames();
        }
    }
}
