package com.tngtech.configbuilder.annotation.propertyloaderconfiguration;


import com.tngtech.propertyloader.PropertyLoader;

import java.lang.annotation.Annotation;

public class PropertyLocationsProcessor implements PropertyLoaderConfigurationProcessor {

    public void configurePropertyLoader(Annotation annotation, PropertyLoader propertyLoader) {
        propertyLoader.getLocations().clear();
        String[] locations = ((PropertyLocations) annotation).directories();
        for (String location : locations) {
            propertyLoader.atDirectory(location);
        }
        Class[] classes = ((PropertyLocations) annotation).resourcesForClasses();
        for (Class clazz : classes) {
            propertyLoader.atRelativeToClass(clazz);
        }
        if (((PropertyLocations) annotation).fromClassLoader()) {
            propertyLoader.atContextClassPath();
        }
    }
}
