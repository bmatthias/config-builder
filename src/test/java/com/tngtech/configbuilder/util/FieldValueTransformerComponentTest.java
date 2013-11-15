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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FieldValueTransformerComponentTest {
    
    public class TestTransformer extends TypeTransformer<String, Integer> {

        @Override
        public Integer transform(String argument) {
            return 1472;
        }
    }

    public static class AnotherTestTransformer extends TypeTransformer<String, Integer> {

        @Override
        public Integer transform(String argument) {
            return 1472;
        }
    }
    
    private class TestConfigClass {

        @TypeTransformers({CommaSeparatedStringToStringCollectionTransformer.class})
        private Collection<String> stringCollectionField;
        private int intField;
        private double doubleField;
        private Boolean boolField;
        @TypeTransformers({TestTransformer.class})
        private int otherIntField;
        @TypeTransformers({AnotherTestTransformer.class})
        private Integer integerField;
        private Collection<Path> pathCollectionField;
        private Collection<Integer> integerCollectionField;
        private Collection<Object> objectCollectionField;
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
    private Field doubleField;
    private Field boolField;
    private Field otherIntField;
    private Field integerField;
    private Field pathCollectionField;
    private Field integerCollectionField;
    private Field objectCollectionField;
    
    private FieldValueTransformer fieldValueTransformer;
    
    @Before
    public void setUp() throws Exception {


        when(configBuilderFactory.getInstance(FieldValueExtractor.class)).thenReturn(fieldValueExtractor);
        when(configBuilderFactory.getInstance(ErrorMessageSetup.class)).thenReturn(errorMessageSetup);
        when(configBuilderFactory.getInstance(GenericsAndCastingHelper.class)).thenReturn(new GenericsAndCastingHelper());

        when(configBuilderFactory.getInstance(CommaSeparatedStringToStringCollectionTransformer.class)).thenReturn(new CommaSeparatedStringToStringCollectionTransformer());
        when(configBuilderFactory.getInstance(StringCollectionToCommaSeparatedStringTransformer.class)).thenReturn(new StringCollectionToCommaSeparatedStringTransformer());
        when(configBuilderFactory.getInstance(StringToPathTransformer.class)).thenReturn(new StringToPathTransformer());
        when(configBuilderFactory.getInstance(CollectionTransformer.class)).thenReturn(new CollectionTransformer());
        when(configBuilderFactory.getInstance(StringOrPrimitiveToPrimitiveTransformer.class)).thenReturn(new StringOrPrimitiveToPrimitiveTransformer());
        when(configBuilderFactory.getInstance(TestTransformer.class)).thenReturn(new TestTransformer());
        
        stringCollectionField = TestConfigClass.class.getDeclaredField("stringCollectionField");
        intField = TestConfigClass.class.getDeclaredField("intField");
        boolField = TestConfigClass.class.getDeclaredField("boolField");
        otherIntField = TestConfigClass.class.getDeclaredField("otherIntField");
        integerField = TestConfigClass.class.getDeclaredField("integerField");
        pathCollectionField = TestConfigClass.class.getDeclaredField("pathCollectionField");
        integerCollectionField = TestConfigClass.class.getDeclaredField("integerCollectionField");
        doubleField = TestConfigClass.class.getDeclaredField("doubleField");
        objectCollectionField = TestConfigClass.class.getDeclaredField("objectCollectionField");
        
        this.fieldValueTransformer = new FieldValueTransformer(configBuilderFactory);
    }

    @Test
    public void testTransformingStringToStringCollection() {
        ArrayList<String> actualResult = (ArrayList<String>)fieldValueTransformer.transformFieldValue(stringCollectionField, "Alpha,Beta,Gamma");
        assertEquals(Lists.newArrayList("Alpha","Beta","Gamma"), actualResult);
    }

    @Test
    public void testTransformingStringToInt() {
        Object actualResult = fieldValueTransformer.transformFieldValue(intField, "17");

        assertEquals(17, actualResult);
    }

    @Test
    public void testTransformingIntegerToDouble() {
        Object actualResult = fieldValueTransformer.transformFieldValue(doubleField, 17);
        assertEquals(17.0, actualResult);
    }

    @Test(expected = TypeTransformerException.class)
    public void testExceptionIfValueCannotBeParsedToBoolean() {
        fieldValueTransformer.transformFieldValue(boolField, 38.7);
    }

    @Test
    public void testWhenNoTransformerNecessary() {
        Object actualResult = fieldValueTransformer.transformFieldValue(intField, 197);
        assertEquals(197, actualResult);
    }

    @Test
    public void testThatTransformersInAnnotationArePrioritized() {
        Object actualResult = fieldValueTransformer.transformFieldValue(otherIntField, "2");
        assertEquals(1472, actualResult);
    }

    @Test
    public void testTransformingStringToPathCollection() {
        Collection<Path> actualResult = (Collection<Path>)fieldValueTransformer.transformFieldValue(pathCollectionField, "/etc,/usr");
        assertEquals(Lists.newArrayList(Paths.get("/etc"),Paths.get("/usr")), actualResult);
    }

    @Test
    public void testTransformingStringToIntegerCollection() {
        Collection<Integer> actualResult = (Collection<Integer>)fieldValueTransformer.transformFieldValue(integerCollectionField, "3,4");
        assertEquals(Lists.newArrayList(3,4), actualResult);
    }

    @Test
    public void testTransformingStringToObjectCollection() {
        Collection<Object> actualResult = (Collection<Object>)fieldValueTransformer.transformFieldValue(objectCollectionField, "someString,anotherString");
        assertEquals(Lists.newArrayList("someString","anotherString"), actualResult);
    }

    @Test
    public void testThatValueTransformerIgnoresNull() {
        Collection<Path> actualResult = (Collection<Path>)fieldValueTransformer.transformFieldValue(pathCollectionField, null);
        assertEquals(null, actualResult);
    }
}