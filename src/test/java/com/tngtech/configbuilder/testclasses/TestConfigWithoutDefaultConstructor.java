package com.tngtech.configbuilder.testclasses;

import com.tngtech.configbuilder.annotation.configuration.LoadingOrder;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertiesFiles;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertyExtension;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertyLocations;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertySuffixes;
import com.tngtech.propertyloader.PropertyLoader;

@PropertyExtension("testproperties")
@PropertySuffixes(extraSuffixes = {"test"})
@PropertyLocations(resourcesForClasses = {PropertyLoader.class})
@PropertiesFiles("demoapp-configuration")
@LoadingOrder()
public class TestConfigWithoutDefaultConstructor {

    private int number;

    public TestConfigWithoutDefaultConstructor(Integer i) {
        this.number = i;
    }

    public int getNumber() {
        return number;
    }
}
