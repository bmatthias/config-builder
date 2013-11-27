package com.tngtech.configbuilder.annotation.valueextractor;

import com.tngtech.configbuilder.configuration.BuilderConfiguration;
import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.exception.ImportedConfigurationException;
import com.tngtech.configbuilder.util.ConfigBuilderFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class ImportedValueProcessor implements ValueExtractorProcessor {

    @Override
    public Object getValue(Annotation annotation, ConfigBuilderFactory configBuilderFactory) {
        BuilderConfiguration builderConfiguration = configBuilderFactory.getInstance(BuilderConfiguration.class);
        Object importedConfiguration = builderConfiguration.getImportedConfiguration();

        if(importedConfiguration == null) {
            return null;
        }
        
        String fieldName = ((ImportedValue) annotation).value();
        Object result;

        try {
            Field field = importedConfiguration.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            result = field.get(importedConfiguration);
        } catch (NoSuchFieldException e) {
            throw createException(configBuilderFactory, fieldName);
        } catch (IllegalAccessException e) {
            throw createException(configBuilderFactory, fieldName);
        }

        return result;
    }

    private ImportedConfigurationException createException(ConfigBuilderFactory configBuilderFactory, String fieldName) {
        ErrorMessageSetup errorMessageSetup = configBuilderFactory.getInstance(ErrorMessageSetup.class);
        return new ImportedConfigurationException(errorMessageSetup.getErrorMessage(ImportedConfigurationException.class, fieldName));
    }
}
