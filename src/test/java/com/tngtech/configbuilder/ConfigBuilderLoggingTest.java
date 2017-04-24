package com.tngtech.configbuilder;

import com.tngtech.configbuilder.annotation.configuration.DoNotLogValue;
import com.tngtech.configbuilder.annotation.valueextractor.PropertyValue;
import com.tngtech.configbuilder.testutil.LoggerRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigBuilderLoggingTest {
    @Rule
    public LoggerRule logger = new LoggerRule();

    @Test
    public void testValueLogging() {
        Properties properties = new Properties();
        properties.put("username", "johndoe");
        properties.put("password", "test123");

        Config config = ConfigBuilder.on(Config.class)
                .addProperties(properties)
                .build();

        assertThat(config.username).isEqualTo("johndoe");
        assertThat(config.password).isEqualTo("test123");
        assertThat(logger.getLog())
                .contains("found value \"johndoe\" for field username")
                .doesNotContain("test123")
                .contains("found value for field password");
    }

    static class Config {
        @PropertyValue("username")
        String username;

        @DoNotLogValue
        @PropertyValue("password")
        String password;
    }
}
