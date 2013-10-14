package com.tngtech.configbuilder.annotation.valueextractor;


import com.tngtech.configbuilder.configuration.BuilderConfiguration;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

@Component
public class CommandLineValueProcessor  implements IValueExtractorProcessor {

    public String getValue(Annotation annotation, BuilderConfiguration builderConfiguration) {
        if(((CommandLineValue)annotation).hasArg()) {
            return builderConfiguration.getCommandLine().getOptionValue(((CommandLineValue)annotation).shortOpt());
        }
        else {
            return String.valueOf(builderConfiguration.getCommandLine().hasOption(((CommandLineValue)annotation).shortOpt()) || builderConfiguration.getCommandLine().hasOption(((CommandLineValue)annotation).longOpt()));
        }
    }
}
