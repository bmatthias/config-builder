package com.tngtech.configbuilder.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ClassCastingHelper {

    private final Map<Class, Class> primitiveToWrapperMapping;
    
    public ClassCastingHelper() {
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

    public Class getWrapperClassForPrimitive(Class primitiveClass) {
        return primitiveToWrapperMapping.get(primitiveClass);
    }

    public Class castTypeToClass(Type object) {
        if(object.getClass().equals(Class.class)) {
            return (Class<?>) object;
        } else {
            return (Class<?>) ((ParameterizedType) object).getRawType();
        }
    }
}
