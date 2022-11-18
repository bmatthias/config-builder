package com.tngtech.configbuilder.util;

import com.tngtech.configbuilder.annotation.valueextractor.DefaultValue;
import com.tngtech.configbuilder.configuration.BuilderConfiguration;
import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.exception.ConfigBuilderException;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FieldSetterTest {

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

    @BeforeEach
    public void setUp() {
        when(configBuilderFactory.getInstance(FieldValueTransformer.class)).thenReturn(fieldValueTransformer);
        when(configBuilderFactory.getInstance(FieldValueExtractor.class)).thenReturn(fieldValueExtractor);
        when(configBuilderFactory.getInstance(AnnotationHelper.class)).thenReturn(annotationHelper);
        when(configBuilderFactory.getInstance(ErrorMessageSetup.class)).thenReturn(errorMessageSetup);
    }

    @Test
    public void testSetFieldsThrowsIllegalArgumentException() {
        when(annotationHelper.fieldHasAnnotationAnnotatedWith(any(Field.class), any(Class.class))).thenReturn(true);
        when(fieldValueExtractor.extractValue(any(Field.class), any(BuilderConfiguration.class))).thenReturn("stringValue");
        when(fieldValueTransformer.transformFieldValue(any(Field.class), any(String.class))).thenReturn("stringValue");
        when(errorMessageSetup.getErrorMessage(any(IllegalArgumentException.class), any(String.class), any(String.class), any(String.class))).thenReturn("IllegalArgumentException");

        FieldSetter<TestConfigForIllegalArgumentException> fieldSetter = new FieldSetter<>(configBuilderFactory);
        TestConfigForIllegalArgumentException testConfigForIllegalArgumentException = new TestConfigForIllegalArgumentException();

        assertThatThrownBy(() -> fieldSetter.setFields(testConfigForIllegalArgumentException, builderConfiguration))
                .isInstanceOf(ConfigBuilderException.class)
                .hasMessage("IllegalArgumentException");
    }

    @Test
    public void testSetFields() {
        when(annotationHelper.fieldHasAnnotationAnnotatedWith(any(Field.class), any(Class.class))).thenReturn(true);
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
        when(annotationHelper.fieldHasAnnotationAnnotatedWith(any(Field.class), any(Class.class))).thenReturn(true);
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
