package com.tngtech.configbuilder.configuration;

import com.tngtech.configbuilder.annotation.valueextractor.*;
import org.apache.commons.cli.CommandLine;

import java.lang.annotation.Annotation;
import java.util.Properties;

/**
 * Stores the configuration for the ConfigBuilder, i.e. the CommandLine, the Properties and the global annotation processing order.
 */
public class BuilderConfiguration {

    private Properties properties;
    private CommandLine commandLine;
    private Class<? extends Annotation>[] annotationOrder = new Class[]{PropertyValue.class, EnvironmentVariableValue.class, SystemPropertyValue.class, DefaultValue.class};

    public BuilderConfiguration() {
        properties = new Properties();
        commandLine = null;
    }

    public CommandLine getCommandLine() {
        return commandLine;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setCommandLine(CommandLine commandLine) {
        this.commandLine = commandLine;
        this.annotationOrder = new Class[]{CommandLineValue.class, PropertyValue.class, EnvironmentVariableValue.class, SystemPropertyValue.class, DefaultValue.class};
    }

    public void setAnnotationOrder(Class<? extends Annotation>[] annotationOrder) {
        this.annotationOrder = annotationOrder;
    }

    public Class<? extends Annotation>[] getAnnotationOrder() {
        return annotationOrder;
    }
}
