package com.tngtech.configbuilder.annotation.valueextractor;

import com.tngtech.configbuilder.configuration.BuilderConfiguration;
import com.tngtech.configbuilder.util.ConfigBuilderFactory;
import org.apache.commons.cli.CommandLine;

import java.lang.annotation.Annotation;

/**
 * Processes CommandLineValue annotations, implements ValueExtractorProcessor
 */
public class CommandLineValueProcessor implements ValueExtractorProcessor {

    public String getValue(Annotation annotation, ConfigBuilderFactory configBuilderFactory) {
        CommandLine commandLine = configBuilderFactory.getInstance(BuilderConfiguration.class).getCommandLine();
        CommandLineValue commandLineValue = (CommandLineValue) annotation;
        if (commandLineValue.hasArg()) {
            return commandLine.getOptionValue(commandLineValue.shortOpt());
        } else {
            boolean hasOption = commandLine.hasOption(commandLineValue.shortOpt()) || commandLine.hasOption(commandLineValue.longOpt());
            return String.valueOf(hasOption);
        }
    }
}
