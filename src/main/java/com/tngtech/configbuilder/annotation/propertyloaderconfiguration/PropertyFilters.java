package com.tngtech.configbuilder.annotation.propertyloaderconfiguration;

import com.tngtech.propertyloader.impl.interfaces.PropertyFilterContainer;
import com.tngtech.propertyloader.impl.interfaces.PropertyLoaderFilter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to specify the filters which the PropertyLoader applies to the properties files.<br>
 * <b>Usage:</b> <code>@PropertyFilters({Config.class})</code>
 */
@PropertyLoaderConfigurationAnnotation(PropertyFiltersProcessor.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyFilters {
    public Class<? extends PropertyLoaderFilter>[] value() default {};
}
