package com.tngtech.configbuilder.configuration;

import org.junit.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class BuilderConfigurationTest {

    private BuilderConfiguration builderConfiguration = new BuilderConfiguration();

    @Test
    public void testGetCommandLineArgs() {
        assertThat(builderConfiguration.getCommandLine()).isNull();
    }

    @Test
    public void testGetProperties() {
        assertThat(builderConfiguration.getProperties()).isEqualTo(new Properties());
    }
}
