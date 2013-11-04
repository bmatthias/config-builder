package com.tngtech.configbuilder.util;

import com.google.common.collect.Maps;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertiesFilesProcessor;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertyExtensionProcessor;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertyLocationsProcessor;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertySuffixProcessor;
import com.tngtech.configbuilder.annotation.typetransformer.CommaSeparatedStringToStringCollectionTransformer;
import com.tngtech.configbuilder.annotation.typetransformer.StringCollectionToCommaSeparatedStringTransformer;
import com.tngtech.configbuilder.annotation.typetransformer.StringToBooleanTransformer;
import com.tngtech.configbuilder.annotation.typetransformer.StringToIntegerTransformer;
import com.tngtech.configbuilder.annotation.valueextractor.*;
import com.tngtech.configbuilder.configuration.BuilderConfiguration;
import com.tngtech.configbuilder.configuration.ErrorMessageSetup;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.Map;

public class ConfigBuilderFactory {

    private Map<Class,Object> singletonMap = Maps.newHashMap();

    public <T> void initialize() {
        //Order is IMPORTANT!
        ErrorMessageSetup errorMessageSetup = new ErrorMessageSetup();
        singletonMap.put(ErrorMessageSetup.class, errorMessageSetup);
        AnnotationHelper annotationHelper = new AnnotationHelper();
        singletonMap.put(AnnotationHelper.class, annotationHelper);
        FieldValueExtractor fieldValueExtractor = new FieldValueExtractor(this);
        singletonMap.put(FieldValueExtractor.class, fieldValueExtractor);
        FieldValueTransformer fieldValueTransformer = new FieldValueTransformer(this);
        singletonMap.put(FieldValueTransformer.class, fieldValueTransformer);

        //configuration
        singletonMap.put(BuilderConfiguration.class, new BuilderConfiguration());

        //util
        singletonMap.put(PropertyLoaderConfigurator.class, new PropertyLoaderConfigurator(annotationHelper, this));
        singletonMap.put(ConstructionHelper.class, new ConstructionHelper<T>(errorMessageSetup));
        singletonMap.put(FieldSetter.class, new FieldSetter<T>(fieldValueTransformer, errorMessageSetup, annotationHelper));
        singletonMap.put(ConfigValidator.class, new ConfigValidator<T>(this, errorMessageSetup, annotationHelper));
        singletonMap.put(CommandLineHelper.class, new CommandLineHelper(this, annotationHelper, errorMessageSetup));

        //AnnotationProcessors
        singletonMap.put(SystemPropertyProcessor.class, new SystemPropertyProcessor());
        singletonMap.put(PropertyValueProcessor.class, new PropertyValueProcessor());
        singletonMap.put(EnvironmentVariableProcessor.class, new EnvironmentVariableProcessor());
        singletonMap.put(CommandLineValueProcessor.class, new CommandLineValueProcessor());
        singletonMap.put(PropertySuffixProcessor.class, new PropertySuffixProcessor());
        singletonMap.put(PropertyLocationsProcessor.class, new PropertyLocationsProcessor());
        singletonMap.put(PropertyExtensionProcessor.class, new PropertyExtensionProcessor());
        singletonMap.put(PropertiesFilesProcessor.class, new PropertiesFilesProcessor());
        singletonMap.put(DefaultValueProcessor.class, new DefaultValueProcessor());
        
        //Transformers
        singletonMap.put(StringToBooleanTransformer.class, new StringToBooleanTransformer());
        singletonMap.put(StringToIntegerTransformer.class, new StringToIntegerTransformer());
        singletonMap.put(StringCollectionToCommaSeparatedStringTransformer.class, new StringCollectionToCommaSeparatedStringTransformer());
        singletonMap.put(CommaSeparatedStringToStringCollectionTransformer.class, new CommaSeparatedStringToStringCollectionTransformer());
    }

    public <K> K getInstance(Class<K> clazz) {
        return (K)singletonMap.get(clazz);
    }

    public <K> K createInstance(Class<K> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ValidatorFactory getValidatorFactory() {
        return Validation.buildDefaultValidatorFactory();
    }
}
