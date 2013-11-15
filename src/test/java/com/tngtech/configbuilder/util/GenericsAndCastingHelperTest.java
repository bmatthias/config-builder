package com.tngtech.configbuilder.util;


import com.tngtech.configbuilder.annotation.typetransformer.TypeTransformer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class GenericsAndCastingHelperTest {
    
    private class TestClass extends TypeTransformer<String, Collection<String>> {

        @Override
        public Collection<String> transform(String argument) {
            return null;
        }
    }
    
    private GenericsAndCastingHelper genericsAndCastingHelper;
    
    @Before
    public void setUp() {
        genericsAndCastingHelper = new GenericsAndCastingHelper();
    }
    
    @Test
    public void testForCorrectWrappers() {
        assertEquals(genericsAndCastingHelper.getWrapperClassForPrimitive(int.class), Integer.class);
        assertEquals(genericsAndCastingHelper.getWrapperClassForPrimitive(boolean.class), Boolean.class);
        assertEquals(genericsAndCastingHelper.getWrapperClassForPrimitive(double.class), Double.class);
        assertEquals(genericsAndCastingHelper.getWrapperClassForPrimitive(byte.class), Byte.class);
    }
    
    @Test
    public void testCastTypeToClass() {
        Type interfaceType = TestClass.class.getGenericSuperclass();
        Type[] genericTypes = ((ParameterizedType)interfaceType).getActualTypeArguments();
        
        assertEquals(genericsAndCastingHelper.castTypeToClass(genericTypes[0]).getClass(), Class.class);
        assertEquals(genericsAndCastingHelper.castTypeToClass(genericTypes[1]).getClass(), Class.class);
    }
}
