package com.tngtech.configbuilder;


import com.google.common.collect.Lists;
import com.tngtech.configbuilder.testclasses.TestConfig;
import com.tngtech.configbuilder.testclasses.TestConfigWithoutDefaultConstructor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

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
        testConfig.setStringCollection(Lists.newArrayList("PIDs fixed with success"));
        testConfig.setList(Lists.newArrayList(new String[]{"one success"}, (new String[]{"two success"})));
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
        String[] args = new String[]{"-u", "--collection", "PIDs fixed with"};
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
        TestConfig testConfig = new TestConfig();
        testConfig.setHelloWorld("HelloWorld!");
        testConfig.setSomeNumber(3);
        testConfig.setBoolean(true);
        testConfig.setStringCollection(Lists.newArrayList("collection"));
        testConfig.setList(Lists.newArrayList(new String[]{"one success"}, (new String[]{"two success"})));
        testConfig.setEnvironmentVariable(System.getenv("PATH"));
        testConfig.setSystemProperty(System.getProperty("user.language"));

        TestConfig testConfig2 = new TestConfig();
        testConfig2.setHelloWorld("HelloWorld!");
        testConfig2.setBoolean(false);
        testConfig2.setStringCollection(Lists.newArrayList("collection"));

        ConfigBuilder configBuilder = new ConfigBuilder(configClass);
        String[] args = new String[]{"-u", "--collection", "PIDs fixed with"};
        Object result = configBuilder.withCommandLineArgs(args).merge(testConfig2);
        assertReflectionEquals(testConfig, result);
    }

    @Test
    public void testPrintCommandLine() {
        ConfigBuilder configBuilder = new ConfigBuilder(configClass);
        configBuilder.printCommandLineHelp();
    }
}
