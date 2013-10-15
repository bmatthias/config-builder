package com.tngtech.configbuilder.annotation.valueextractor;


import com.tngtech.configbuilder.configuration.BuilderConfiguration;

import java.lang.annotation.Annotation;

public class CommandLineValueProcessor implements IValueExtractorProcessor {

    public String getValue(Annotation annotation, BuilderConfiguration builderConfiguration) {
        if (((CommandLineValue) annotation).hasArg()) {
            return builderConfiguration.getCommandLine().getOptionValue(((CommandLineValue) annotation).shortOpt());
        } else {
            return String.valueOf(builderConfiguration.getCommandLine().hasOption(((CommandLineValue) annotation).shortOpt()) || builderConfiguration.getCommandLine().hasOption(((CommandLineValue) annotation).longOpt()));
        }
    }
}
