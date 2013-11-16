package com.tngtech.configbuilder.exception;

public class FactoryInstantiationException extends RuntimeException {
    public FactoryInstantiationException(String errorMessage) {
        super(errorMessage);
    }
}
