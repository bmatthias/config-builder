package com.tngtech.configbuilder;


import com.tngtech.configbuilder.testclasses.TestConfigWithoutAnnotations;
import com.tngtech.configbuilder.testutil.SystemOutRule;
import com.tngtech.propertyloader.PropertyLoader;
import com.tngtech.propertyloader.impl.filters.DecryptingFilter;
import org.junit.Rule;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Properties;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;

public class ConfigBuilderWithoutAnnotationsIntegrationTest {

    @Rule
    public SystemOutRule systemOut = new SystemOutRule();

    private ConfigBuilder<TestConfigWithoutAnnotations> configBuilder = new ConfigBuilder<>(TestConfigWithoutAnnotations.class);

    @Test
    public void testConfigBuilderWithParameters() {
        TestConfigWithoutAnnotations expectedConfig = new TestConfigWithoutAnnotations();
        expectedConfig.setSomeString("Hello, Galaxy!");
        expectedConfig.setOtherString("${a}");
        expectedConfig.setSomeNumber(3);
        expectedConfig.setBoolean(true);
        expectedConfig.setStringCollection(newArrayList("first entry", "second entry"));
        expectedConfig.setIntegerList(newArrayList(1, 2, 3, 4, 5));
        expectedConfig.setPathCollection(newHashSet(Paths.get("/etc"), Paths.get("/usr")));
        expectedConfig.setHomeDir(Paths.get(System.getenv("HOME")));
        expectedConfig.setSystemProperty(System.getProperty("user.language"));

        final Properties additionalProperties = new Properties();
        additionalProperties.put("a", "Hello, Galaxy!");
        String[] args = {"-u", "--collection", "first entry,second entry"};

        Object result = configBuilder
                .withPropertyLocations(PropertyLoader.class)
                .withPropertyFilters(DecryptingFilter.class)
                .withPropertyExtension("testproperties")
                .withPropertySuffix("test")
                .withPropertiesFile("demoapp-configuration")
                .withCommandLineArgs(args)
                .addProperties(additionalProperties)
                .build();
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedConfig);
        assertThat(systemOut.getLog()).contains("config validated");
    }
}
