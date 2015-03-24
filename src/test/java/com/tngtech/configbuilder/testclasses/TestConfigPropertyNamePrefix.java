package com.tngtech.configbuilder.testclasses;

import com.tngtech.configbuilder.annotation.configuration.PropertyNamePrefix;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertiesFiles;
import com.tngtech.configbuilder.annotation.valueextractor.PropertyValue;

@PropertiesFiles("testPropertyNamePrefix")
@PropertyNamePrefix("test.prefix.")
public class TestConfigPropertyNamePrefix {

    @PropertyValue("foo")
    private String foo;

    public String getFoo() {
        return foo;
    }
}
