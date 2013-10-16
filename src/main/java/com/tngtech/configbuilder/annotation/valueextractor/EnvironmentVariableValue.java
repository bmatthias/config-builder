package com.tngtech.configbuilder.annotation.valueextractor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to specify environment variables.<br>
 * <b>Usage:</b> <code>@EnvironmentVariableValue("ENV_VAR")</code>
 */
@ValueExtractorAnnotation(EnvironmentVariableProcessor.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnvironmentVariableValue {
    String value();
}
