package com.tngtech.configbuilder.testclasses;

import com.tngtech.configbuilder.annotation.valueextractor.DefaultValue;

import java.lang.reflect.Field;

public class TestConfigThrowsIllegalArgumentException {

    @DefaultValue("stringValue")
    private Field integer;
}
