package com.tngtech.configbuilder.annotation.valueextractor;

import com.tngtech.configbuilder.configuration.BuilderConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EnvironmentVariableProcessorTest {

    private EnvironmentVariableProcessor environmentVariableProcessor;

    @Mock
    private EnvironmentVariableValue environmentVariableValue;

    @Mock
    private BuilderConfiguration builderConfiguration;

    @Before
    public void setUp() throws Exception {
        environmentVariableProcessor = new EnvironmentVariableProcessor();
        when(environmentVariableValue.value()).thenReturn("PATH");
    }

    @Test
    public void testGetValue() throws Exception {
        assertEquals(System.getenv("PATH"),environmentVariableProcessor.getValue(environmentVariableValue,builderConfiguration));
    }
}
