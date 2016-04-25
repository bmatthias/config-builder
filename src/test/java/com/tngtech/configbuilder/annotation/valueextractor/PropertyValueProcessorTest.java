package com.tngtech.configbuilder.annotation.valueextractor;

import com.tngtech.configbuilder.configuration.BuilderConfiguration;
import com.tngtech.configbuilder.util.ConfigBuilderFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PropertyValueProcessorTest {

    private PropertyValueProcessor propertyValueProcessor;

    @Mock
    private BuilderConfiguration builderConfiguration;
    @Mock
    private ConfigBuilderFactory configBuilderFactory;
    @Mock
    private Properties properties;
    @Mock
    PropertyValue propertyValue;

    @Before
    public void setUp() throws Exception {
        when(configBuilderFactory.getInstance(BuilderConfiguration.class)).thenReturn(builderConfiguration);
        when(builderConfiguration.getProperties()).thenReturn(properties);

        propertyValueProcessor = new PropertyValueProcessor();
    }

    @Test
    public void testPropertyValueProcessor() {
        when(builderConfiguration.getPropertyNamePrefixes()).thenReturn(new String[]{""});
        when(propertyValue.value()).thenReturn("test");
        when(properties.containsKey("test")).thenReturn(true);
        when(properties.getProperty("test")).thenReturn("passed");

        assertThat(propertyValueProcessor.getValue(propertyValue, configBuilderFactory)).isEqualTo("passed");
    }

    @Test
    public void testPropertyValueProcessorWithPropertyNamePrefix() {
        when(builderConfiguration.getPropertyNamePrefixes()).thenReturn(new String[]{"prefix."});
        when(propertyValue.value()).thenReturn("test");
        when(properties.containsKey("prefix.test")).thenReturn(true);
        when(properties.getProperty("prefix.test")).thenReturn("passed");

        assertThat(propertyValueProcessor.getValue(propertyValue, configBuilderFactory)).isEqualTo("passed");
    }

    @Test
    public void testPropertyValueProcessorWithPropertyNamePrefixes() {
        when(builderConfiguration.getPropertyNamePrefixes()).thenReturn(new String[]{"other.", "prefix."});
        when(propertyValue.value()).thenReturn("test");
        when(properties.containsKey("prefix.test")).thenReturn(true);
        when(properties.getProperty("prefix.test")).thenReturn("passed");

        assertThat(propertyValueProcessor.getValue(propertyValue, configBuilderFactory)).isEqualTo("passed");

        final InOrder inOrder = inOrder(properties);
        inOrder.verify(properties).containsKey("other.test");
        inOrder.verify(properties).containsKey("prefix.test");
        inOrder.verify(properties).getProperty("prefix.test");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testPropertyValueProcessorPropertyNotFound() {
        when(builderConfiguration.getPropertyNamePrefixes()).thenReturn(new String[]{""});
        when(propertyValue.value()).thenReturn("test");
        when(properties.contains("test")).thenReturn(false);

        assertThat(propertyValueProcessor.getValue(propertyValue, configBuilderFactory)).isNull();

        verify(properties).containsKey("test");
        verify(properties, never()).getProperty(anyString());
        verifyNoMoreInteractions(properties);
    }
}
