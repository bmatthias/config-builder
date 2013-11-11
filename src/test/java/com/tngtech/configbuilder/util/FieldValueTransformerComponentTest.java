package com.tngtech.configbuilder.util;

import com.google.common.collect.Lists;
import com.tngtech.configbuilder.annotation.typetransformer.*;
import com.tngtech.configbuilder.configuration.BuilderConfiguration;
import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.exception.TypeTransformerException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

import static com.google.common.collect.Lists.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FieldValueTransformerComponentTest {
    
    public class TestTransformer extends ITypeTransformer<String, Integer> {

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

        private Collection<Path> pathCollectionField;
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
    private Field pathCollectionField;
    
    private FieldValueTransformer fieldValueTransformer;
    
    @Before
    public void setUp() throws Exception {


        when(configBuilderFactory.getInstance(FieldValueExtractor.class)).thenReturn(fieldValueExtractor);
        when(configBuilderFactory.getInstance(ErrorMessageSetup.class)).thenReturn(errorMessageSetup);
        when(configBuilderFactory.getInstance(ClassCastingHelper.class)).thenReturn(new ClassCastingHelper());

        when(configBuilderFactory.getInstance(CommaSeparatedStringToStringCollectionTransformer.class)).thenReturn(new CommaSeparatedStringToStringCollectionTransformer());
        when(configBuilderFactory.getInstance(IntegerToDoubleTransformer.class)).thenReturn(new IntegerToDoubleTransformer());
        when(configBuilderFactory.getInstance(StringCollectionToCommaSeparatedStringTransformer.class)).thenReturn(new StringCollectionToCommaSeparatedStringTransformer());
        when(configBuilderFactory.getInstance(StringToBooleanTransformer.class)).thenReturn(new StringToBooleanTransformer());
        when(configBuilderFactory.getInstance(StringToDoubleTransformer.class)).thenReturn(new StringToDoubleTransformer());
        when(configBuilderFactory.getInstance(StringToIntegerTransformer.class)).thenReturn(new StringToIntegerTransformer());
        when(configBuilderFactory.getInstance(StringToPathTransformer.class)).thenReturn(new StringToPathTransformer());

        CollectionTransformer collectionTransformer = new CollectionTransformer(configBuilderFactory);
        when(configBuilderFactory.getInstance(CollectionTransformer.class)).thenReturn(collectionTransformer);
        
        stringCollectionField = TestConfigClass.class.getDeclaredField("stringCollectionField");
        intField = TestConfigClass.class.getDeclaredField("intField");
        boolField = TestConfigClass.class.getDeclaredField("boolField");
        otherIntField = TestConfigClass.class.getDeclaredField("otherIntField");
        pathCollectionField = TestConfigClass.class.getDeclaredField("pathCollectionField");
        
        this.fieldValueTransformer = new FieldValueTransformer(configBuilderFactory);
    }

    @Test
    public void testTransformingFieldValue() {
        String content = "Alpha,Beta,Gamma";
        
        when(fieldValueExtractor.extractValue(stringCollectionField, builderConfiguration)).thenReturn(content);
                
        ArrayList<String> expectResult = newArrayList();
        for (String value : content.split(",")) {
            expectResult.add(value);
        }
        
        ArrayList<String> actualResult = (ArrayList<String>)fieldValueTransformer.transformedFieldValue(stringCollectionField, builderConfiguration);
        
        verify(fieldValueExtractor).extractValue(stringCollectionField, builderConfiguration);
        
        assertEquals(expectResult, actualResult);
    }

    @Test
    public void testIfDefaultTransformersAreFound() {
        when(fieldValueExtractor.extractValue(intField, builderConfiguration)).thenReturn("17");

        Object actualResult = fieldValueTransformer.transformedFieldValue(intField, builderConfiguration);

        assertEquals(17, actualResult);
    }

    @Test(expected = TypeTransformerException.class)
    public void testExceptionIfNoTransformerFound() {
        when(fieldValueExtractor.extractValue(boolField, builderConfiguration)).thenReturn(38.7);
        
        fieldValueTransformer.transformedFieldValue(boolField, builderConfiguration);
    }

    @Test
    public void testWhenNoTransformerNecessary() {
        when(fieldValueExtractor.extractValue(intField, builderConfiguration)).thenReturn(197);

        Object actualResult = fieldValueTransformer.transformedFieldValue(intField, builderConfiguration);
        assertEquals(197, actualResult);
    }

    @Ignore
    @Test
    public void testThatTransformersInAnnotationArePrioritized() {
        when(fieldValueExtractor.extractValue(otherIntField, builderConfiguration)).thenReturn("2");

        Object actualResult = fieldValueTransformer.transformedFieldValue(otherIntField, builderConfiguration);
        
        verify(configBuilderFactory, never()).createInstance(StringToIntegerTransformer.class);

        assertEquals(1472, actualResult);
    }

    @Test
    public void testThatMultipleTransformersAreApplied() {
        when(fieldValueExtractor.extractValue(pathCollectionField, builderConfiguration)).thenReturn("/etc,/usr");

        Collection<Path> actualResult = (Collection<Path>)fieldValueTransformer.transformedFieldValue(pathCollectionField, builderConfiguration);
        assertEquals(Lists.newArrayList(Paths.get("/etc"),Paths.get("/usr")), actualResult);
    }

    @Test
    public void testThatValueTransformerIgnoresNull() {
        when(fieldValueExtractor.extractValue(stringCollectionField, builderConfiguration)).thenReturn(null);

        Collection<Path> actualResult = (Collection<Path>)fieldValueTransformer.transformedFieldValue(pathCollectionField, builderConfiguration);
        assertEquals(null, actualResult);
    }

}