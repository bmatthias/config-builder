package com.tngtech.configbuilder.testclasses;

import com.tngtech.configbuilder.annotation.configuration.LoadingOrder;
import com.tngtech.configbuilder.annotation.configuration.Separator;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.*;
import com.tngtech.configbuilder.annotation.typetransformer.CharacterSeparatedStringToStringListTransformer;
import com.tngtech.configbuilder.annotation.typetransformer.TypeTransformer;
import com.tngtech.configbuilder.annotation.typetransformer.TypeTransformers;
import com.tngtech.configbuilder.annotation.validation.Validation;
import com.tngtech.configbuilder.annotation.valueextractor.*;
import com.tngtech.propertyloader.PropertyLoader;
import com.tngtech.propertyloader.impl.filters.DecryptingFilter;
import com.tngtech.propertyloader.impl.filters.VariableResolvingFilter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class TestConfigWithoutAnnotations {

    public TestConfigWithoutAnnotations() {

    }

    public class TestConfigFactory extends TypeTransformer<String,TestConfigWithoutAnnotations> {
        public TestConfigWithoutAnnotations transform(String input) {
            TestConfigWithoutAnnotations testConfig = new TestConfigWithoutAnnotations();
            testConfig.setSomeString(input);
            return testConfig;
        }
    }

    @DefaultValue("3")
    @ImportedValue("someNumber")
    private int someNumber;

    @PropertyValue("keyThatDoesNotExist")
    private int shouldBeZero;

    @PropertyValue("a")
    private String someString;
    
    @PropertyValue("b")
    private String otherString;

    @CommandLineValue(shortOpt = "u", longOpt = "user")
    private boolean aBoolean;

    @LoadingOrder(value = {CommandLineValue.class})
    @CommandLineValue(shortOpt = "c", longOpt = "collection", hasArg = true, description = "command line option description")
    @TypeTransformers({CharacterSeparatedStringToStringListTransformer.class})
    private Collection<String> stringCollection;

    @DefaultValue("/etc,/usr")
    @ImportedValue("stringCollection")
    private HashSet<Path> pathCollection;

    @ImportedValue("stringCollection")
    private Iterable<String> copiedStringCollection;

    @Separator(";")
    @DefaultValue("1;2;3;4;5")
    private List<Integer> integerList;

    @TypeTransformers(TestConfigFactory.class)
    private ArrayList<TestConfigWithoutAnnotations> testConfigList;

    @EnvironmentVariableValue("HOME")
    private Path homeDir;

    @SystemPropertyValue("user.language")
    private String systemProperty;

    public void setSomeNumber(Integer someNumber) {
        this.someNumber = someNumber;
    }
  
    public Integer getSomeNumber() {
      return someNumber;
    }

    public void setSomeString(String someString) {
        this.someString = someString;
    }

    public String getOtherString() {
        return otherString;
    }
    
    public void setOtherString(String otherString) {
        this.otherString = otherString;
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

    public ArrayList<TestConfigWithoutAnnotations> getTestConfigList() {
        return testConfigList;
    }

    public Collection<String> getStringCollection() {
        return stringCollection;
    }

    public void setCopiedStringCollection(Iterable<String> copiedStringCollection) {
        this.copiedStringCollection = copiedStringCollection;
    }

    @Validation
    protected void validate() {
        System.out.println("config validated");
    }
}
