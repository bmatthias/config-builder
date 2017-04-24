package com.tngtech.configbuilder.annotation.configuration;

import com.tngtech.configbuilder.annotation.valueextractor.CommandLineValue;
import com.tngtech.configbuilder.annotation.valueextractor.DefaultValue;
import com.tngtech.configbuilder.annotation.valueextractor.EnvironmentVariableValue;
import com.tngtech.configbuilder.annotation.valueextractor.ImportedValue;
import com.tngtech.configbuilder.annotation.valueextractor.PropertyValue;
import com.tngtech.configbuilder.annotation.valueextractor.SystemPropertyValue;
import com.tngtech.configbuilder.annotation.valueextractor.ValueExtractorAnnotation;

import java.lang.annotation.*;

/**
 * This annotation is used to specify the order in which the {@link ValueExtractorAnnotation} annotations
 * {@link CommandLineValue}, {@link PropertyValue}, {@link EnvironmentVariableValue},
 * {@link SystemPropertyValue}, {@link ImportedValue} and {@link DefaultValue} are processed.
 * It can specify the order for a certain field if placed on the field, or a global order if placed on the config class.
 * The annotations are processed top-down until a string value is found, i.e. the order is from the most important to the least important.
 * The {@code LoadingOrder} may only contain the aforementioned {@link ValueExtractorAnnotation} classes.<br>
 * <b>Usage:</b> <code>@LoadingOrder(value = {PropertyValue.class, CommandLineValue.class, DefaultValue.class})</code>
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LoadingOrder {
    Class<? extends Annotation>[] value() default {CommandLineValue.class, PropertyValue.class, DefaultValue.class};
}
