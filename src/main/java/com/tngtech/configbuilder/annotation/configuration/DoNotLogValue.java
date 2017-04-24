package com.tngtech.configbuilder.annotation.configuration;

import com.tngtech.configbuilder.ConfigBuilder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to prevent a field's value from being logged
 * when {@link ConfigBuilder#build(Object...)} builds a config instance.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DoNotLogValue {
}
