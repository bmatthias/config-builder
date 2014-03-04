package com.tngtech.configbuilder;

import com.google.common.collect.Lists;
import com.tngtech.configbuilder.annotation.configuration.LoadingOrder;
import com.tngtech.configbuilder.configuration.BuilderConfiguration;
import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.testclasses.TestConfigWithoutAnnotations;
import com.tngtech.configbuilder.util.ConfigBuilderFactory;
import com.tngtech.configbuilder.testclasses.TestConfig;
import com.tngtech.configbuilder.util.*;
import com.tngtech.propertyloader.PropertyLoader;
import com.tngtech.propertyloader.impl.DefaultPropertyFilterContainer;
import com.tngtech.propertyloader.impl.DefaultPropertyLocationContainer;
import com.tngtech.propertyloader.impl.DefaultPropertySuffixContainer;
import com.tngtech.propertyloader.impl.filters.DecryptingFilter;
import com.tngtech.propertyloader.impl.filters.VariableResolvingFilter;
import com.tngtech.propertyloader.impl.interfaces.PropertyLoaderFilter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConfigBuilderTest {

    private ConfigBuilder<TestConfig> configBuilder;

    private ConfigBuilder<TestConfigWithoutAnnotations> configBuilderWithoutAnnotations;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Mock
    private ConfigBuilderFactory configBuilderFactory;
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
    private Properties additionalProperties;

    @Mock
    private PropertyLoaderConfigurator propertyLoaderConfigurator;

    @Mock
    private PropertyLoader propertyLoader;
    @Mock
    private DefaultPropertySuffixContainer suffixContainer;
    @Mock
    private DefaultPropertyLocationContainer locationContainer;
    @Mock
    private DefaultPropertyFilterContainer filterContainer;
    @Mock
    private List<PropertyLoaderFilter> filters;
    @Mock
    private Options commandLineOptions;
    @Mock
    private CommandLine commandLine;
    @Mock
    private Properties properties;
    @Mock
    private LoadingOrder loadingOrder;
    private PrintStream originalOutStream;

    @Before
    public void setUp() throws Exception {

        originalOutStream = new PrintStream(System.out);
        System.setOut(new PrintStream(outContent));

        when(configBuilderFactory.getInstance(BuilderConfiguration.class)).thenReturn(builderConfiguration);
        when(configBuilderFactory.getInstance(CommandLineHelper.class)).thenReturn(commandLineHelper);
        when(configBuilderFactory.getInstance(ConfigValidator.class)).thenReturn(configValidator);
        when(configBuilderFactory.getInstance(FieldSetter.class)).thenReturn(fieldSetter);
        when(configBuilderFactory.getInstance(ErrorMessageSetup.class)).thenReturn(errorMessageSetup);
        when(configBuilderFactory.getInstance(ConstructionHelper.class)).thenReturn(constructionHelper);
        when(configBuilderFactory.getInstance(PropertyLoaderConfigurator.class)).thenReturn(propertyLoaderConfigurator);
        when(configBuilderFactory.createInstance(Properties.class)).thenReturn(additionalProperties);
        when(propertyLoaderConfigurator.configurePropertyLoader(TestConfig.class)).thenReturn(propertyLoader);
        when(propertyLoader.getSuffixes()).thenReturn(suffixContainer);
        when(propertyLoader.getLocations()).thenReturn(locationContainer);
        when(propertyLoader.getFilters()).thenReturn(filterContainer);
        when(filterContainer.getFilters()).thenReturn(filters);
        when(commandLineHelper.getOptions(TestConfig.class)).thenReturn(commandLineOptions);

        configBuilder = new ConfigBuilder<TestConfig>(TestConfig.class, configBuilderFactory);
        configBuilderWithoutAnnotations = new ConfigBuilder<TestConfigWithoutAnnotations>(TestConfigWithoutAnnotations.class, configBuilderFactory);
    }

    @After
    public void tearDown() {
        System.setOut(originalOutStream);
    }

    @Test
    public void testWithCommandLineArgs() throws Exception {
        when(commandLineHelper.getCommandLine(TestConfig.class, new String[]{})).thenReturn(commandLine);
        assertEquals(configBuilder, configBuilder.withCommandLineArgs(new String[]{}));
    }

    @Test
    public void testOverridePropertiesFiles() throws Exception {
        List<String> baseNames = Lists.newArrayList("file");
        configBuilder.overridePropertiesFiles(baseNames);
        verify(propertyLoader).withBaseNames(baseNames);
    }

    @Test
    public void testPrintCommandLineHelp() throws Exception {
        configBuilder.printCommandLineHelp();
        assertTrue(outContent.toString().contains("Command Line Options for class TestConfig"));
    }

    @Test
    public void testBuild() throws Exception {
        when(propertyLoader.load()).thenReturn(properties);
        configBuilder.build();
        verify(propertyLoader).load();
        verify(builderConfiguration).setProperties(properties);
        verify(errorMessageSetup).initialize(null, propertyLoader);

        verify(fieldSetter).setFields(Matchers.any(TestConfig.class), Matchers.any(BuilderConfiguration.class));
        verify(configValidator).validate(Matchers.any(TestConfig.class));
    }

    //    @Test
//    This test does not work. It needs to be fixed. Urgently. Please do it. Only you can save us!
    public void testMerge() throws Exception {
        TestConfig importedConfig = new TestConfig();
        when(propertyLoader.load()).thenReturn(properties);

        configBuilder.withImportedConfiguration(importedConfig).build();

        verify(propertyLoader).load();
        verify(builderConfiguration).setProperties(properties);
        verify(errorMessageSetup).initialize(null, propertyLoader);
    }

    @Test
    public void testAddProperties() {
        Properties properties = mock(Properties.class);
        assertThat(configBuilder.addProperties(properties), is(sameInstance(configBuilder)));

        verify(additionalProperties).putAll(properties);
        verifyNoMoreInteractions(propertyLoader);
    }

    @Test
    public void testWithExtension() {
        final String propertyExtension = "<propertyExtension>";
        assertThat(configBuilder.withPropertyExtension(propertyExtension), is(sameInstance(configBuilder)));

        verify(propertyLoader).withExtension(propertyExtension);
        verifyNoMoreInteractions(propertyLoader);
    }

    @Test
    public void testWithPropertySuffix() {
        final String propertySuffix = "<propertySuffix>";
        assertThat(configBuilder.withPropertySuffix(propertySuffix), is(sameInstance(configBuilder)));

        InOrder order = inOrder(propertyLoader, suffixContainer);
        order.verify(propertyLoader).getSuffixes();
        order.verify(suffixContainer).clear();
        order.verify(suffixContainer).addSuffixList(Lists.newArrayList(propertySuffix));
        order.verifyNoMoreInteractions();
    }

    @Test
    public void testWithPropertySuffixes() {
        final String propertySuffix1 = "<propertySuffix1>";
        final String propertySuffix2 = "<propertySuffix2>";
        assertThat(configBuilder.withPropertySuffixes(propertySuffix1, propertySuffix2), is(sameInstance(configBuilder)));

        InOrder order = inOrder(propertyLoader, suffixContainer);
        order.verify(propertyLoader).getSuffixes();
        order.verify(suffixContainer).clear();
        order.verify(suffixContainer).addSuffixList(Lists.newArrayList(propertySuffix1, propertySuffix2));
        order.verifyNoMoreInteractions();
    }

    @Test
    public void testAddPropertySuffixes() {
        final String propertySuffix1 = "<propertySuffix1>";
        final String propertySuffix2 = "<propertySuffix2>";
        assertThat(configBuilder.addPropertySuffixes(propertySuffix1, propertySuffix2), is(sameInstance(configBuilder)));

        InOrder order = inOrder(propertyLoader, suffixContainer);
        order.verify(propertyLoader).getSuffixes();
        order.verify(suffixContainer).addSuffixList(Lists.newArrayList(propertySuffix1, propertySuffix2));
        order.verifyNoMoreInteractions();
    }

    @Test
    public void testWithPropertiesFile() {
        final String propertyFile = "<propertyFile>";
        assertThat(configBuilder.withPropertiesFile(propertyFile), is(sameInstance(configBuilder)));

        verify(propertyLoader).withBaseNames(Lists.newArrayList(propertyFile));
        verifyNoMoreInteractions(propertyLoader);
    }

    @Test
    public void testWithPropertiesFiles() {
        final String propertiesFile1 = "<propertiesFile1>";
        final String propertiesFile2 = "<propertiesFile2>";
        assertThat(configBuilder.withPropertiesFiles(propertiesFile1, propertiesFile2), is(sameInstance(configBuilder)));

        verify(propertyLoader).withBaseNames(Lists.newArrayList(propertiesFile1, propertiesFile2));
        verifyNoMoreInteractions(propertyLoader);
    }

    @Test
    public void testWithPropertyLocations() {
        final String propertyLocation1 = "<propertyLocation>";
        final Class propertyLocation2 = PropertyLoader.class;
        assertThat(configBuilder.withPropertyLocations(propertyLocation1, propertyLocation2),
                is(sameInstance(configBuilder)));

        InOrder order = inOrder(propertyLoader, locationContainer);
        order.verify(propertyLoader).getLocations();
        order.verify(locationContainer).clear();
        order.verify(locationContainer).atDirectory(propertyLocation1);
        order.verify(locationContainer).atRelativeToClass(propertyLocation2);
        order.verifyNoMoreInteractions();
    }

    @Test
    public void testWithPropertyFilters() {

        assertThat(configBuilder.withPropertyFilters(
                VariableResolvingFilter.class, DecryptingFilter.class),
                is(sameInstance(configBuilder)));

        InOrder order = inOrder(propertyLoader, filterContainer, filters);
        order.verify(propertyLoader).getFilters();
        order.verify(filterContainer).getFilters();
        order.verify(filters).clear();
        order.verify(filters).add(isA(VariableResolvingFilter.class));
        order.verify(filters).add(isA(DecryptingFilter.class));
        order.verifyNoMoreInteractions();
    }
}
