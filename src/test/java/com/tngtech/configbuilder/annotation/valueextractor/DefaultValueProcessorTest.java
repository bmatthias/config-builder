package com.tngtech.configbuilder.annotation.valueextractor;

import com.tngtech.configbuilder.util.ConfigBuilderFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DefaultValueProcessorTest {

    private DefaultValueProcessor defaultValueProcessor;

    @Mock
    private ConfigBuilderFactory configBuilderFactory;
    @Mock
    private DefaultValue defaultValue;

    @BeforeEach
    public void setUp() {
        defaultValueProcessor = new DefaultValueProcessor();
    }

    @Test
    public void testDefaultValueProcessor() {
        when(defaultValue.value()).thenReturn("value");
        assertThat(defaultValueProcessor.getValue(defaultValue, configBuilderFactory)).isEqualTo("value");
    }
}
