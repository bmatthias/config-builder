package com.tngtech.configbuilder.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GenericsAndCastingHelper {

    private final Map<Class<?>, Class<?>> primitiveToWrapperMapping;
    
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

    public Class<?> getWrapperClassIfPrimitive(Class clazz) {
        return primitiveToWrapperMapping.get(clazz) == null? clazz : primitiveToWrapperMapping.get(clazz);
    }

    public Class<?> castTypeToClass(Type type) {
        if(type.getClass().equals(Class.class)) {
            return (Class<?>) type;
        } else {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        }
    }

    public boolean typesMatch(Object sourceValue, Type targetType) {
        if(sourceValue == null) {
            return true;
        }
        Class<?> sourceClass = getWrapperClassIfPrimitive(sourceValue.getClass());
        if(targetType.getClass().equals(Class.class)) {
            return getWrapperClassIfPrimitive((Class<?>) targetType).isAssignableFrom(sourceClass);
        }
        else if(Collection.class.isAssignableFrom((Class<?>)((ParameterizedType)targetType).getRawType())) {
            if(Collection.class.isAssignableFrom(sourceClass)) {
                Class<?> typeArgument = (Class<?>)((ParameterizedType) targetType).getActualTypeArguments()[0];
                for(Object item : (Collection)sourceValue) {
                    if(!typeArgument.isAssignableFrom(item.getClass())) {
                        return false;
                    }
                }
                return true;
            }
            else {
                return false;
            }
        }
        return (castTypeToClass(targetType)).isAssignableFrom(sourceClass);
    }

    public boolean isPrimitiveOrWrapper(Class targetClass) {
        return primitiveToWrapperMapping.containsKey(targetClass) || primitiveToWrapperMapping.containsValue(targetClass);
    }
}
