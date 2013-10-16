package com.tngtech.configbuilder.util;

import com.google.common.collect.Lists;
import com.tngtech.configbuilder.FieldValueProvider;
import com.tngtech.configbuilder.annotation.configuration.CollectionType;
import com.tngtech.configbuilder.annotation.configuration.LoadingOrder;
import com.tngtech.configbuilder.annotation.valueextractor.*;
import com.tngtech.configbuilder.annotation.valuetransformer.ValueTransformer;
import com.tngtech.configbuilder.annotation.valuetransformer.ValueTransformerProcessor;
import com.tngtech.configbuilder.configuration.BuilderConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FieldValueExtractorTest {

    private static class TestConfig {

        public static class ValueProviderTestClass implements FieldValueProvider<Object> {
            public Object getValue(String fieldString) {
                return null;
            }
        }

        @PropertyValue("testField")
        @CommandLineValue(shortOpt = "t", longOpt = "testField")
        @ValueTransformer(ValueProviderTestClass.class)
        private Collection<String> testField;

        @LoadingOrder({CommandLineValue.class, PropertyValue.class})
        @PropertyValue("testFieldWithLoadingOrder")
        @CommandLineValue(shortOpt = "t", longOpt = "testFieldWithLoadingOrder")
        private Collection<String> testFieldWithLoadingOrder;

        @CollectionType
        @DefaultValue("value1,value2")
        @ValueTransformer(ValueProviderTestClass.class)
        private Collection<String> collectionField;

        @DefaultValue("3")
        private int primitiveField;

        @DefaultValue("3")
        private boolean booleanField;
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private ConfigBuilderFactory configBuilderFactory;
    @Mock
    private BuilderConfiguration builderConfiguration;
    @Mock
    private AnnotationHelper annotationHelper;
    @Mock
    private PropertyValueProcessor propertyValueProcessor;
    @Mock
    private CommandLineValueProcessor commandLineValueProcessor;
    @Mock
    private DefaultValueProcessor defaultValueProcessor;
    @Mock
    private ValueTransformerProcessor valueTransformerProcessor;
    @Mock
    private ValueTransformer valueTransformer;

    private FieldValueExtractor fieldValueExtractor;
    private Field field;
    Class<? extends Annotation>[] order = new Class[]{PropertyValue.class, CommandLineValue.class};


    @Before
    public void setUp() throws Exception {
        fieldValueExtractor = new FieldValueExtractor(annotationHelper, configBuilderFactory);

        when(builderConfiguration.getAnnotationOrder()).thenReturn(order);

        when(configBuilderFactory.getInstance(PropertyValueProcessor.class)).thenReturn(propertyValueProcessor);
        when(configBuilderFactory.getInstance(CommandLineValueProcessor.class)).thenReturn(commandLineValueProcessor);
        when(configBuilderFactory.getInstance(DefaultValueProcessor.class)).thenReturn(defaultValueProcessor);
        when(configBuilderFactory.getInstance(ValueTransformerProcessor.class)).thenReturn(valueTransformerProcessor);

        when(annotationHelper.getAnnotationsAnnotatedWith(Matchers.any(Annotation[].class), Matchers.any(Class.class))).thenReturn(Lists.newArrayList((Annotation) valueTransformer));
        when(valueTransformer.annotationType()).thenReturn((Class) ValueTransformer.class);

        when(propertyValueProcessor.getValue(Matchers.any(PropertyValue.class), Matchers.any(BuilderConfiguration.class))).thenReturn("propertyValue");
        when(commandLineValueProcessor.getValue(Matchers.any(CommandLineValue.class), Matchers.any(BuilderConfiguration.class))).thenReturn("commandLineValue");
        when(valueTransformerProcessor.transformString(Matchers.any(ValueTransformer.class), Matchers.anyString())).thenReturn("propertyValue");
    }

    @Test
    public void testExtractValue() throws Exception {
        field = TestConfig.class.getDeclaredField("testField");

        PropertyValue propertyValue = field.getAnnotation(PropertyValue.class);
        CommandLineValue commandLineValue = field.getAnnotation(CommandLineValue.class);

        List<Annotation> orderList = Lists.newArrayList(propertyValue, commandLineValue);
        when(annotationHelper.getAnnotationsInOrder(Matchers.any(Field.class), Matchers.any(Class[].class))).thenReturn(orderList);

        String result = (String) fieldValueExtractor.extractValue(field, builderConfiguration);
        verify(annotationHelper).getAnnotationsInOrder(field, order);
        assertEquals("propertyValue", result);
    }

    @Test
    public void testExtractValueWithLoadingOrder() throws Exception {
        field = TestConfig.class.getDeclaredField("testFieldWithLoadingOrder");

        PropertyValue propertyValue = field.getAnnotation(PropertyValue.class);
        CommandLineValue commandLineValue = field.getAnnotation(CommandLineValue.class);

        List<Annotation> orderList = Lists.newArrayList(commandLineValue, propertyValue);
        when(annotationHelper.getAnnotationsInOrder(Matchers.any(Field.class), Matchers.any(Class[].class))).thenReturn(orderList);

        order = new Class[]{CommandLineValue.class, PropertyValue.class};

        String result = (String) fieldValueExtractor.extractValue(field, builderConfiguration);
        verify(annotationHelper).getAnnotationsInOrder(field, order);
        assertEquals("commandLineValue", result);
    }

    @Test
    public void testExtractValueWithNullValue() throws Exception {
        field = TestConfig.class.getDeclaredField("testField");

        PropertyValue propertyValue = field.getAnnotation(PropertyValue.class);
        CommandLineValue commandLineValue = field.getAnnotation(CommandLineValue.class);
        List<Annotation> orderList = Lists.newArrayList(propertyValue, commandLineValue);
        when(annotationHelper.getAnnotationsInOrder(Matchers.any(Field.class), Matchers.any(Class[].class))).thenReturn(orderList);

        when(propertyValueProcessor.getValue(Matchers.any(PropertyValue.class), Matchers.any(BuilderConfiguration.class))).thenReturn(null);
        when(commandLineValueProcessor.getValue(Matchers.any(CommandLineValue.class), Matchers.any(BuilderConfiguration.class))).thenReturn(null);
        when(valueTransformerProcessor.transformString(Matchers.any(ValueTransformer.class), Matchers.anyString())).thenReturn(null);

        String result = (String) fieldValueExtractor.extractValue(field, builderConfiguration);
        assertEquals(null, result);
    }

    @Test
    public void testBuildCollection() throws Exception {
        field = TestConfig.class.getDeclaredField("collectionField");

        DefaultValue defaultValue = field.getAnnotation(DefaultValue.class);
        List<Annotation> orderList = Lists.newArrayList((Annotation) defaultValue);
        when(annotationHelper.getAnnotationsInOrder(Matchers.any(Field.class), Matchers.any(Class[].class))).thenReturn(orderList);

        when(defaultValueProcessor.getValue(Matchers.any(DefaultValue.class), Matchers.any(BuilderConfiguration.class))).thenReturn("value1,value2");
        when(valueTransformerProcessor.transformString(Matchers.any(ValueTransformer.class),  eq("value1"))).thenReturn("transformedValue1");
        when(valueTransformerProcessor.transformString(Matchers.any(ValueTransformer.class),  eq("value2"))).thenReturn("transformedValue2");

        Object result = fieldValueExtractor.extractValue(field, builderConfiguration);

        assertEquals(Lists.newArrayList("transformedValue1", "transformedValue2"), result);
    }

    @Test
    public void testTransformStringToPrimitive() throws Exception {
        field = TestConfig.class.getDeclaredField("booleanField");

        DefaultValue defaultValue = field.getAnnotation(DefaultValue.class);
        List<Annotation> orderList = Lists.newArrayList((Annotation) defaultValue);
        when(annotationHelper.getAnnotationsInOrder(Matchers.any(Field.class), Matchers.any(Class[].class))).thenReturn(orderList);

        when(defaultValueProcessor.getValue(Matchers.any(DefaultValue.class), Matchers.any(BuilderConfiguration.class))).thenReturn("true");

        Object result = fieldValueExtractor.extractValue(field, builderConfiguration);
        assertEquals(true, result);
    }

    @Test
    public void testTransformStringToPrimitiveThrowsNumberFormatException() throws Exception {
        field = TestConfig.class.getDeclaredField("primitiveField");

        DefaultValue defaultValue = field.getAnnotation(DefaultValue.class);
        List<Annotation> orderList = Lists.newArrayList((Annotation) defaultValue);
        when(annotationHelper.getAnnotationsInOrder(Matchers.any(Field.class), Matchers.any(Class[].class))).thenReturn(orderList);

        when(defaultValueProcessor.getValue(Matchers.any(DefaultValue.class), Matchers.any(BuilderConfiguration.class))).thenReturn("someString");

        expectedException.expect(NumberFormatException.class);

        fieldValueExtractor.extractValue(field, builderConfiguration);
    }

    @Test
    public void testTransformStringToPrimitiveThrowsIllegalArgumentException() throws Exception {
        field = TestConfig.class.getDeclaredField("booleanField");

        DefaultValue defaultValue = field.getAnnotation(DefaultValue.class);
        List<Annotation> orderList = Lists.newArrayList((Annotation) defaultValue);
        when(annotationHelper.getAnnotationsInOrder(Matchers.any(Field.class), Matchers.any(Class[].class))).thenReturn(orderList);

        when(defaultValueProcessor.getValue(Matchers.any(DefaultValue.class), Matchers.any(BuilderConfiguration.class))).thenReturn("someString");

        expectedException.expect(IllegalArgumentException.class);

        fieldValueExtractor.extractValue(field, builderConfiguration);
    }
}
