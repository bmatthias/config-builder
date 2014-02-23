package com.tngtech.configbuilder;

import com.tngtech.configbuilder.testclasses.ExtendedTestConfig;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ConfigBuilderExtensionIntegrationTest {

    private ExtendedTestConfig config;

    @Before
    public void setUp() {
        config = new ConfigBuilder<ExtendedTestConfig>(ExtendedTestConfig.class).build();
    }

    @Test
    public void testGetValuePresentInSuperClassAndCurrentClass() {
        assertThat(config.getSomeNumber(), is(5));
    }

    @Test
    public void testGetValueFromSuperclass() {
        assertThat(config.getSuperSomeNumber(), is(3));
    }

    @Test
    public void testDirectValue() {
        assertThat(config.getAdditionalNumber(), is(4));
    }
}
