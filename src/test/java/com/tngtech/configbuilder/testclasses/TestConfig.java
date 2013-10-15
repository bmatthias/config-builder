package com.tngtech.configbuilder.testclasses;

import com.google.common.collect.Lists;
import com.tngtech.configbuilder.FieldValueProvider;
import com.tngtech.configbuilder.annotation.configuration.CollectionType;
import com.tngtech.configbuilder.annotation.configuration.LoadingOrder;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertiesFiles;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertyExtension;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertyLocations;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertySuffixes;
import com.tngtech.configbuilder.annotation.validation.Validation;
import com.tngtech.configbuilder.annotation.valueextractor.*;
import com.tngtech.configbuilder.annotation.valuetransformer.ValueTransformer;
import com.tngtech.propertyloader.PropertyLoader;

import java.util.*;

@PropertyExtension("testproperties")
@PropertySuffixes(extraSuffixes = {"test"})
@PropertyLocations(resourcesForClasses = {PropertyLoader.class})
@PropertiesFiles("demoapp-configuration")
@LoadingOrder(value = {CommandLineValue.class, PropertyValue.class, EnvironmentVariableValue.class, SystemPropertyValue.class, DefaultValue.class})
public class TestConfig {

    public TestConfig(){

    }

    public static class PidFixFactory implements FieldValueProvider<Collection<String>> {
        public Collection<String> getValue(String optionValue) {
            Collection<String> coll = Lists.newArrayList();
            coll.add(optionValue + " success");
            return coll;
        }
    }

    @DefaultValue("3")
    private int someNumber;

    @PropertyValue("a")
    private String helloWorld;

    @CommandLineValue(shortOpt = "u", longOpt = "user")
    private boolean aBoolean;

    @LoadingOrder(value = {CommandLineValue.class})
    @CommandLineValue(shortOpt = "c", longOpt = "collection", hasArg = true, description = "command line option description")
    @ValueTransformer(PidFixFactory.class)
    private Collection<String> stringCollection;

    @CollectionType
    @DefaultValue("one,two")
    @ValueTransformer(PidFixFactory.class)
    private ArrayList<Integer> list;

    @EnvironmentVariableValue("PATH")
    private String environmentVariable;

    @SystemPropertyValue("user.language")
    private String systemProperty;

    public void setSomeNumber(Integer someNumber) {
        this.someNumber = someNumber;
    }

    public void setHelloWorld(String helloWorld) {
        this.helloWorld = helloWorld;
    }

    public void setBoolean(boolean aBoolean) {
        this.aBoolean = aBoolean;
    }

    public void setStringCollection(Collection<String> stringCollection) {
        this.stringCollection = stringCollection;
    }

    public void setList(ArrayList list) {
        this.list = list;
    }

    public void setEnvironmentVariable(String environmentVariable) {
        this.environmentVariable = environmentVariable;
    }

    public void setSystemProperty(String systemProperty) {
        this.systemProperty = systemProperty;
    }

    @Validation
    private void validate(){

    }
}
