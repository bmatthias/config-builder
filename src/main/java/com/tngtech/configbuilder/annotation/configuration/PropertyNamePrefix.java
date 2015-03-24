package com.tngtech.configbuilder.annotation.configuration;

import com.tngtech.configbuilder.annotation.valueextractor.CommandLineValue;
import com.tngtech.configbuilder.annotation.valueextractor.DefaultValue;
import com.tngtech.configbuilder.annotation.valueextractor.PropertyValue;

import java.lang.annotation.*;

/**
 * This annotation is used to specify additional prefixes to be included in the search for property names
 * <b>Usage:</b> <code>@PropertyNamePrefix(value = {"test.prefix."})</code>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyNamePrefix {
    public String[] value() default {""};

}
