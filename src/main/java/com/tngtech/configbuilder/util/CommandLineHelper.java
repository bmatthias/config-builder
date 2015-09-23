package com.tngtech.configbuilder.util;

import com.tngtech.configbuilder.annotation.valueextractor.CommandLineValue;
import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.exception.ConfigBuilderException;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class CommandLineHelper {

    private final static Logger log = LoggerFactory.getLogger(CommandLineHelper.class);

    private final ConfigBuilderFactory configBuilderFactory;
    private final AnnotationHelper annotationHelper;
    private final ErrorMessageSetup errorMessageSetup;

    public CommandLineHelper(ConfigBuilderFactory configBuilderFactory) {
        this.configBuilderFactory = configBuilderFactory;
        this.annotationHelper = configBuilderFactory.getInstance(AnnotationHelper.class);
        this.errorMessageSetup = configBuilderFactory.getInstance(ErrorMessageSetup.class);
    }

    public CommandLine getCommandLine(Class configClass, String[] args) {
        log.info("getting command line options from fields and parsing command line arguments");
        Options options = getOptions(configClass);
        return parseCommandLine(args, options);
    }

    public Options getOptions(Class configClass) {
        Options options = configBuilderFactory.createInstance(Options.class);
        for (Field field : annotationHelper.getFieldsAnnotatedWith(configClass, CommandLineValue.class)) {
            if (field.isSynthetic()) {
                continue;
            }
            options.addOption(getOption(field));
        }
        return options;
    }

    @SuppressWarnings("AccessStaticViaInstance")
    private Option getOption(Field field) {
        CommandLineValue commandLineValue = field.getAnnotation(CommandLineValue.class);
        log.debug("adding command line option {} for field {}", commandLineValue.shortOpt(), field.getName());
        return OptionBuilder.withLongOpt(commandLineValue.longOpt())
                .hasArg()
                .isRequired(commandLineValue.required())
                .withDescription(commandLineValue.description())
                .hasArg(commandLineValue.hasArg())
                .create(commandLineValue.shortOpt());
    }

    private CommandLine parseCommandLine(String[] args, Options options) {
        CommandLine commandLine;
        try {
            commandLine = configBuilderFactory.createInstance(GnuParser.class).parse(options, args);
        } catch (ParseException e) {
            throw new ConfigBuilderException(errorMessageSetup.getErrorMessage(e.getClass().getSuperclass()), e);
        }
        return commandLine;
    }
}