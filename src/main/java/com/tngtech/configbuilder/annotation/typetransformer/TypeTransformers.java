package com.tngtech.configbuilder.annotation.typetransformer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation uses a lists of classes that implements the {@link TypeTransformer} interface in order to transform the type of the annotated field.<br>
 * <b>Usage:</b> <code>@TypeTransformer({AClassThatImplementsITypeTransformer.class, AnotherClassThatImplementsITypeTransofrmer})</code>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TypeTransformers {
    public Class[] value();
}
