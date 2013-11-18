package com.tngtech.configbuilder.annotation.typetransformer;

import java.nio.file.Path;
import java.nio.file.Paths;

public class StringToPathTransformer extends TypeTransformer<String, Path> {
    @Override
    public Path transform(String argument) {
        return Paths.get(argument);
    }
}
