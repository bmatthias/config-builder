package com.tngtech.configbuilder.annotation.valueextractor;

import com.tngtech.configbuilder.util.ConfigBuilderFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SystemPropertyProcessorTest {

    private SystemPropertyProcessor systemPropertyProcessor = new SystemPropertyProcessor();

    @Mock
    private SystemPropertyValue systemPropertyValue;
    @Mock
    private ConfigBuilderFactory configBuilderFactory;

    @Test
    public void testGetValue() {
        when(systemPropertyValue.value()).thenReturn("user.language");
        assertThat(systemPropertyProcessor.getValue(systemPropertyValue, configBuilderFactory)).isEqualTo(System.getProperty("user.language"));
    }
}
