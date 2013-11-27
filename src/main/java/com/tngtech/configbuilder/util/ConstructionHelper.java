package com.tngtech.configbuilder.util;

import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.exception.ConfigBuilderException;
import com.tngtech.configbuilder.exception.NoConstructorFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ConstructionHelper<T> {

    private final static Logger log = LoggerFactory.getLogger(ConstructionHelper.class);

    private ErrorMessageSetup errorMessageSetup;

    public ConstructionHelper(ConfigBuilderFactory configBuilderFactory) {
        this.errorMessageSetup = configBuilderFactory.getInstance(ErrorMessageSetup.class);
    }

    public T getInstance(Class<T> configClass, Object... objects) {
        try {
            Constructor<T> tConstructor = findSuitableConstructor(configClass, objects);
            log.info(String.format("found constructor - instantiating %s", configClass.getName()));
            tConstructor.setAccessible(true);
            return tConstructor.newInstance(objects);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ConfigBuilderException(errorMessageSetup.getErrorMessage(e), e);
        }
    }

    @SuppressWarnings("unchecked")
    private Constructor<T> findSuitableConstructor(Class<T> configClass, Object... objects) {
        log.debug(String.format("trying to find a constructor for %s matching the arguments of build()", configClass.getName()));
        Constructor[] constructors = configClass.getDeclaredConstructors();
        for (Constructor<T> constructor : constructors) {
            if (constructorIsMatching(constructor, objects)) {
                return constructor;
            }
        }
        throw new NoConstructorFoundException(errorMessageSetup.getErrorMessage(NoConstructorFoundException.class));
    }

    private boolean constructorIsMatching(Constructor<T> constructor, Object... objects) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        if (parameterTypes.length != objects.length) {
            return false;
        } else {
            boolean constructorIsMatching = true;
            for (int i = 0; i < parameterTypes.length; i++) {
                constructorIsMatching &= parameterTypes[i].isAssignableFrom(objects[i].getClass());
            }
            return constructorIsMatching;
        }
    }
}
