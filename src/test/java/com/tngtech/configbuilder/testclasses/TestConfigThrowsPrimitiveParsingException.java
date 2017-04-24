package com.tngtech.configbuilder.testclasses;

import com.tngtech.configbuilder.annotation.valueextractor.DefaultValue;

public class TestConfigThrowsPrimitiveParsingException {

    @DefaultValue("stringValue")
    private int integer;
}
