package com.tngtech.configbuilder.annotation.valueextractor;

import com.tngtech.configbuilder.configuration.BuilderConfiguration;
import com.tngtech.configbuilder.util.ConfigBuilderFactory;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PropertyValueProcessorTest {

    private final PropertyValueProcessor propertyValueProcessor = new PropertyValueProcessor();

    @Mock
    private BuilderConfiguration builderConfiguration;
    @Mock
    private ConfigBuilderFactory configBuilderFactory;
    @Mock
    private Properties properties;
    @Mock
    PropertyValue propertyValue;

    @BeforeEach
    public void setUp() {
        when(configBuilderFactory.getInstance(BuilderConfiguration.class)).thenReturn(builderConfiguration);
        when(builderConfiguration.getProperties()).thenReturn(properties);
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
        when(properties.containsKey("other.test")).thenReturn(false);
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
        when(properties.containsKey("test")).thenReturn(false);

        assertThat(propertyValueProcessor.getValue(propertyValue, configBuilderFactory)).isNull();

        verify(properties).containsKey("test");
        verify(properties, never()).getProperty(anyString());
        verifyNoMoreInteractions(properties);
    }
}
