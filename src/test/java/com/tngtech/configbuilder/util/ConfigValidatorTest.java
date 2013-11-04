package com.tngtech.configbuilder.util;

import com.google.common.collect.Sets;
import com.tngtech.configbuilder.annotation.validation.Validation;
import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.exception.ValidatorException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.validation.ConstraintViolation;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConfigValidatorTest {

    private static class TestConfig {

        @Validation
        private void validate() {
            throw new RuntimeException();
        }
    }

    private ConfigValidator<TestConfig> configValidator;

    @Mock
    private ConfigBuilderFactory configBuilderFactory;
    @Mock
    private ValidatorFactory validatorFactory;
    @Mock
    private javax.validation.Validator validator;
    @Mock
    private TestConfig testConfig;
    @Mock
    private ConstraintViolation<TestConfig> constraintViolation1, constraintViolation2;
    @Mock
    private ErrorMessageSetup errorMessageSetup;
    @Mock
    private AnnotationHelper annotationHelper;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {

        when(configBuilderFactory.getInstance(AnnotationHelper.class)).thenReturn(annotationHelper);
        when(configBuilderFactory.getInstance(ErrorMessageSetup.class)).thenReturn(errorMessageSetup);
        when(configBuilderFactory.getValidatorFactory()).thenReturn(validatorFactory);
        when(validatorFactory.getValidator()).thenReturn(validator);

        configValidator = new ConfigValidator<>(configBuilderFactory);
    }

    @Test
    public void testValidateWithConstraintViolations() throws Exception {

        Set<ConstraintViolation<TestConfig>> constraintViolations = Sets.newHashSet(constraintViolation1, constraintViolation2);

        when(validator.validate(testConfig)).thenReturn(constraintViolations);
        when(errorMessageSetup.getErrorMessage(Matchers.any(Class.class))).thenReturn("Validation found the following constraint violations:");

        expectedException.expect(ValidatorException.class);
        expectedException.expectMessage("Validation found the following constraint violations:");

        configValidator.validate(testConfig);
    }

    @Test
    public void testCallValidadionMethods() throws Exception {

        when(errorMessageSetup.getErrorMessage(Matchers.any(Throwable.class))).thenReturn("InvocationTargetException");
        expectedException.expect(ValidatorException.class);
        expectedException.expectMessage("InvocationTargetException");
        when(annotationHelper.getMethodsAnnotatedWith(TestConfig.class, Validation.class)).thenReturn(Sets.newHashSet(TestConfig.class.getDeclaredMethod("validate")));
        configValidator.validate(new TestConfig());
        verify(annotationHelper).getMethodsAnnotatedWith(TestConfig.class, Validation.class);
    }

    @Test
    public void testValidateWithoutConstraintViolations() throws Exception {

        Set<ConstraintViolation<TestConfig>> constraintViolations = Sets.newHashSet();
        when(validator.validate(testConfig)).thenReturn(constraintViolations);

        configValidator.validate(testConfig);
    }
}
