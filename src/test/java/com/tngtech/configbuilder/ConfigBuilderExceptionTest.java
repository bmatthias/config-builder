package com.tngtech.configbuilder;

import com.tngtech.configbuilder.exception.ConfigBuilderException;
import com.tngtech.configbuilder.exception.NoConstructorFoundException;
import com.tngtech.configbuilder.exception.PrimitiveParsingException;
import com.tngtech.configbuilder.testclasses.TestConfigThrowsIllegalArgumentException;
import com.tngtech.configbuilder.testclasses.TestConfigThrowsPrimitiveParsingException;
import com.tngtech.configbuilder.testclasses.TestConfigThrowsInvocationTargetExceptionException;
import com.tngtech.configbuilder.testclasses.TestConfigWithoutDefaultConstructor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ConfigBuilderExceptionTest {

    private Class configClass;
    private Class<? extends Throwable> exceptionClass;
    private String message;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
    }

    @Parameterized.Parameters
    public static Collection configs() {
        return Arrays.asList(new Object[][]{
                {TestConfigWithoutDefaultConstructor.class, NoConstructorFoundException.class, "build()"},
                {TestConfigThrowsInvocationTargetExceptionException.class, ConfigBuilderException.class, "InvocationTargetException"},
                {TestConfigThrowsPrimitiveParsingException.class, PrimitiveParsingException.class, "stringValue"},
                {TestConfigThrowsIllegalArgumentException.class, ConfigBuilderException.class, "Field"}
        });
    }

    public ConfigBuilderExceptionTest(Class configClass, Class<? extends Throwable> exceptionClass, String message) {
        this.configClass = configClass;
        this.exceptionClass = exceptionClass;
        this.message = message;
    }

    @Test
    public void testConfigBuilderExceptions() {
        expectedException.expect(exceptionClass);
        expectedException.expectMessage(message);
        ConfigBuilder configBuilder = new ConfigBuilder(configClass);
        configBuilder.build();
    }
}
