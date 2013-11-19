package com.tngtech.configbuilder.testclasses;

import com.google.common.collect.Lists;
import com.tngtech.configbuilder.annotation.configuration.LoadingOrder;
import com.tngtech.configbuilder.annotation.configuration.Separator;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertiesFiles;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertyExtension;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertyLocations;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertySuffixes;
import com.tngtech.configbuilder.annotation.validation.Validation;
import com.tngtech.configbuilder.annotation.valueextractor.*;
import com.tngtech.configbuilder.annotation.valuetransformer.CharacterSeparatedStringToStringListTransformer;
import com.tngtech.configbuilder.annotation.valuetransformer.ValueTransformer;
import com.tngtech.configbuilder.annotation.valuetransformer.ValueTransformers;
import com.tngtech.propertyloader.PropertyLoader;

import java.nio.file.Path;
import java.util.*;

@PropertyExtension("testproperties")
@PropertySuffixes(extraSuffixes = {"test"})
@PropertyLocations(resourcesForClasses = {PropertyLoader.class})
@PropertiesFiles("demoapp-configuration")
public class TestConfig {

    public TestConfig() {

    }

    public class TestConfigFactory extends ValueTransformer<String,TestConfig> {
        public TestConfig transform(String input) {
            TestConfig testConfig = new TestConfig();
            testConfig.setSomeString(input);
            return testConfig;
        }
    }

    public static class ListElementIncrementer extends ValueTransformer<ArrayList<Integer>,ArrayList<Integer>> {
        public ArrayList<Integer> transform(ArrayList<Integer> input) {
            ArrayList<Integer> result = Lists.newArrayList();
            for(Integer element : input) {
                result.add(element + 1);
            }
            return result;
        }
    }

    @DefaultValue("3")
    @ImportedValue("someNumber")
    private int someNumber;

    @PropertyValue("keyThatDoesNotExist")
    private int shouldBeZero;

    @PropertyValue("a")
    private String someString;

    @CommandLineValue(shortOpt = "u", longOpt = "user")
    private boolean aBoolean;

    @LoadingOrder(value = {CommandLineValue.class})
    @CommandLineValue(shortOpt = "c", longOpt = "collection", hasArg = true, description = "command line option description")
    @ValueTransformers({CharacterSeparatedStringToStringListTransformer.class})
    private Collection<String> stringCollection;

    @DefaultValue("/etc,/usr")
    @ImportedValue("stringCollection")
    private HashSet<Path> pathCollection;

    @ImportedValue("stringCollection")
    private Iterable<String> copiedStringCollection;

    @Separator(";")
    @DefaultValue("0;1;2;3;4")
    @ValueTransformers(ListElementIncrementer.class)
    private List<Integer> integerList;

    @ValueTransformers(TestConfigFactory.class)
    private ArrayList<TestConfig> testConfigList;

    @EnvironmentVariableValue("HOME")
    private Path homeDir;

    @SystemPropertyValue("user.language")
    private String systemProperty;

    public void setSomeNumber(Integer someNumber) {
        this.someNumber = someNumber;
    }

    public void setSomeString(String someString) {
        this.someString = someString;
    }

    public void setBoolean(boolean aBoolean) {
        this.aBoolean = aBoolean;
    }

    public void setStringCollection(Collection<String> stringCollection) {
        this.stringCollection = stringCollection;
    }

    public void setHomeDir(Path homeDir) {
        this.homeDir = homeDir;
    }

    public void setSystemProperty(String systemProperty) {
        this.systemProperty = systemProperty;
    }
    
    public Collection<Path> getPathCollection() {
        return pathCollection;
    }

    public void setPathCollection(HashSet<Path> pathCollection) {
        this.pathCollection = pathCollection;
    }

    public List<Integer> getIntegerList() {
        return integerList;
    }

    public void setIntegerList(List<Integer> integerList) {
        this.integerList = integerList;
    }

    public ArrayList<TestConfig> getTestConfigList() {
        return testConfigList;
    }

    public Collection<String> getStringCollection() {
        return stringCollection;
    }

    public void setCopiedStringCollection(Iterable<String> copiedStringCollection) {
        this.copiedStringCollection = copiedStringCollection;
    }

    @Validation
    private void validate() {
        System.out.println("config validated");
    }
}
