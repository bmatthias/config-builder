package com.tngtech.configbuilder;

import com.tngtech.configbuilder.exception.ConfigBuilderException;
import com.tngtech.configbuilder.exception.NoConstructorFoundException;
import com.tngtech.configbuilder.exception.PrimitiveParsingException;
import com.tngtech.configbuilder.testclasses.TestConfigThrowsInvocationTargetExceptionException;
import com.tngtech.configbuilder.testclasses.TestConfigThrowsPrimitiveParsingException;
import com.tngtech.configbuilder.testclasses.TestConfigWithoutDefaultConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(Parameterized.class)
public class ConfigBuilderExceptionTest {

    private Class configClass;
    private Class<? extends Throwable> exceptionClass;
    private String message;

    @Parameterized.Parameters
    public static Collection configs() {
        return Arrays.asList(new Object[][]{
                {TestConfigWithoutDefaultConstructor.class, NoConstructorFoundException.class, "build()"},
                {TestConfigThrowsInvocationTargetExceptionException.class, ConfigBuilderException.class, "InvocationTargetException"},
                {TestConfigThrowsPrimitiveParsingException.class, PrimitiveParsingException.class, "stringValue"}
        });
    }

    public ConfigBuilderExceptionTest(Class configClass, Class<? extends Throwable> exceptionClass, String message) {
        this.configClass = configClass;
        this.exceptionClass = exceptionClass;
        this.message = message;
    }

    @Test
    public void testConfigBuilderExceptions() {
        ConfigBuilder configBuilder = new ConfigBuilder(configClass);
        assertThatThrownBy(() -> configBuilder.build())
                .isInstanceOf(exceptionClass)
                .hasMessageContaining(message);
    }
}
