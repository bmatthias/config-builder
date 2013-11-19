package com.tngtech.configbuilder.annotation.typetransformer;

import com.tngtech.configbuilder.annotation.valuetransformer.StringOrPrimitiveToPrimitiveTransformer;
import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.exception.PrimitiveParsingException;
import com.tngtech.configbuilder.util.ConfigBuilderFactory;
import com.tngtech.configbuilder.util.FieldValueTransformer;
import com.tngtech.configbuilder.util.GenericsAndCastingHelper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
        stringOrPrimitiveToPrimitiveTransformer = new StringOrPrimitiveToPrimitiveTransformer();
    }

    @Test
    public void testTransform() throws Exception {
        initializeFactoryAndHelperMocks();
        stringOrPrimitiveToPrimitiveTransformer.initialize(fieldValueTransformer, configBuilderFactory);

        stringOrPrimitiveToPrimitiveTransformer.setTargetType(boolean.class);
        assertEquals(true, stringOrPrimitiveToPrimitiveTransformer.transform("true"));

        stringOrPrimitiveToPrimitiveTransformer.setTargetType(int.class);
        assertEquals(1, stringOrPrimitiveToPrimitiveTransformer.transform("1"));

        stringOrPrimitiveToPrimitiveTransformer.setTargetType(double.class);
        assertEquals(1.0, stringOrPrimitiveToPrimitiveTransformer.transform("1"));

        stringOrPrimitiveToPrimitiveTransformer.setTargetType(double.class);
        assertEquals(1.0, stringOrPrimitiveToPrimitiveTransformer.transform("1.0"));

        stringOrPrimitiveToPrimitiveTransformer.setTargetType(boolean.class);
        assertEquals(true, stringOrPrimitiveToPrimitiveTransformer.transform(true));

        stringOrPrimitiveToPrimitiveTransformer.setTargetType(double.class);
        assertEquals(1.0, stringOrPrimitiveToPrimitiveTransformer.transform(1));

        stringOrPrimitiveToPrimitiveTransformer.setTargetType(double.class);
        assertEquals(1.0, stringOrPrimitiveToPrimitiveTransformer.transform(1.0));
    }

    @Test(expected = PrimitiveParsingException.class)
    public void testTransformThrowsException() throws Exception {
        initializeFactoryAndHelperMocks();
        stringOrPrimitiveToPrimitiveTransformer.initialize(fieldValueTransformer, configBuilderFactory);
        stringOrPrimitiveToPrimitiveTransformer.setTargetType(int.class);
        assertEquals(1, stringOrPrimitiveToPrimitiveTransformer.transform(1.0));
    }

    @Test
    public void testIsMatching() throws Exception {
        initializeFactoryAndHelperMocks();

        stringOrPrimitiveToPrimitiveTransformer.initialize(fieldValueTransformer, configBuilderFactory);

        assertFalse(stringOrPrimitiveToPrimitiveTransformer.isMatching(3, Integer.class));
        assertTrue(stringOrPrimitiveToPrimitiveTransformer.isMatching("someString", int.class));
        assertFalse(stringOrPrimitiveToPrimitiveTransformer.isMatching(3, Object.class));
    }

    private void initializeFactoryAndHelperMocks() {
        when(configBuilderFactory.getInstance(ErrorMessageSetup.class)).thenReturn(errorMessageSetup);
        when(configBuilderFactory.getInstance(GenericsAndCastingHelper.class)).thenReturn(genericsAndCastingHelper);

        when(genericsAndCastingHelper.getWrapperClassIfPrimitive(String.class)).thenReturn((Class)String.class);
        when(genericsAndCastingHelper.getWrapperClassIfPrimitive(int.class)).thenReturn((Class) int.class);
        when(genericsAndCastingHelper.getWrapperClassIfPrimitive(Integer.class)).thenReturn((Class) Integer.class);

        when(genericsAndCastingHelper.castTypeToClass(Integer.class)).thenReturn((Class)Integer.class);
        when(genericsAndCastingHelper.castTypeToClass(Object.class)).thenReturn((Class)Object.class);
        when(genericsAndCastingHelper.castTypeToClass(String.class)).thenReturn((Class)String.class);
        when(genericsAndCastingHelper.castTypeToClass(boolean.class)).thenReturn((Class)boolean.class);
        when(genericsAndCastingHelper.castTypeToClass(double.class)).thenReturn((Class)double.class);
        when(genericsAndCastingHelper.castTypeToClass(int.class)).thenReturn((Class)int.class);

        when(genericsAndCastingHelper.isPrimitiveOrWrapper(int.class)).thenReturn(true);
        when(genericsAndCastingHelper.isPrimitiveOrWrapper(Integer.class)).thenReturn(true);
        when(genericsAndCastingHelper.isPrimitiveOrWrapper(Object.class)).thenReturn(false);
    }
}
