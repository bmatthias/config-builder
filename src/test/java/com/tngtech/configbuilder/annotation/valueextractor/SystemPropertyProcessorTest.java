package com.tngtech.configbuilder.annotation.valueextractor;

import com.tngtech.configbuilder.util.ConfigBuilderFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SystemPropertyProcessorTest {

    private SystemPropertyProcessor systemPropertyProcessor = new SystemPropertyProcessor();

    @Mock
    private SystemPropertyValue systemPropertyValue;
    @Mock
    private ConfigBuilderFactory configBuilderFactory;

    @Test
    public void testGetValue() throws Exception {
        when(systemPropertyValue.value()).thenReturn("user.language");
        assertThat(systemPropertyProcessor.getValue(systemPropertyValue, configBuilderFactory)).isEqualTo(System.getProperty("user.language"));
    }
}
