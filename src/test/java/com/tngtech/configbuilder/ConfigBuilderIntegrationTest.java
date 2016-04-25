package com.tngtech.configbuilder;


import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tngtech.configbuilder.testclasses.TestConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

@RunWith(Parameterized.class)
public class ConfigBuilderIntegrationTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    private Class configClass;
    private Object configInstance;
    private PrintStream originalOutStream;

    @Before
    public void setUp() {
        originalOutStream = new PrintStream(System.out);
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void tearDown() {
        System.setOut(originalOutStream);
    }

    @Parameterized.Parameters
    public static Collection configs() {
        TestConfig testConfig = new TestConfig();
        testConfig.setSomeString("Hello, World!");
        testConfig.setSomeNumber(3);
        testConfig.setBoolean(true);
        testConfig.setStringCollection(Lists.newArrayList("first entry", "second entry"));
        testConfig.setIntegerList(Lists.newArrayList(1, 2, 3, 4, 5));
        testConfig.setPathCollection(Sets.newHashSet(Paths.get("/etc"), Paths.get("/usr")));
        testConfig.setHomeDir(Paths.get(System.getenv("HOME")));
        testConfig.setSystemProperty(System.getProperty("user.language"));

        return Arrays.asList(new Object[][]{{TestConfig.class, testConfig}});
    }

    public ConfigBuilderIntegrationTest(Class configClass, Object configInstance) {
        this.configClass = configClass;
        this.configInstance = configInstance;
    }

    @Test
    public void testConfigBuilderWithParameters() {
        ConfigBuilder configBuilder = ConfigBuilder.on(configClass);
        String[] args = new String[]{"-u", "--collection", "first entry,second entry"};
        Object result = configBuilder.withCommandLineArgs(args).build();
        assertReflectionEquals(configInstance, result);
        assertTrue(outContent.toString().contains("config validated"));
    }

    @Test
    public void testWithImportedConfig() {
        ArrayList<String> arrayList = Lists.newArrayList("collection", "two");
        String home = System.getenv("HOME");
        String userLanguage = System.getProperty("user.language");

        TestConfig importedTestConfig = new TestConfig();
        importedTestConfig.setSomeNumber(5);
        importedTestConfig.setStringCollection(Lists.newArrayList("/mnt", "/home"));

        TestConfig expectedTestConfig = new TestConfig();
        expectedTestConfig.setSomeNumber(5);
        List<Path> paths = Arrays.asList(Paths.get("/mnt"), Paths.get("/home"));
        expectedTestConfig.setPathCollection(Sets.newLinkedHashSet(paths));
        expectedTestConfig.setCopiedStringCollection(importedTestConfig.getStringCollection());
        expectedTestConfig.setSomeString("Hello, World!");
        expectedTestConfig.setBoolean(true);
        expectedTestConfig.setStringCollection(arrayList);
        expectedTestConfig.setIntegerList(Lists.newArrayList(1, 2, 3, 4, 5));

        expectedTestConfig.setHomeDir(Paths.get(home));
        expectedTestConfig.setSystemProperty(userLanguage);

        ConfigBuilder configBuilder = ConfigBuilder.on(configClass);
        String[] args = new String[]{"-u", "--collection", "collection,two"};

        Object result = configBuilder.withCommandLineArgs(args).withImportedConfiguration(importedTestConfig).build();
        assertLenientEquals(expectedTestConfig, result);
        assertTrue(outContent.toString().contains("config validated"));
    }
}
