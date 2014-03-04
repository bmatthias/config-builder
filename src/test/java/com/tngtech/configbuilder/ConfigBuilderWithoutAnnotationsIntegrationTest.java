package com.tngtech.configbuilder;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tngtech.configbuilder.testclasses.TestConfigWithoutAnnotations;
import com.tngtech.propertyloader.PropertyLoader;
import com.tngtech.propertyloader.impl.filters.DecryptingFilter;
import com.tngtech.propertyloader.impl.filters.VariableResolvingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class ConfigBuilderWithoutAnnotationsIntegrationTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    private PrintStream originalOutStream;

    private ConfigBuilder<TestConfigWithoutAnnotations> configBuilder;
    private TestConfigWithoutAnnotations expectedConfig;

    @Before
    public void setUp() {
        originalOutStream = new PrintStream(System.out);
        System.setOut(new PrintStream(outContent));

        configBuilder = new ConfigBuilder<TestConfigWithoutAnnotations>(TestConfigWithoutAnnotations.class);

        expectedConfig = new TestConfigWithoutAnnotations();
        expectedConfig.setSomeString("Hello, Galaxy!");
        expectedConfig.setOtherString("${a}");
        expectedConfig.setSomeNumber(3);
        expectedConfig.setBoolean(true);
        expectedConfig.setStringCollection(Lists.newArrayList("first entry", "second entry"));
        expectedConfig.setIntegerList(Lists.newArrayList(1, 2, 3, 4, 5));
        expectedConfig.setPathCollection(Sets.newHashSet(Paths.get("/etc"), Paths.get("/usr")));
        expectedConfig.setHomeDir(Paths.get(System.getenv("HOME")));
        expectedConfig.setSystemProperty(System.getProperty("user.language"));
    }

    @After
    public void tearDown() {
        System.setOut(originalOutStream);
    }

    @Test
    public void testConfigBuilderWithParameters() {
        final Properties additionalProperties = new Properties();
        additionalProperties.put("a", "Hello, Galaxy!");
        String[] args = new String[]{"-u", "--collection", "first entry,second entry"};
        Object result = configBuilder
                .withPropertyLocations(PropertyLoader.class)
                .withPropertyFilters(DecryptingFilter.class)
                .withPropertyExtension("testproperties")
                .withPropertySuffix("test")
                .withPropertiesFile("demoapp-configuration")
                .withCommandLineArgs(args)
                .addProperties(additionalProperties)
                .build();
        assertReflectionEquals(expectedConfig, result);
        assertTrue(outContent.toString().contains("config validated"));
    }
}
