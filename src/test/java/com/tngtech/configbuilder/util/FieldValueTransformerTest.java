package com.tngtech.configbuilder.util;


import com.google.common.collect.Lists;
import com.tngtech.configbuilder.annotation.typetransformer.*;
import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.exception.TypeTransformerException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FieldValueTransformerTest {

    private FieldValueTransformer fieldValueTransformer;

    @TypeTransformers(FieldValueTransformerComponentTest.TestTransformer.class)
    private ArrayList<Path> testField;

    @Mock
    private ConfigBuilderFactory configBuilderFactory;
    @Mock
    private ErrorMessageSetup errorMessageSetup;
    @Mock
    private GenericsAndCastingHelper genericsAndCastingHelper;
    @Mock
    private CollectionTransformer collectionTransformer;
    @Mock
    private CommaSeparatedStringToStringCollectionTransformer commaSeparatedStringToStringCollectionTransformer;
    @Mock
    private StringCollectionToCommaSeparatedStringTransformer stringCollectionToCommaSeparatedStringTransformer;
    @Mock
    private StringOrPrimitiveToPrimitiveTransformer stringOrPrimitiveToPrimitiveTransformer;
    @Mock
    private StringToPathTransformer stringToPathTransformer;
    @Mock
    private FieldValueTransformerComponentTest.TestTransformer testTransformer;

    private Field field;

    @Before
    public void setUp() throws Exception {
        when(configBuilderFactory.getInstance(ErrorMessageSetup.class)).thenReturn(errorMessageSetup);
        when(configBuilderFactory.getInstance(GenericsAndCastingHelper.class)).thenReturn(genericsAndCastingHelper);

        when(configBuilderFactory.getInstance(FieldValueTransformerComponentTest.TestTransformer.class)).thenReturn(testTransformer);
        when(configBuilderFactory.getInstance(CollectionTransformer.class)).thenReturn(collectionTransformer);
        when(configBuilderFactory.getInstance(CommaSeparatedStringToStringCollectionTransformer.class)).thenReturn(commaSeparatedStringToStringCollectionTransformer);
        when(configBuilderFactory.getInstance(StringCollectionToCommaSeparatedStringTransformer.class)).thenReturn(stringCollectionToCommaSeparatedStringTransformer);
        when(configBuilderFactory.getInstance(StringOrPrimitiveToPrimitiveTransformer.class)).thenReturn(stringOrPrimitiveToPrimitiveTransformer);
        when(configBuilderFactory.getInstance(StringToPathTransformer.class)).thenReturn(stringToPathTransformer);

        fieldValueTransformer = new FieldValueTransformer(configBuilderFactory);
        field = this.getClass().getDeclaredField("testField");
    }

    @Test
    public void testPerformNecessaryTransformationsForMatchingTypes() throws Exception {
        when(genericsAndCastingHelper.typesMatch(Matchers.any(Object.class),Matchers.any(Type.class))).thenReturn(true);
        assertEquals(1, fieldValueTransformer.transformFieldValue(field, 1));
        verify(genericsAndCastingHelper).typesMatch(1, field.getGenericType());
    }

    @Test
    public void testPerformNecessaryTransformationsForNonMatchingTypes() throws Exception {
        String input = "/etc,/usr";
        ArrayList<Path> expectedOutput = Lists.newArrayList(Paths.get("/etc"),Paths.get("/usr"));

        when(genericsAndCastingHelper.typesMatch(input,field.getGenericType())).thenReturn(false);
        when(genericsAndCastingHelper.typesMatch(Lists.newArrayList(input.split(",")),field.getGenericType())).thenReturn(false);
        when(genericsAndCastingHelper.typesMatch(expectedOutput,field.getGenericType())).thenReturn(true);
        when(genericsAndCastingHelper.getWrapperClassForPrimitive(String.class)).thenReturn(String.class);
        when(genericsAndCastingHelper.getWrapperClassForPrimitive(ArrayList.class)).thenReturn(ArrayList.class);
        when(genericsAndCastingHelper.castTypeToClass(field.getGenericType())).thenReturn(ArrayList.class);
        when(commaSeparatedStringToStringCollectionTransformer.isMatching(String.class, ArrayList.class)).thenReturn(true);
        when(collectionTransformer.isMatching(ArrayList.class, ArrayList.class)).thenReturn(true);
        when(commaSeparatedStringToStringCollectionTransformer.transform(input)).thenReturn(Lists.newArrayList(input.split(",")));
        when(collectionTransformer.transform(Lists.newArrayList(input.split(",")))).thenReturn(expectedOutput);

        assertEquals(expectedOutput, fieldValueTransformer.transformFieldValue(field, input));

        verify(testTransformer, times(2)).isMatching(Matchers.any(Class.class), Matchers.any(Class.class));
        verify(testTransformer, times(2)).setGenericsAndCastingHelper(genericsAndCastingHelper);
        verify(configBuilderFactory, times(2)).getInstance(CommaSeparatedStringToStringCollectionTransformer.class);
        verify(configBuilderFactory).getInstance(CollectionTransformer.class);
        verify(commaSeparatedStringToStringCollectionTransformer, times(2)).setGenericsAndCastingHelper(genericsAndCastingHelper);
        verify(collectionTransformer).setGenericsAndCastingHelper(genericsAndCastingHelper);
        verify(commaSeparatedStringToStringCollectionTransformer).initialize(Matchers.any(FieldValueTransformer.class), Matchers.any(ConfigBuilderFactory.class), Matchers.any(Type.class), Matchers.any(ArrayList.class));
        verify(collectionTransformer).initialize(Matchers.any(FieldValueTransformer.class), Matchers.any(ConfigBuilderFactory.class), Matchers.any(Type.class), Matchers.any(ArrayList.class));
    }

    @Test(expected = TypeTransformerException.class)
    public void testPerformNecessaryTransformationsThrowsTypeTransformerException() throws Exception {
        String input = "input";

        when(genericsAndCastingHelper.typesMatch(input,field.getGenericType())).thenReturn(false);
        when(genericsAndCastingHelper.getWrapperClassForPrimitive(String.class)).thenReturn(String.class);
        when(genericsAndCastingHelper.getWrapperClassForPrimitive(ArrayList.class)).thenReturn(ArrayList.class);
        when(genericsAndCastingHelper.castTypeToClass(field.getGenericType())).thenReturn(ArrayList.class);

        fieldValueTransformer.transformFieldValue(field, input);
    }
}
