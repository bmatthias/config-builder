package com.tngtech.configbuilder.annotation.typetransformer;


/**
 * Implementations of this interface transform an object into a different type of object
 * @param <SourceClass> the type of the parameter before the transformation
 * @param <TargetClass> return type
 */
public interface ITypeTransformer<SourceClass, TargetClass> {
    public TargetClass transform(SourceClass argument);
}
