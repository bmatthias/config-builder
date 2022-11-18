package com.tngtech.configbuilder.configuration;

import java.util.Properties;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BuilderConfigurationTest {

    private final BuilderConfiguration builderConfiguration = new BuilderConfiguration();

    @Test
    public void testGetCommandLineArgs() {
        assertThat(builderConfiguration.getCommandLine()).isNull();
    }

    @Test
    public void testGetProperties() {
        assertThat(builderConfiguration.getProperties()).isEqualTo(new Properties());
    }
}
