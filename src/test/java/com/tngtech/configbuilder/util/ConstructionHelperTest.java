package com.tngtech.configbuilder.util;

import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.exception.NoConstructorFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConstructionHelperTest {

    private static class TestConfig {
        private final String string;
        private final Integer integer;

        public TestConfig(String string, Integer integer) {
            this.string = string;
            this.integer = integer;
        }

        private Integer getInteger() {
            return integer;
        }

        private String getString() {
            return string;
        }
    }

    private static class TestConfigForException {
        public TestConfigForException() {
        }
    }

    @Mock
    private ConfigBuilderFactory configBuilderFactory;
    @Mock
    private ErrorMessageSetup errorMessageSetup;

    @BeforeEach
    public void setUp() {
        when(configBuilderFactory.getInstance(ErrorMessageSetup.class)).thenReturn(errorMessageSetup);
    }

    @Test
    public void testGetInstance() {
        ConstructionHelper<TestConfig> constructionHelper = new ConstructionHelper<>(configBuilderFactory);
        TestConfig testConfig = constructionHelper.getInstance(TestConfig.class, "string", 3);
        assertThat(testConfig.getString()).isEqualTo("string");
        assertThat(testConfig.getInteger()).isEqualTo(3);
    }

    @Test
    public void testGetInstanceThrowsException() {
        when(errorMessageSetup.getErrorMessage(NoConstructorFoundException.class)).thenReturn("NoConstructorFoundException");
        ConstructionHelper<TestConfigForException> constructionHelper = new ConstructionHelper<>(configBuilderFactory);
        assertThatThrownBy(() -> constructionHelper.getInstance(TestConfigForException.class, "string", 3))
                .isInstanceOf(NoConstructorFoundException.class)
                .hasMessage("NoConstructorFoundException");
    }
}
