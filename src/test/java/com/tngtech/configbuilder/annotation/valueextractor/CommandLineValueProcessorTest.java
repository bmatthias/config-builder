package com.tngtech.configbuilder.annotation.valueextractor;

import com.tngtech.configbuilder.configuration.BuilderConfiguration;
import com.tngtech.configbuilder.util.ConfigBuilderFactory;
import org.apache.commons.cli.CommandLine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommandLineValueProcessorTest {

    private CommandLineValueProcessor commandLineValueProcessor = new CommandLineValueProcessor();

    @Mock
    private BuilderConfiguration builderConfiguration;
    @Mock
    private ConfigBuilderFactory configBuilderFactory;
    @Mock
    private CommandLine commandLine;

    @Before
    public void setUpMocks() {
        when(configBuilderFactory.getInstance(BuilderConfiguration.class)).thenReturn(builderConfiguration);
        when(builderConfiguration.getCommandLine()).thenReturn(commandLine);
    }

    @Test
    public void testCommandLineValueProcessorOptionNotPresent() {
        CommandLineValue commandLineValue = TestConfig.getAnnotation("value");
        assertThat(commandLineValueProcessor.getValue(commandLineValue, configBuilderFactory)).isEqualTo("false");
    }

    @Test
    public void testCommandLineValueProcessorShortOptionPresent() {
        CommandLineValue commandLineValue = TestConfig.getAnnotation("value");
        when(commandLine.hasOption("value")).thenReturn(true);
        assertThat(commandLineValueProcessor.getValue(commandLineValue, configBuilderFactory)).isEqualTo("true");
    }

    @Test
    public void testCommandLineValueProcessorLongOptionPresent() {
        CommandLineValue commandLineValue = TestConfig.getAnnotation("value");
        when(commandLine.hasOption("longOption")).thenReturn(true);
        assertThat(commandLineValueProcessor.getValue(commandLineValue, configBuilderFactory)).isEqualTo("true");
    }

    @Test
    public void testCommandLineValueProcessorWithArgValueOptionNotPresent()  {
        CommandLineValue commandLineValue = TestConfig.getAnnotation("valueWithArg");
        assertThat(commandLineValueProcessor.getValue(commandLineValue, configBuilderFactory)).isNull();
    }

    @Test
    public void testCommandLineValueProcessorWithArg()  {
        CommandLineValue commandLineValue = TestConfig.getAnnotation("valueWithArg");
        when(commandLine.getOptionValue("value")).thenReturn("passed");
        assertThat(commandLineValueProcessor.getValue(commandLineValue, configBuilderFactory)).isEqualTo("passed");
    }

    static class TestConfig {
        @CommandLineValue(shortOpt = "value", longOpt = "longOption")
        String value;
        @CommandLineValue(shortOpt = "value", longOpt = "longOption", hasArg = true)
        String valueWithArg;

        static CommandLineValue getAnnotation(String fieldName) {
            try {
                return TestConfig.class.getDeclaredField(fieldName).getAnnotation(CommandLineValue.class);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
