package com.tngtech.configbuilder.util;


import com.tngtech.configbuilder.annotation.typetransformer.ITypeTransformer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ClassCastingHelperTest {
    
    private class testClass extends ITypeTransformer<String, Collection<String>> {

        @Override
        public Collection<String> transform(String argument) {
            return null;
        }
    }
    
    private ClassCastingHelper classCastingHelper;
    
    @Before
    public void setUp() {
        classCastingHelper = new ClassCastingHelper();
    }
    
    @Test
    public void testForCorrectWrappers() {
        assertEquals(classCastingHelper.getWrapperClassForPrimitive(int.class), Integer.class);
        assertEquals(classCastingHelper.getWrapperClassForPrimitive(boolean.class), Boolean.class);
        assertEquals(classCastingHelper.getWrapperClassForPrimitive(double.class), Double.class);
        assertEquals(classCastingHelper.getWrapperClassForPrimitive(byte.class), Byte.class);
    }
    
    @Test
    public void testCastTypeToClass() {
        Type[] interfaceType = testClass.class.getGenericInterfaces();
        Type[] genericTypes = ((ParameterizedType) interfaceType[0]).getActualTypeArguments();
        
        assertEquals(classCastingHelper.castTypeToClass(genericTypes[0]).getClass(), Class.class);
        assertEquals(classCastingHelper.castTypeToClass(genericTypes[1]).getClass(), Class.class);
    }
}
