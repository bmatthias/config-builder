package com.tngtech.configbuilder;

import com.tngtech.configbuilder.exception.ConfigBuilderException;
import com.tngtech.configbuilder.exception.NoConstructorFoundException;
import com.tngtech.configbuilder.exception.PrimitiveParsingException;
import com.tngtech.configbuilder.testclasses.TestConfigThrowsInvocationTargetExceptionException;
import com.tngtech.configbuilder.testclasses.TestConfigThrowsPrimitiveParsingException;
import com.tngtech.configbuilder.testclasses.TestConfigWithoutDefaultConstructor;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ConfigBuilderExceptionTest {

    private static Stream<Arguments> configs() {
        return Stream.of(
                Arguments.of(TestConfigWithoutDefaultConstructor.class, NoConstructorFoundException.class, "build()"),
                Arguments.of(TestConfigThrowsInvocationTargetExceptionException.class, ConfigBuilderException.class, "InvocationTargetException"),
                Arguments.of(TestConfigThrowsPrimitiveParsingException.class, PrimitiveParsingException.class, "stringValue"));
    }

    @MethodSource("configs")
    @ParameterizedTest
    public void testConfigBuilderExceptions(Class configClass, Class<? extends Throwable> exceptionClass, String message) {
        ConfigBuilder configBuilder = new ConfigBuilder(configClass);
        assertThatThrownBy(() -> configBuilder.build())
                .isInstanceOf(exceptionClass)
                .hasMessageContaining(message);
    }
}
