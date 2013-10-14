package com.tngtech.configbuilder.annotation.valueextractor;

import com.tngtech.configbuilder.configuration.BuilderConfiguration;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

@Component
public class EnvironmentVariableProcessor implements IValueExtractorProcessor {
    public String getValue(Annotation annotation, BuilderConfiguration builderConfiguration) {
        return System.getenv(((EnvironmentVariable)annotation).value());
    }
}
