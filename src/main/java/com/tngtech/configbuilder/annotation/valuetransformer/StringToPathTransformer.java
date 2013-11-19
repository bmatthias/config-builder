package com.tngtech.configbuilder.annotation.valuetransformer;

import java.nio.file.Path;
import java.nio.file.Paths;

public class StringToPathTransformer extends ValueTransformer<String, Path> {
    @Override
    public Path transform(String argument) {
        return Paths.get(argument);
    }
}
