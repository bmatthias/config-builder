package com.tngtech.configbuilder.util;


import com.google.common.collect.Lists;
import com.tngtech.configbuilder.annotation.typetransformer.*;
import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.exception.TypeTransformerException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

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
    private CollectionToArrayListTransformer collectionToArrayListTransformer;
    @Mock
    private CharacterSeparatedStringToStringListTransformer characterSeparatedStringToStringListTransformer;
    @Mock
    private StringCollectionToCommaSeparatedStringTransformer stringCollectionToCommaSeparatedStringTransformer;
    @Mock
    private StringOrPrimitiveToPrimitiveTransformer stringOrPrimitiveToPrimitiveTransformer;
    @Mock
    private StringToPathTransformer stringToPathTransformer;
    @Mock
    private FieldValueTransformerComponentTest.TestTransformer testTransformer;
    @Mock
    private CollectionToHashSetTransformer collectionToHashSetTransformer;
    @Mock
    private
    CharacterSeparatedStringToStringSetTransformer characterSeparatedStringToStringSetTransformer;

    private Field field;

    @Before
    public void setUp() throws Exception {
        when(configBuilderFactory.getInstance(ErrorMessageSetup.class)).thenReturn(errorMessageSetup);
        when(configBuilderFactory.getInstance(GenericsAndCastingHelper.class)).thenReturn(genericsAndCastingHelper);

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

        initializeFactoryAndHelperMocks(input, expectedOutput);
        when(characterSeparatedStringToStringListTransformer.isMatching(String.class, ArrayList.class)).thenReturn(true);
        when(collectionToArrayListTransformer.isMatching(ArrayList.class, ArrayList.class)).thenReturn(true);
        when(characterSeparatedStringToStringListTransformer.transform(input)).thenReturn(Lists.newArrayList(input.split(",")));
        when(collectionToArrayListTransformer.transform(Lists.newArrayList(input.split(",")))).thenReturn(expectedOutput);

        assertEquals(expectedOutput, fieldValueTransformer.transformFieldValue(field, input));

        verifyMethodCalls();
    }

    @Test(expected = TypeTransformerException.class)
    public void testPerformNecessaryTransformationsThrowsTypeTransformerException() throws Exception {
        String input = "input";

        initializeFactoryAndHelperMocks(input, null);

        //All mock TypeTransformers return false
        fieldValueTransformer.transformFieldValue(field, input);
    }

    private void verifyMethodCalls() {
        InOrder inOrder = inOrder(testTransformer, characterSeparatedStringToStringListTransformer, testTransformer, characterSeparatedStringToStringListTransformer, collectionToArrayListTransformer);
        inOrder.verify(testTransformer, times(2)).isMatching(Matchers.any(Class.class), Matchers.any(Class.class));
        verify(testTransformer, times(2)).initialize(fieldValueTransformer, configBuilderFactory, new Object[]{","});
        verify(characterSeparatedStringToStringListTransformer, times(2)).initialize(fieldValueTransformer, configBuilderFactory, new Object[]{","});
        verify(collectionToArrayListTransformer).initialize(fieldValueTransformer, configBuilderFactory, new Object[]{","});
        verify(characterSeparatedStringToStringListTransformer).setTargetType(Matchers.any(Type.class));
        verify(collectionToArrayListTransformer).setTargetType(Matchers.any(Type.class));
    }

    private void initializeFactoryAndHelperMocks(String input, ArrayList<Path> expectedOutput) {
        when(configBuilderFactory.getInstance(FieldValueTransformerComponentTest.TestTransformer.class)).thenReturn(testTransformer);
        when(configBuilderFactory.getInstance(CollectionToArrayListTransformer.class)).thenReturn(collectionToArrayListTransformer);
        when(configBuilderFactory.getInstance(CollectionToHashSetTransformer.class)).thenReturn(collectionToHashSetTransformer);
        when(configBuilderFactory.getInstance(CharacterSeparatedStringToStringListTransformer.class)).thenReturn(characterSeparatedStringToStringListTransformer);
        when(configBuilderFactory.getInstance(CharacterSeparatedStringToStringSetTransformer.class)).thenReturn(characterSeparatedStringToStringSetTransformer);
        when(configBuilderFactory.getInstance(StringCollectionToCommaSeparatedStringTransformer.class)).thenReturn(stringCollectionToCommaSeparatedStringTransformer);
        when(configBuilderFactory.getInstance(StringOrPrimitiveToPrimitiveTransformer.class)).thenReturn(stringOrPrimitiveToPrimitiveTransformer);
        when(configBuilderFactory.getInstance(StringToPathTransformer.class)).thenReturn(stringToPathTransformer);

        when(genericsAndCastingHelper.typesMatch(input,field.getGenericType())).thenReturn(false);
        when(genericsAndCastingHelper.typesMatch(Lists.newArrayList(input.split(",")), field.getGenericType())).thenReturn(false);
        when(genericsAndCastingHelper.typesMatch(expectedOutput, field.getGenericType())).thenReturn(true);
        when(genericsAndCastingHelper.getWrapperClassIfPrimitive(String.class)).thenReturn((Class)String.class);
        when(genericsAndCastingHelper.getWrapperClassIfPrimitive(ArrayList.class)).thenReturn((Class)ArrayList.class);
        when(genericsAndCastingHelper.castTypeToClass(field.getGenericType())).thenReturn((Class)ArrayList.class);
        when(genericsAndCastingHelper.castTypeToClass(String.class)).thenReturn((Class)String.class);
    }
}
