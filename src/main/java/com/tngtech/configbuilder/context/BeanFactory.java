package com.tngtech.configbuilder.context;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;

public class BeanFactory {
    public <T> T getBean(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException();
        }
    }

    public ValidatorFactory getValidatorFactory() {
        return Validation.buildDefaultValidatorFactory();
    }
}
