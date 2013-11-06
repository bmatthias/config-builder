package com.tngtech.configbuilder.util;

import com.google.common.collect.Lists;
import com.tngtech.configbuilder.annotation.typetransformer.CommaSeparatedStringToStringCollectionTransformer;
import com.tngtech.configbuilder.annotation.typetransformer.StringCollectionToCommaSeparatedStringTransformer;
import com.tngtech.configbuilder.annotation.typetransformer.TypeTransformers;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FieldValueTransformerTest {
    
    private class TestConfigClass {
        
        @TypeTransformers({CommaSeparatedStringToStringCollectionTransformer.class})
        private Collection<String> stringCollectionField;
        
        private Integer intField;
        
        @TypeTransformers({StringCollectionToCommaSeparatedStringTransformer.class})
        private Boolean boolField;
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
    
    private FieldValueTransformer fieldValueTransformer;
    
    @Before
    public void setUp() throws Exception { 
        when(configBuilderFactory.getInstance(FieldValueExtractor.class)).thenReturn(fieldValueExtractor);
        when(configBuilderFactory.getInstance(ErrorMessageSetup.class)).thenReturn(errorMessageSetup);
        
        stringCollectionField = TestConfigClass.class.getDeclaredField("stringCollectionField");
        intField = TestConfigClass.class.getDeclaredField("intField");
        boolField = TestConfigClass.class.getDeclaredField("boolField");
        
        this.fieldValueTransformer = new FieldValueTransformer(configBuilderFactory);
    }

    @Test
    public void testTransformingFieldValue() {
        String content = "Alpha,Beta,Gamma";
        
        when(fieldValueExtractor.extractValue(stringCollectionField, builderConfiguration)).thenReturn(content);
        when(configBuilderFactory.getInstance(CommaSeparatedStringToStringCollectionTransformer.class)).thenReturn(new CommaSeparatedStringToStringCollectionTransformer());
                
        ArrayList<String> expectResult = Lists.newArrayList();
        for (String value : content.split(",")) {
            expectResult.add(value);
        }
        
        ArrayList<String> result = fieldValueTransformer.transformedFieldValue(stringCollectionField, builderConfiguration);
        
        verify(fieldValueExtractor).extractValue(stringCollectionField, builderConfiguration);
        
        assertThat(result, equalTo(expectResult));
    }
    
    @Test(expected = TypeTransformerException.class)
    public void testExceptionIfNoTransformerGiven() {
        when(fieldValueExtractor.extractValue(intField, builderConfiguration)).thenReturn("Olo");
        
        fieldValueTransformer.transformedFieldValue(intField, builderConfiguration);
    }
    
    @Test(expected = TypeTransformerException.class)
    public void testExceptionIfNoTransformerFound() {
        when(fieldValueExtractor.extractValue(boolField, builderConfiguration)).thenReturn(17);
        
        fieldValueTransformer.transformedFieldValue(boolField, builderConfiguration);
    }
    
    @Test
    public void testWhenNoTransformerNecessary() {
        when(fieldValueExtractor.extractValue(intField, builderConfiguration)).thenReturn(197);

        int actualResult = fieldValueTransformer.transformedFieldValue(intField, builderConfiguration);   
        assertThat(actualResult, equalTo(197));
    }

}