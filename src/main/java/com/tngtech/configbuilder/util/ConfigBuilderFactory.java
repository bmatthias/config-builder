package com.tngtech.configbuilder.util;

import com.google.common.collect.Maps;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertiesFilesProcessor;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertyExtensionProcessor;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertyLocationsProcessor;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertySuffixProcessor;
import com.tngtech.configbuilder.annotation.typetransformer.*;
import com.tngtech.configbuilder.annotation.valueextractor.*;
import com.tngtech.configbuilder.configuration.BuilderConfiguration;
import com.tngtech.configbuilder.configuration.ErrorMessageSetup;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.lang.reflect.Constructor;
import java.util.Map;

public class ConfigBuilderFactory {

    private Map<Class,Object> singletonMap = Maps.newHashMap();

    public <T> void initialize() {

        //Order is IMPORTANT!

        //configuration
        singletonMap.put(ErrorMessageSetup.class, new ErrorMessageSetup());
        singletonMap.put(BuilderConfiguration.class, new BuilderConfiguration());

        //util
        singletonMap.put(AnnotationHelper.class, new AnnotationHelper());
        singletonMap.put(GenericsAndCastingHelper.class, new GenericsAndCastingHelper());
        singletonMap.put(FieldValueExtractor.class, new FieldValueExtractor(this));
        singletonMap.put(FieldValueTransformer.class, new FieldValueTransformer(this));
        singletonMap.put(PropertyLoaderConfigurator.class, new PropertyLoaderConfigurator(this));
        singletonMap.put(ConstructionHelper.class, new ConstructionHelper<T>(this));
        singletonMap.put(FieldSetter.class, new FieldSetter<T>(this));
        singletonMap.put(ConfigValidator.class, new ConfigValidator<T>(this));
        singletonMap.put(CommandLineHelper.class, new CommandLineHelper(this));

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

        //TypeTransformers
        singletonMap.put(StringOrPrimitiveToPrimitiveTransformer.class, new StringOrPrimitiveToPrimitiveTransformer());
        singletonMap.put(CollectionTransformer.class, new CollectionTransformer());
        singletonMap.put(CommaSeparatedStringToStringCollectionTransformer.class, new CommaSeparatedStringToStringCollectionTransformer());
        singletonMap.put(StringCollectionToCommaSeparatedStringTransformer.class, new StringCollectionToCommaSeparatedStringTransformer());
        singletonMap.put(StringToPathTransformer.class, new StringToPathTransformer());

        //other
        singletonMap.put(ValidatorFactory.class, Validation.buildDefaultValidatorFactory());
    }

    public <K> K getInstance(Class<K> clazz) {
        if(singletonMap.get(clazz) != null) {
            return (K)singletonMap.get(clazz);
        }
        else {
            return createInstance(clazz);
        }
    }

    //TODO: Make this work if config class has no default constructor & better exception
    public <K> K createInstance(Class<K> clazz) {
        try {
            return clazz.newInstance();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            Class superClass = clazz.getDeclaringClass();
            try {
                Object superInstance = superClass.newInstance();
                Constructor<K> constructor = clazz.getConstructor(superClass);
                return constructor.newInstance(superInstance);
            } catch (Exception e1) {
                throw new RuntimeException(e1);
            }
        }
    }
}
