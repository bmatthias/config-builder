package com.tngtech.configbuilder.annotation.valueextractor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to specify system properties.<br>
 * <b>Usage:</b> <code>@SystemPropertyValue("property.key")</code>
 */
@ValueExtractorAnnotation(SystemPropertyProcessor.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SystemPropertyValue {
    String value();
}
