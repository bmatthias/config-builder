package com.tngtech.configbuilder.annotation.typetransformer;

import com.google.common.collect.Lists;
import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.exception.TypeTransformerException;
import com.tngtech.configbuilder.util.ConfigBuilderFactory;
import com.tngtech.configbuilder.util.FieldValueTransformer;
import com.tngtech.configbuilder.util.GenericsAndCastingHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StringToPrimitiveTransformerTest {

    private StringOrPrimitiveToPrimitiveTransformer stringOrPrimitiveToPrimitiveTransformer;

    @Mock
    private FieldValueTransformer fieldValueTransformer;
    @Mock
    private ConfigBuilderFactory configBuilderFactory;
    @Mock
    private ErrorMessageSetup errorMessageSetup;
    @Mock
    private GenericsAndCastingHelper genericsAndCastingHelper;

    @Before
    public void setUp() throws Exception {
        when(configBuilderFactory.getInstance(ErrorMessageSetup.class)).thenReturn(errorMessageSetup);
        when(configBuilderFactory.getInstance(GenericsAndCastingHelper.class)).thenReturn(genericsAndCastingHelper);

        when(genericsAndCastingHelper.castTypeToClass(boolean.class)).thenReturn(boolean.class);
        when(genericsAndCastingHelper.castTypeToClass(double.class)).thenReturn(double.class);
        when(genericsAndCastingHelper.castTypeToClass(int.class)).thenReturn(int.class);

        stringOrPrimitiveToPrimitiveTransformer = new StringOrPrimitiveToPrimitiveTransformer();
    }

    @Test
    public void testTransform() throws Exception {
        stringOrPrimitiveToPrimitiveTransformer.initialize(fieldValueTransformer, configBuilderFactory, boolean.class, new ArrayList<Class>());
        assertEquals(true, stringOrPrimitiveToPrimitiveTransformer.transform("true"));

        stringOrPrimitiveToPrimitiveTransformer.initialize(fieldValueTransformer, configBuilderFactory, int.class, new ArrayList<Class>());
        assertEquals(1, stringOrPrimitiveToPrimitiveTransformer.transform("1"));

        stringOrPrimitiveToPrimitiveTransformer.initialize(fieldValueTransformer, configBuilderFactory, double.class, new ArrayList<Class>());
        assertEquals(1.0, stringOrPrimitiveToPrimitiveTransformer.transform("1"));

        stringOrPrimitiveToPrimitiveTransformer.initialize(fieldValueTransformer, configBuilderFactory, double.class, new ArrayList<Class>());
        assertEquals(1.0, stringOrPrimitiveToPrimitiveTransformer.transform("1.0"));

        stringOrPrimitiveToPrimitiveTransformer.initialize(fieldValueTransformer, configBuilderFactory, boolean.class, new ArrayList<Class>());
        assertEquals(true, stringOrPrimitiveToPrimitiveTransformer.transform(true));

        stringOrPrimitiveToPrimitiveTransformer.initialize(fieldValueTransformer, configBuilderFactory, double.class, new ArrayList<Class>());
        assertEquals(1.0, stringOrPrimitiveToPrimitiveTransformer.transform(1));

        stringOrPrimitiveToPrimitiveTransformer.initialize(fieldValueTransformer, configBuilderFactory, double.class, new ArrayList<Class>());
        assertEquals(1.0, stringOrPrimitiveToPrimitiveTransformer.transform(1.0));

    }

    @Test(expected = TypeTransformerException.class)
    public void testTransformThrowsException() throws Exception {
        stringOrPrimitiveToPrimitiveTransformer.initialize(fieldValueTransformer, configBuilderFactory, int.class, new ArrayList<Class>());
        assertEquals(1, stringOrPrimitiveToPrimitiveTransformer.transform(1.0));
    }

    @Test
    public void testIsMatching() throws Exception {
        when(genericsAndCastingHelper.isPrimitiveOrWrapper(int.class)).thenReturn(true);
        when(genericsAndCastingHelper.isPrimitiveOrWrapper(Integer.class)).thenReturn(true);
        when(genericsAndCastingHelper.isPrimitiveOrWrapper(Object.class)).thenReturn(false);

        stringOrPrimitiveToPrimitiveTransformer.initialize(fieldValueTransformer, configBuilderFactory, int.class, new ArrayList<Class>());

        assertTrue(stringOrPrimitiveToPrimitiveTransformer.isMatching(int.class, Integer.class));
        assertTrue(stringOrPrimitiveToPrimitiveTransformer.isMatching(String.class, int.class));
        assertFalse(stringOrPrimitiveToPrimitiveTransformer.isMatching(int.class, Object.class));
    }
}
