package com.tngtech.configbuilder;

import com.google.common.collect.Lists;
import com.tngtech.configbuilder.configuration.BuilderConfiguration;
import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.testclasses.TestConfig;
import com.tngtech.configbuilder.util.*;
import com.tngtech.propertyloader.PropertyLoader;
import org.apache.commons.cli.Options;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConfigBuilderTest {

    private ConfigBuilder<TestConfig> configBuilder;

    @Mock
    private BuilderConfiguration builderConfiguration;
    @Mock
    private CommandLineHelper commandLineHelper;
    @Mock
    private FieldSetter<TestConfig> fieldSetter;
    @Mock
    private ConfigValidator<TestConfig> configValidator;
    @Mock
    private ErrorMessageSetup errorMessageSetup;
    @Mock
    private ConstructionHelper<TestConfig> constructionHelper;

    @Mock
    private PropertyLoaderConfigurator propertyLoaderConfigurator;

    @Mock
    private PropertyLoader propertyLoader;
    @Mock
    private Options commandLineOptions;

    @Before
    public void setUp() throws Exception {
        when(propertyLoaderConfigurator.configurePropertyLoader(TestConfig.class)).thenReturn(propertyLoader);
        configBuilder = new ConfigBuilder<>(TestConfig.class, builderConfiguration, propertyLoaderConfigurator, commandLineHelper, configValidator, fieldSetter, errorMessageSetup, constructionHelper);
    }

    @Test
    public void testWithCommandLineArgs() throws Exception {

    }

    @Test
    public void testOverridePropertiesFiles() throws Exception {
        List<String> baseNames = Lists.newArrayList("file");
        configBuilder.overridePropertiesFiles(baseNames);
        verify(propertyLoader).withBaseNames(baseNames);
    }

    @Test
    public void testPrintCommandLineHelp() throws Exception {

    }

    @Test
    public void testBuild() throws Exception {

    }

    @Test
    public void testMerge() throws Exception {

    }
}
