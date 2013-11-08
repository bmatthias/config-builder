package com.tngtech.configbuilder;


import com.google.common.collect.Lists;
import com.tngtech.configbuilder.testclasses.TestConfig;
import com.tngtech.configbuilder.testclasses.TestConfigWithoutDefaultConstructor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

@RunWith(Parameterized.class)
public class ConfigBuilderIntegrationTest {

    private Class configClass;
    private Object configInstance;

    @Before
    public void setUp() {
    }

    @Parameterized.Parameters
    public static Collection configs() {
        TestConfig testConfig = new TestConfig();
        testConfig.setHelloWorld("Hello, World!");
        testConfig.setSomeNumber(3);
        testConfig.setBoolean(true);
        testConfig.setStringCollection(Lists.newArrayList("one entry"));
        testConfig.setEnvironmentVariable(System.getenv("PATH"));
        testConfig.setSystemProperty(System.getProperty("user.language"));

        return Arrays.asList(new Object[][]{{TestConfig.class, testConfig}});
    }

    public ConfigBuilderIntegrationTest(Class configClass, Object configInstance) {
        this.configClass = configClass;
        this.configInstance = configInstance;
    }

    @Test
    public void testConfigBuilderWithParameters() {
        ConfigBuilder configBuilder = new ConfigBuilder(configClass);
        String[] args = new String[]{"-u", "--collection", "one entry"};
        Object result = configBuilder.withCommandLineArgs(args).build();
        assertReflectionEquals(configInstance, result);
    }

    @Test
    public void testConfigBuilderWithConstructorArgument() {
        ConfigBuilder<TestConfigWithoutDefaultConstructor> configBuilder = new ConfigBuilder<>(TestConfigWithoutDefaultConstructor.class);
        TestConfigWithoutDefaultConstructor c = configBuilder.build(3);
        assertEquals(3, c.getNumber());
    }

    @Test
    public void testMerge() {
        ArrayList<String> arrayList = Lists.newArrayList("collection", "two");
        String systemPath = System.getenv("PATH");
        String userLanguage = System.getProperty("user.language");
        
        TestConfig originalTestConfig = new TestConfig();
        originalTestConfig.setHelloWorld("astringwithoutspaces");
        originalTestConfig.setSomeNumber(3);
        originalTestConfig.setBoolean(true);
        originalTestConfig.setStringCollection(Lists.newArrayList("collection"));
        originalTestConfig.setEnvironmentVariable(systemPath);
        originalTestConfig.setSystemProperty(userLanguage);

        TestConfig overwritingTestConfig = new TestConfig();
        overwritingTestConfig.setHelloWorld("Hello World!");
        overwritingTestConfig.setBoolean(false);
        overwritingTestConfig.setStringCollection(arrayList);
        
        TestConfig expectedTestConfig = new TestConfig();
        expectedTestConfig.setHelloWorld("Hello World!");
        expectedTestConfig.setSomeNumber(3);
        expectedTestConfig.setBoolean(true); // primitives will not be overwritten
        expectedTestConfig.setStringCollection(arrayList);
        expectedTestConfig.setEnvironmentVariable(systemPath);
        expectedTestConfig.setSystemProperty(userLanguage);
        

        ConfigBuilder configBuilder = new ConfigBuilder(configClass);
        String[] args = new String[]{"-u", "--collection", "PIDs fixed with"};
        Object result = configBuilder.withCommandLineArgs(args).merge(overwritingTestConfig);
        assertReflectionEquals(expectedTestConfig, result);
    }
}
