package com.tngtech.configbuilder.annotation.valueextractor;

import com.tngtech.configbuilder.util.ConfigBuilderFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EnvironmentVariableProcessorTest {

    private final EnvironmentVariableProcessor environmentVariableProcessor = new EnvironmentVariableProcessor();

    @Mock
    private EnvironmentVariableValue environmentVariableValue;

    @Mock
    private ConfigBuilderFactory configBuilderFactory;

    @Test
    public void testGetValue() {
        when(environmentVariableValue.value()).thenReturn("PATH");
        assertThat(environmentVariableProcessor.getValue(environmentVariableValue, configBuilderFactory)).isEqualTo(System.getenv("PATH"));
    }
}
