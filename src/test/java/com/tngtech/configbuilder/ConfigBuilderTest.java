package com.tngtech.configbuilder;

import com.tngtech.configbuilder.configuration.BuilderConfiguration;
import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.testclasses.TestConfig;
import com.tngtech.configbuilder.testutil.SystemOutRule;
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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Properties;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConfigBuilderTest {

    @Rule
    public SystemOutRule systemOut = new SystemOutRule();

    private ConfigBuilder<TestConfig> configBuilder;

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

    @Before
    public void setUp() {
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

        configBuilder = new ConfigBuilder<>(TestConfig.class, configBuilderFactory);
    }

    @Test
    public void testWithCommandLineArgs() {
        assertThat(configBuilder.withCommandLineArgs(new String[]{})).isSameAs(configBuilder);
    }

    @Test
    public void testOverridePropertiesFiles() {
        List<String> baseNames = newArrayList("file");
        configBuilder.overridePropertiesFiles(baseNames);
        verify(propertyLoader).withBaseNames(baseNames);
    }

    @Test
    public void testPrintCommandLineHelp() {
        configBuilder.printCommandLineHelp();
        assertThat(systemOut.getLog()).contains("Command Line Options for class TestConfig");
    }

    @Test
    public void testBuild() {
        when(propertyLoader.load()).thenReturn(properties);
        TestConfig testConfig = new TestConfig();
        when(constructionHelper.getInstance(TestConfig.class)).thenReturn(testConfig);

        configBuilder.build();

        verify(propertyLoader).load();
        verify(builderConfiguration).setProperties(properties);
        verify(errorMessageSetup).initialize(null, propertyLoader);
        verify(fieldSetter).setFields(same(testConfig), any(BuilderConfiguration.class));
        verify(configValidator).validate(same(testConfig));
    }

    @Test
    public void testMerge() {
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
        assertThat(configBuilder.addProperties(properties)).isSameAs(configBuilder);

        verify(additionalProperties).putAll(properties);
        verifyNoMoreInteractions(propertyLoader);
    }

    @Test
    public void testWithExtension() {
        final String propertyExtension = "<propertyExtension>";
        assertThat(configBuilder.withPropertyExtension(propertyExtension)).isSameAs(configBuilder);

        verify(propertyLoader).withExtension(propertyExtension);
        verifyNoMoreInteractions(propertyLoader);
    }

    @Test
    public void testWithPropertySuffix() {
        final String propertySuffix = "<propertySuffix>";
        assertThat(configBuilder.withPropertySuffix(propertySuffix)).isSameAs(configBuilder);

        InOrder order = inOrder(propertyLoader, suffixContainer);
        order.verify(propertyLoader).getSuffixes();
        order.verify(suffixContainer).clear();
        order.verify(suffixContainer).addSuffixList(newArrayList(propertySuffix));
        order.verifyNoMoreInteractions();
    }

    @Test
    public void testWithPropertySuffixes() {
        final String propertySuffix1 = "<propertySuffix1>";
        final String propertySuffix2 = "<propertySuffix2>";
        assertThat(configBuilder.withPropertySuffixes(propertySuffix1, propertySuffix2)).isSameAs(configBuilder);

        InOrder order = inOrder(propertyLoader, suffixContainer);
        order.verify(propertyLoader).getSuffixes();
        order.verify(suffixContainer).clear();
        order.verify(suffixContainer).addSuffixList(newArrayList(propertySuffix1, propertySuffix2));
        order.verifyNoMoreInteractions();
    }

    @Test
    public void testAddPropertySuffixes() {
        final String propertySuffix1 = "<propertySuffix1>";
        final String propertySuffix2 = "<propertySuffix2>";
        assertThat(configBuilder.addPropertySuffixes(propertySuffix1, propertySuffix2)).isSameAs(configBuilder);

        InOrder order = inOrder(propertyLoader, suffixContainer);
        order.verify(propertyLoader).getSuffixes();
        order.verify(suffixContainer).addSuffixList(newArrayList(propertySuffix1, propertySuffix2));
        order.verifyNoMoreInteractions();
    }

    @Test
    public void testWithPropertiesFile() {
        final String propertyFile = "<propertyFile>";
        assertThat(configBuilder.withPropertiesFile(propertyFile)).isSameAs(configBuilder);

        verify(propertyLoader).withBaseNames(newArrayList(propertyFile));
        verifyNoMoreInteractions(propertyLoader);
    }

    @Test
    public void testWithPropertiesFiles() {
        final String propertiesFile1 = "<propertiesFile1>";
        final String propertiesFile2 = "<propertiesFile2>";
        assertThat(configBuilder.withPropertiesFiles(propertiesFile1, propertiesFile2)).isSameAs(configBuilder);

        verify(propertyLoader).withBaseNames(newArrayList(propertiesFile1, propertiesFile2));
        verifyNoMoreInteractions(propertyLoader);
    }

    @Test
    public void testWithPropertyLocations() {
        final String propertyLocation1 = "<propertyLocation>";
        final Class propertyLocation2 = PropertyLoader.class;
        assertThat(configBuilder.withPropertyLocations(propertyLocation1, propertyLocation2)).isSameAs(configBuilder);

        InOrder order = inOrder(propertyLoader, locationContainer);
        order.verify(propertyLoader).getLocations();
        order.verify(locationContainer).clear();
        order.verify(locationContainer).atDirectory(propertyLocation1);
        order.verify(locationContainer).atRelativeToClass(propertyLocation2);
        order.verifyNoMoreInteractions();
    }

    @Test
    public void testWithPropertyFilters() {
        assertThat(configBuilder.withPropertyFilters(VariableResolvingFilter.class, DecryptingFilter.class)).isSameAs(configBuilder);

        InOrder order = inOrder(propertyLoader, filterContainer, filters);
        order.verify(propertyLoader).getFilters();
        order.verify(filterContainer).getFilters();
        order.verify(filters).clear();
        order.verify(filters).add(isA(VariableResolvingFilter.class));
        order.verify(filters).add(isA(DecryptingFilter.class));
        order.verifyNoMoreInteractions();
    }

    @Test
    public void testStaticBuilderFactoryMethod() {
        configBuilder = ConfigBuilder.on(TestConfig.class);

        assertThat(configBuilder).isNotNull();
    }
}
