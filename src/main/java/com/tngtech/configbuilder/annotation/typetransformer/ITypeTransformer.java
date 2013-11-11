package com.tngtech.configbuilder.annotation.typetransformer;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Implementations of this interface transform an object into a different type of object
 * @param <SourceClass> the type of the parameter before the transformation
 * @param <TargetClass> return type
 */
public abstract class ITypeTransformer<SourceClass, TargetClass> {
    public abstract TargetClass transform(SourceClass argument);

    public boolean isMatching(Class<?> sourceClass, Class<?> targetClass) {
        Type typeOfInterface = this.getClass().getGenericSuperclass();
        Type[] genericTypes = ((ParameterizedType) typeOfInterface).getActualTypeArguments();

        Class transformerSourceClass = castTypeToClass(genericTypes[0]);
        Class transformerTargetClass = castTypeToClass(genericTypes[1]);
        return transformerSourceClass.isAssignableFrom(sourceClass) && targetClass.isAssignableFrom(transformerTargetClass);
    }

    private Class castTypeToClass(Type object) {
        if(object.getClass().equals(Class.class)) {
            return (Class<?>) object;
        } else {
            return (Class<?>) ((ParameterizedType) object).getRawType();
        }
    }
}
