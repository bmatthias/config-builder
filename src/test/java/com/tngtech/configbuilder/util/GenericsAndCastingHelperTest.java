package com.tngtech.configbuilder.util;


import com.google.common.collect.Lists;
import com.tngtech.configbuilder.annotation.valuetransformer.ValueTransformer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class GenericsAndCastingHelperTest {
    
    private class TestClass extends ValueTransformer<Collection<String>, Iterable<String>> {

        @Override
        public Iterable<String> transform(Collection<String> argument) {
            return argument;
        }
    }
    
    private GenericsAndCastingHelper genericsAndCastingHelper;
    
    @Before
    public void setUp() {
        genericsAndCastingHelper = new GenericsAndCastingHelper();
    }
    
    @Test
    public void testForCorrectWrappers() {
        assertEquals(genericsAndCastingHelper.getWrapperClassIfPrimitive(int.class), Integer.class);
        assertEquals(genericsAndCastingHelper.getWrapperClassIfPrimitive(boolean.class), Boolean.class);
        assertEquals(genericsAndCastingHelper.getWrapperClassIfPrimitive(double.class), Double.class);
        assertEquals(genericsAndCastingHelper.getWrapperClassIfPrimitive(byte.class), Byte.class);

        assertEquals(genericsAndCastingHelper.getWrapperClassIfPrimitive(Double.class), Double.class);
        assertEquals(genericsAndCastingHelper.getWrapperClassIfPrimitive(Object.class), Object.class);
    }

    @Test
    public void testTypesMatch() {
        Type interfaceType = TestClass.class.getGenericSuperclass();
        Type stringCollectionType = ((ParameterizedType)interfaceType).getActualTypeArguments()[0];
        Type stringIterableType = ((ParameterizedType)interfaceType).getActualTypeArguments()[1];

        assertTrue(genericsAndCastingHelper.typesMatch(null, int.class));
        assertTrue(genericsAndCastingHelper.typesMatch(null, stringCollectionType));
        assertTrue(genericsAndCastingHelper.typesMatch(Lists.newArrayList("1", "2"), stringCollectionType));
        assertTrue(genericsAndCastingHelper.typesMatch(Lists.newArrayList("1", "2"), stringIterableType));

        assertFalse(genericsAndCastingHelper.typesMatch(Lists.newArrayList(1, 2), stringCollectionType));
        assertFalse(genericsAndCastingHelper.typesMatch(Lists.newArrayList(new Object(), new Object()), stringCollectionType));
        assertFalse(genericsAndCastingHelper.typesMatch(new Object(), stringCollectionType));
    }
    
    @Test
    public void testCastTypeToClass() {
        Type interfaceType = TestClass.class.getGenericSuperclass();
        Type[] genericTypes = ((ParameterizedType)interfaceType).getActualTypeArguments();
        
        assertEquals(genericsAndCastingHelper.castTypeToClass(genericTypes[0]).getClass(), Class.class);
        assertEquals(genericsAndCastingHelper.castTypeToClass(genericTypes[1]).getClass(), Class.class);
    }
}
