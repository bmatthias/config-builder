package com.tngtech.configbuilder.annotation.typetransformer;

import java.io.File;

public class StringToFileTransformer extends TypeTransformer<String, File> {
    @Override
    public File transform(String argument) {
        return new File(argument);
    }
}
