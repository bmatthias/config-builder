package com.tngtech.configbuilder.annotation.typetransformer;

import com.tngtech.configbuilder.annotation.valuetransformer.StringToPathTransformer;
import com.tngtech.configbuilder.util.ConfigBuilderFactory;
import com.tngtech.configbuilder.util.FieldValueTransformer;
import com.tngtech.configbuilder.util.GenericsAndCastingHelper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.ParameterizedType;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StringToPathTransformerTest {
    private StringToPathTransformer transformer;

    @Mock
    private FieldValueTransformer fieldValueTransformer;
    @Mock
    private ConfigBuilderFactory configBuilderFactory;
    @Mock
    private GenericsAndCastingHelper genericsAndCastingHelper;

    @Before
    public void setUp() throws Exception {
        transformer = new StringToPathTransformer();
    }

    @Test
    public void testTransform() throws Exception {
        assertEquals(Paths.get("/usr"), transformer.transform("/usr"));
    }

    @Test
    public void testIsMatching() throws Exception {
        initializeFactoryAndHelperMocks();
        transformer.initialize(fieldValueTransformer, configBuilderFactory);

        assertTrue(transformer.isMatching("someString", Path.class));
        assertFalse(transformer.isMatching(new Object(), Path.class));
    }

    private void initializeFactoryAndHelperMocks(){
        when(configBuilderFactory.getInstance(GenericsAndCastingHelper.class)).thenReturn(genericsAndCastingHelper);

        when(genericsAndCastingHelper.getWrapperClassIfPrimitive(String.class)).thenReturn((Class)String.class);
        when(genericsAndCastingHelper.getWrapperClassIfPrimitive(Object.class)).thenReturn((Class) Object.class);
        when(genericsAndCastingHelper.getWrapperClassIfPrimitive(Path.class)).thenReturn((Class) Path.class);

        when(genericsAndCastingHelper.castTypeToClass(String.class)).thenReturn((Class)String.class);
        when(genericsAndCastingHelper.castTypeToClass(Object.class)).thenReturn((Class)Object.class);
        when(genericsAndCastingHelper.castTypeToClass(Path.class)).thenReturn((Class)Path.class);
        when(genericsAndCastingHelper.castTypeToClass(((ParameterizedType)(transformer.getClass().getGenericSuperclass())).getActualTypeArguments()[1])).thenReturn((Class)Path.class);
    }
}
