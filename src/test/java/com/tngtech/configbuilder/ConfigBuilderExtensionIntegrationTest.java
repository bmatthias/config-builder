package com.tngtech.configbuilder;

import com.tngtech.configbuilder.testclasses.ExtendedTestConfig;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigBuilderExtensionIntegrationTest {

    private ExtendedTestConfig config = new ConfigBuilder<ExtendedTestConfig>(ExtendedTestConfig.class).build();

    @Test
    public void testGetValuePresentInSuperClassAndCurrentClass() {
        assertThat(config.getSomeNumber()).isEqualTo(5);
    }

    @Test
    public void testGetValueFromSuperclass() {
        assertThat(config.getSuperSomeNumber()).isEqualTo(3);
    }

    @Test
    public void testDirectValue() {
        assertThat(config.getAdditionalNumber()).isEqualTo(4);
    }
}
