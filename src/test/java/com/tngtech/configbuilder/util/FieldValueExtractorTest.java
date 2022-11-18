package com.tngtech.configbuilder.util;

import com.tngtech.configbuilder.annotation.configuration.LoadingOrder;
import com.tngtech.configbuilder.annotation.configuration.Separator;
import com.tngtech.configbuilder.annotation.valueextractor.CommandLineValue;
import com.tngtech.configbuilder.annotation.valueextractor.CommandLineValueProcessor;
import com.tngtech.configbuilder.annotation.valueextractor.DefaultValue;
import com.tngtech.configbuilder.annotation.valueextractor.PropertyValue;
import com.tngtech.configbuilder.annotation.valueextractor.PropertyValueProcessor;
import com.tngtech.configbuilder.configuration.BuilderConfiguration;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FieldValueExtractorTest {

    private static class TestConfig {

        @PropertyValue("testField")
        @CommandLineValue(shortOpt = "t", longOpt = "testField")
        private Collection<String> testField;

        @LoadingOrder({CommandLineValue.class, PropertyValue.class})
        @PropertyValue("testFieldWithLoadingOrder")
        @CommandLineValue(shortOpt = "t", longOpt = "testFieldWithLoadingOrder")
        private Collection<String> testFieldWithLoadingOrder;

        @Separator
        @DefaultValue("value1,value2")
        private Collection<String> collectionField;

        @DefaultValue("3")
        private int primitiveField;

        @DefaultValue("3")
        private boolean booleanField;
    }

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

    private FieldValueExtractor fieldValueExtractor;
    private Field field;
    private Class<? extends Annotation>[] order = new Class[]{PropertyValue.class, CommandLineValue.class};

    @BeforeEach
    public void setUp() {
        when(configBuilderFactory.getInstance(AnnotationHelper.class)).thenReturn(annotationHelper);
        
        fieldValueExtractor = new FieldValueExtractor(configBuilderFactory);
    }

    @Test
    public void testExtractValue() throws Exception {
        when(builderConfiguration.getAnnotationOrder()).thenReturn(order);
        when(propertyValueProcessor.getValue(any(PropertyValue.class), any(ConfigBuilderFactory.class))).thenReturn("propertyValue");
        when(configBuilderFactory.getInstance(PropertyValueProcessor.class)).thenReturn(propertyValueProcessor);
        field = TestConfig.class.getDeclaredField("testField");

        PropertyValue propertyValue = field.getAnnotation(PropertyValue.class);
        CommandLineValue commandLineValue = field.getAnnotation(CommandLineValue.class);

        List<Annotation> orderList = newArrayList(propertyValue, commandLineValue);
        when(annotationHelper.getAnnotationsInOrder(any(Field.class), any(Class[].class))).thenReturn(orderList);

        String result = (String) fieldValueExtractor.extractValue(field, builderConfiguration);
        verify(annotationHelper).getAnnotationsInOrder(field, order);
        assertThat(result).isEqualTo("propertyValue");
    }

    @Test
    public void testExtractValueWithLoadingOrder() throws Exception {
        when(commandLineValueProcessor.getValue(any(CommandLineValue.class), any(ConfigBuilderFactory.class))).thenReturn("commandLineValue");
        when(configBuilderFactory.getInstance(CommandLineValueProcessor.class)).thenReturn(commandLineValueProcessor);
        field = TestConfig.class.getDeclaredField("testFieldWithLoadingOrder");

        PropertyValue propertyValue = field.getAnnotation(PropertyValue.class);
        CommandLineValue commandLineValue = field.getAnnotation(CommandLineValue.class);

        List<Annotation> orderList = newArrayList(commandLineValue, propertyValue);
        when(annotationHelper.getAnnotationsInOrder(any(Field.class), any(Class[].class))).thenReturn(orderList);

        order = new Class[]{CommandLineValue.class, PropertyValue.class};

        String result = (String) fieldValueExtractor.extractValue(field, builderConfiguration);
        verify(annotationHelper).getAnnotationsInOrder(field, order);
        assertThat(result).isEqualTo("commandLineValue");
    }

    @Test
    public void testExtractValueWithNullValue() throws Exception {
        when(builderConfiguration.getAnnotationOrder()).thenReturn(order);
        when(configBuilderFactory.getInstance(PropertyValueProcessor.class)).thenReturn(propertyValueProcessor);
        when(configBuilderFactory.getInstance(CommandLineValueProcessor.class)).thenReturn(commandLineValueProcessor);
        field = TestConfig.class.getDeclaredField("testField");

        PropertyValue propertyValue = field.getAnnotation(PropertyValue.class);
        CommandLineValue commandLineValue = field.getAnnotation(CommandLineValue.class);
        List<Annotation> orderList = newArrayList(propertyValue, commandLineValue);
        when(annotationHelper.getAnnotationsInOrder(any(Field.class), any(Class[].class))).thenReturn(orderList);

        when(propertyValueProcessor.getValue(any(PropertyValue.class), any(ConfigBuilderFactory.class))).thenReturn(null);
        when(commandLineValueProcessor.getValue(any(CommandLineValue.class), any(ConfigBuilderFactory.class))).thenReturn(null);

        Object result = fieldValueExtractor.extractValue(field, builderConfiguration);
        assertThat(result).isNull();
    }
}
