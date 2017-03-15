package com.tngtech.configbuilder.annotation.propertyloaderconfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to specify suffixes for the PropertyLoader.<br>
 * <b>Usage:</b> <code>@PropertySuffixes(extraSuffixes = {"suffix1","suffix2"}, hostNames = true)</code>
 */
@PropertyLoaderConfigurationAnnotation(PropertySuffixProcessor.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertySuffixes {
    String[] extraSuffixes() default {};

    boolean hostNames() default false;
}
