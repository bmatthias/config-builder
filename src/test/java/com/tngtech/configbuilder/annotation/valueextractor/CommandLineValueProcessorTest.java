package com.tngtech.configbuilder.annotation.valueextractor;

import com.tngtech.configbuilder.configuration.BuilderConfiguration;
import com.tngtech.configbuilder.util.ConfigBuilderFactory;
import org.apache.commons.cli.CommandLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommandLineValueProcessorTest {

    private final CommandLineValueProcessor commandLineValueProcessor = new CommandLineValueProcessor();

    @Mock
    private BuilderConfiguration builderConfiguration;
    @Mock
    private ConfigBuilderFactory configBuilderFactory;
    @Mock
    private CommandLine commandLine;

    @BeforeEach
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
        when(commandLine.hasOption("value")).thenReturn(false);
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

    @SuppressWarnings("unused")
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
