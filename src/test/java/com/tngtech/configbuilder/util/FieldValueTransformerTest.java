package com.tngtech.configbuilder.util;

import com.google.common.collect.Lists;
import com.tngtech.configbuilder.annotation.typetransformer.*;
import com.tngtech.configbuilder.configuration.BuilderConfiguration;
import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.exception.TypeTransformerException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FieldValueTransformerTest {
    
    public class TestTransformer implements ITypeTransformer<String, Integer> {

        @Override
        public Integer transform(String argument) {
            return 1472;
        }
    }
    
    private class TestConfigClass {
        
        @TypeTransformers({CommaSeparatedStringToStringCollectionTransformer.class})
        private Collection<String> stringCollectionField;
        
        private int intField;
        
        @TypeTransformers({StringCollectionToCommaSeparatedStringTransformer.class})
        private Boolean boolField;
        
        @TypeTransformers({TestTransformer.class})
        private int otherIntField;
    }

    @Mock
    private ConfigBuilderFactory configBuilderFactory;
    
    @Mock
    private FieldValueExtractor fieldValueExtractor;
    
    @Mock
    private ErrorMessageSetup errorMessageSetup;
    
    @Mock
    private BuilderConfiguration builderConfiguration;
    
    private Field stringCollectionField;
    private Field intField;
    private Field boolField;
    private Field otherIntField;
    
    private FieldValueTransformer fieldValueTransformer;
    
    @Before
    public void setUp() throws Exception { 
        when(configBuilderFactory.getInstance(FieldValueExtractor.class)).thenReturn(fieldValueExtractor);
        when(configBuilderFactory.getInstance(ErrorMessageSetup.class)).thenReturn(errorMessageSetup);
        when(configBuilderFactory.getInstance(ClassCastingHelper.class)).thenReturn(new ClassCastingHelper());
        
        stringCollectionField = TestConfigClass.class.getDeclaredField("stringCollectionField");
        intField = TestConfigClass.class.getDeclaredField("intField");
        boolField = TestConfigClass.class.getDeclaredField("boolField");
        otherIntField = TestConfigClass.class.getDeclaredField("otherIntField");
        
        this.fieldValueTransformer = new FieldValueTransformer(configBuilderFactory);
    }

    @Test
    public void testTransformingFieldValue() {
        String content = "Alpha,Beta,Gamma";
        
        when(fieldValueExtractor.extractValue(stringCollectionField, builderConfiguration)).thenReturn(content);
        when(configBuilderFactory.createInstance(CommaSeparatedStringToStringCollectionTransformer.class)).thenReturn(new CommaSeparatedStringToStringCollectionTransformer());
                
        ArrayList<String> expectResult = Lists.newArrayList();
        for (String value : content.split(",")) {
            expectResult.add(value);
        }
        
        ArrayList<String> result = fieldValueTransformer.transformedFieldValue(stringCollectionField, builderConfiguration);
        
        verify(fieldValueExtractor).extractValue(stringCollectionField, builderConfiguration);
        
        assertThat(result, equalTo(expectResult));
    }
    
    @Test
    public void testIfDefaultTransformersAreFound() {
        when(fieldValueExtractor.extractValue(intField, builderConfiguration)).thenReturn("17");
        when(configBuilderFactory.createInstance(StringToIntegerTransformer.class)).thenReturn(new StringToIntegerTransformer());
        
        int actualResult = fieldValueTransformer.transformedFieldValue(intField, builderConfiguration);
        
        assertThat(actualResult, equalTo(17));
    }
    
    @Test(expected = TypeTransformerException.class)
    public void testExceptionIfNoTransformerFound() {
        when(fieldValueExtractor.extractValue(boolField, builderConfiguration)).thenReturn(38.7);
        
        fieldValueTransformer.transformedFieldValue(boolField, builderConfiguration);
    }
    
    @Test
    public void testWhenNoTransformerNecessary() {
        when(fieldValueExtractor.extractValue(intField, builderConfiguration)).thenReturn(197);

        int actualResult = fieldValueTransformer.transformedFieldValue(intField, builderConfiguration);   
        assertThat(actualResult, equalTo(197));
    }
    
    @Test
    public void testThatTransformersInAnnotationArePrioritized() {
        when(fieldValueExtractor.extractValue(otherIntField, builderConfiguration)).thenReturn("2");
        when(configBuilderFactory.createInstance(TestTransformer.class)).thenReturn(new TestTransformer());

        int actualResult = fieldValueTransformer.transformedFieldValue(otherIntField, builderConfiguration);
        
        verify(configBuilderFactory, never()).createInstance(StringToIntegerTransformer.class);

        assertThat(actualResult, equalTo(1472));
    }

}