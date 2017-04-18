package com.tngtech.configbuilder;

import com.tngtech.configbuilder.testclasses.TestConfigPropertyNamePrefix;
import com.tngtech.configbuilder.testclasses.TestConfigWithoutDefaultConstructor;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigBuilderFeatureIntegrationTest {
    @Test
    public void testConfigBuilderWithConstructorArgument() {
        TestConfigWithoutDefaultConstructor c = ConfigBuilder.on(TestConfigWithoutDefaultConstructor.class).build(3);

        assertThat(c.getNumber()).isEqualTo(3);
    }

    @Test
    public void testConfigBuilderWithPropertyNamePrefix() {
        TestConfigPropertyNamePrefix config = ConfigBuilder.on(TestConfigPropertyNamePrefix.class).build();

        assertThat(config.getFoo()).isEqualTo("first property");
    }
}
