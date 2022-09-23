package com.tngtech.configbuilder.annotation.valueextractor;

import com.tngtech.configbuilder.util.ConfigBuilderFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultValueProcessorTest {

    private DefaultValueProcessor defaultValueProcessor;

    @Mock
    private ConfigBuilderFactory configBuilderFactory;
    @Mock
    private DefaultValue defaultValue;

    @Before
    public void setUp() {
        defaultValueProcessor = new DefaultValueProcessor();
    }

    @Test
    public void testDefaultValueProcessor() {
        when(defaultValue.value()).thenReturn("value");
        assertThat(defaultValueProcessor.getValue(defaultValue, configBuilderFactory)).isEqualTo("value");
    }
}
