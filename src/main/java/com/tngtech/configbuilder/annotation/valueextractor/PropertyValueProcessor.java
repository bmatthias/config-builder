package com.tngtech.configbuilder.annotation.valueextractor;

import com.tngtech.configbuilder.configuration.BuilderConfiguration;
import com.tngtech.configbuilder.util.ConfigBuilderFactory;

import java.lang.annotation.Annotation;
import java.util.Properties;

/**
 * Processes {@link PropertyValue} annotations
 */
public class PropertyValueProcessor implements ValueExtractorProcessor {

    public String getValue(Annotation annotation, ConfigBuilderFactory configBuilderFactory) {
        BuilderConfiguration builderConfiguration = configBuilderFactory.getInstance(BuilderConfiguration.class);

        final Properties properties = builderConfiguration.getProperties();
        final String propertyName = ((PropertyValue) annotation).value();

        for (final String propertyNamePrefix : builderConfiguration.getPropertyNamePrefixes()) {
            final String fullPropertyName = propertyNamePrefix + propertyName;
            if (properties.containsKey(fullPropertyName)) {
                return properties.getProperty(fullPropertyName);
            }
        }
        return null;
    }
}
