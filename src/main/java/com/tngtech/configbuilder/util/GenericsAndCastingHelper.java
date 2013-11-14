package com.tngtech.configbuilder.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GenericsAndCastingHelper {

    private final Map<Class, Class> primitiveToWrapperMapping;
    
    public GenericsAndCastingHelper() {
        primitiveToWrapperMapping = new HashMap<>();
        primitiveToWrapperMapping.put(boolean.class, Boolean.class);
        primitiveToWrapperMapping.put(byte.class, Byte.class);
        primitiveToWrapperMapping.put(short.class, Short.class);
        primitiveToWrapperMapping.put(char.class, Character.class);
        primitiveToWrapperMapping.put(int.class, Integer.class);
        primitiveToWrapperMapping.put(long.class, Long.class);
        primitiveToWrapperMapping.put(float.class, Float.class);
        primitiveToWrapperMapping.put(double.class, Double.class);
    }

    public Class<?> getWrapperClassForPrimitive(Class primitiveClass) {
        return primitiveToWrapperMapping.get(primitiveClass) == null? primitiveClass : primitiveToWrapperMapping.get(primitiveClass);
    }

    public Class<?> castTypeToClass(Type object) {
        if(object.getClass().equals(Class.class)) {
            return (Class<?>) object;
        } else {
            return (Class<?>) ((ParameterizedType) object).getRawType();
        }
    }

    public boolean typesMatch(Object sourceValue, Type targetType) {
        if(sourceValue == null) {
            return !castTypeToClass(targetType).isPrimitive();
        }
        Class<?> sourceClass = getWrapperClassForPrimitive(sourceValue.getClass());
        if(targetType.getClass().equals(Class.class)) {
            return getWrapperClassForPrimitive((Class<?>)targetType).isAssignableFrom(sourceClass);
        }
        else if(Collection.class.isAssignableFrom((Class<?>)((ParameterizedType)targetType).getRawType())) {
            if(Collection.class.isAssignableFrom(sourceClass)) {
                Class<?> typeArgument = (Class<?>)((ParameterizedType) targetType).getActualTypeArguments()[0];
                for(Object object : (Collection)sourceValue) {
                    if(!typeArgument.isAssignableFrom(object.getClass())) {
                        return false;
                    }
                }
                return true;
            }
            else {
                return false;
            }
        }
        return ((Class<?>)targetType).isAssignableFrom(sourceClass);
    }

    public boolean isPrimitiveOrWrapper(Class targetClass) {
        return primitiveToWrapperMapping.containsKey(targetClass) || primitiveToWrapperMapping.containsValue(targetClass);
    }
}
