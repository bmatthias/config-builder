package com.tngtech.configbuilder.testclasses;

import com.tngtech.configbuilder.annotation.valueextractor.DefaultValue;
import com.tngtech.configbuilder.annotation.valueextractor.ImportedValue;

public class ExtendedTestConfig extends TestConfig {
    @DefaultValue("4")
    @ImportedValue("additionalNumber")
    private int additionalNumber;

    @DefaultValue("5")
    @ImportedValue("someNumber")
    private int someNumber;

    public int getAdditionalNumber() {
        return additionalNumber;
    }

    @Override
    public Integer getSomeNumber() {
        return someNumber;
    }
    
    public Integer getSuperSomeNumber() {
        return super.getSomeNumber();
    }
}
