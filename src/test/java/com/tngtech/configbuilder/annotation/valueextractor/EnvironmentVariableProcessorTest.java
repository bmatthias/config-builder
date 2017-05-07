package com.tngtech.configbuilder.annotation.valueextractor;

import com.tngtech.configbuilder.configuration.BuilderConfiguration;
import com.tngtech.configbuilder.util.ConfigBuilderFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EnvironmentVariableProcessorTest {

    private EnvironmentVariableProcessor environmentVariableProcessor = new EnvironmentVariableProcessor();

    @Mock
    private EnvironmentVariableValue environmentVariableValue;
    @Mock
    private BuilderConfiguration builderConfiguration;
    @Mock
    private ConfigBuilderFactory configBuilderFactory;

    @Test
    public void testGetValue() {
        when(environmentVariableValue.value()).thenReturn("PATH");
        assertThat(environmentVariableProcessor.getValue(environmentVariableValue, configBuilderFactory)).isEqualTo(System.getenv("PATH"));
    }
}
