package com.tngtech.configbuilder.annotation.configuration;

import java.lang.annotation.*;

/**
 * This annotation is used to specify additional prefixes to be included in the search for property names
 * <b>Usage:</b> <code>@PropertyNamePrefix(value = {"test.prefix."})</code>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyNamePrefix {
    String[] value() default {""};
}
