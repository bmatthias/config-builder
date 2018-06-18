package com.tngtech.configbuilder.util;

import com.tngtech.configbuilder.annotation.valueextractor.DefaultValue;
import com.tngtech.configbuilder.configuration.BuilderConfiguration;
import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.exception.ConfigBuilderException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FieldSetterTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static class TestConfig {
        @DefaultValue("stringValue")
        String emptyTestString;

        @DefaultValue("stringValue")
        String testString = "defaultValue";
    }

    private static class ExtendedTestConfig extends TestConfig {
        @DefaultValue("stringValue")
        String extendedTestString;
    }

    private static class TestConfigForIllegalArgumentException {
        @DefaultValue("user")
        int testInt;
    }

    private static class TestConfigWithoutAnnotations {
        String testString = "testString";
    }

    @Mock
    private BuilderConfiguration builderConfiguration;
    @Mock
    private FieldValueTransformer fieldValueTransformer;
    @Mock
    private FieldValueExtractor fieldValueExtractor;
    @Mock
    private ErrorMessageSetup errorMessageSetup;
    @Mock
    private AnnotationHelper annotationHelper;
    @Mock
    private ConfigBuilderFactory configBuilderFactory;

    @Before
    public void setUp() {
        when(configBuilderFactory.getInstance(FieldValueTransformer.class)).thenReturn(fieldValueTransformer);
        when(configBuilderFactory.getInstance(FieldValueExtractor.class)).thenReturn(fieldValueExtractor);
        when(configBuilderFactory.getInstance(AnnotationHelper.class)).thenReturn(annotationHelper);
        when(configBuilderFactory.getInstance(ErrorMessageSetup.class)).thenReturn(errorMessageSetup);
        when(annotationHelper.fieldHasAnnotationAnnotatedWith(any(Field.class), any(Class.class))).thenReturn(true);
    }

    @Test
    public void testSetFieldsThrowsIllegalArgumentException() {
        when(fieldValueExtractor.extractValue(any(Field.class), any(BuilderConfiguration.class))).thenReturn("stringValue");
        when(fieldValueTransformer.transformFieldValue(any(Field.class), any(String.class))).thenReturn("stringValue");
        when(errorMessageSetup.getErrorMessage(any(IllegalArgumentException.class), any(String.class), any(String.class), any(String.class))).thenReturn("IllegalArgumentException");

        FieldSetter<TestConfigForIllegalArgumentException> fieldSetter = new FieldSetter<>(configBuilderFactory);
        TestConfigForIllegalArgumentException testConfigForIllegalArgumentException = new TestConfigForIllegalArgumentException();

        expectedException.expect(ConfigBuilderException.class);
        expectedException.expectMessage("IllegalArgumentException");

        fieldSetter.setFields(testConfigForIllegalArgumentException, builderConfiguration);
    }

    @Test
    public void testSetFields() {
        when(fieldValueExtractor.extractValue(any(Field.class), any(BuilderConfiguration.class))).thenReturn("stringValue");
        when(fieldValueTransformer.transformFieldValue(any(Field.class), any(String.class))).thenReturn("stringValue");

        FieldSetter<TestConfig> fieldSetter = new FieldSetter<>(configBuilderFactory);
        TestConfig testConfig = new TestConfig();

        fieldSetter.setFields(testConfig, builderConfiguration);

        assertThat(testConfig.testString).isEqualTo("stringValue");
        assertThat(testConfig.emptyTestString).isEqualTo("stringValue");
    }

    @Test
    public void testSetFieldsForFieldWithoutValueExtractorAnnotation() {
        when(annotationHelper.fieldHasAnnotationAnnotatedWith(any(Field.class), any(Class.class))).thenReturn(false);

        FieldSetter<TestConfigWithoutAnnotations> fieldSetter = new FieldSetter<>(configBuilderFactory);
        TestConfigWithoutAnnotations testConfigWithoutAnnotations = new TestConfigWithoutAnnotations();

        fieldSetter.setFields(testConfigWithoutAnnotations, builderConfiguration);

        assertThat(testConfigWithoutAnnotations.testString).isEqualTo("testString");
    }

    @Test
    public void testSetFieldsInObjectHierarchy() {
        when(fieldValueExtractor.extractValue(any(Field.class), any(BuilderConfiguration.class))).thenReturn("stringValue");
        when(fieldValueTransformer.transformFieldValue(any(Field.class), any(String.class))).thenReturn("stringValue");

        ExtendedTestConfig testConfig = new ExtendedTestConfig();

        FieldSetter<ExtendedTestConfig> fieldSetter = new FieldSetter<>(configBuilderFactory);
        fieldSetter.setFields(testConfig, builderConfiguration);

        assertThat(testConfig.testString).isEqualTo("stringValue");
        assertThat(testConfig.emptyTestString).isEqualTo("stringValue");
        assertThat(testConfig.extendedTestString).isEqualTo("stringValue");
    }
}
