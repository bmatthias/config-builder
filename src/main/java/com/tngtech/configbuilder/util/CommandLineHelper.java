package com.tngtech.configbuilder.util;

import com.tngtech.configbuilder.annotation.valueextractor.CommandLineValue;
import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.exception.ConfigBuilderException;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;

public class CommandLineHelper {

    private final static Logger log = Logger.getLogger(CommandLineHelper.class);

    private final ConfigBuilderFactory configBuilderFactory;
    private final AnnotationHelper annotationHelper;
    private final ErrorMessageSetup errorMessageSetup;

    public CommandLineHelper(ConfigBuilderFactory configBuilderFactory, AnnotationHelper annotationHelper, ErrorMessageSetup errorMessageSetup) {
        this.configBuilderFactory = configBuilderFactory;
        this.annotationHelper = annotationHelper;
        this.errorMessageSetup = errorMessageSetup;
    }

    public CommandLine getCommandLine(Class configClass, String[] args) {
        log.info("getting command line options from fields and parsing command line arguments");
        Options options = getOptions(configClass);
        return parseCommandLine(args, options);
    }

    public Options getOptions(Class configClass) {
        Options options = configBuilderFactory.createInstance(Options.class);
        for (Field field : annotationHelper.getFieldsAnnotatedWith(configClass, CommandLineValue.class)) {
            options.addOption(getOption(field));
        }
        return options;
    }

    @SuppressWarnings("AccessStaticViaInstance")
    private Option getOption(Field field) {
        CommandLineValue commandLineValue = field.getAnnotation(CommandLineValue.class);
        log.debug(String.format("adding command line option %s for field %s", commandLineValue.shortOpt(), field.getName()));
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