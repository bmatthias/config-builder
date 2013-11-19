package com.tngtech.configbuilder.annotation.typetransformer;

import com.google.common.collect.Sets;
import com.tngtech.configbuilder.annotation.valuetransformer.CharacterSeparatedStringToStringSetTransformer;
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
import java.util.*;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CharacterSeparatedStringToStringSetTransformerTest {
    private CharacterSeparatedStringToStringSetTransformer transformer;

    @Mock
    private FieldValueTransformer fieldValueTransformer;
    @Mock
    private ConfigBuilderFactory configBuilderFactory;
    @Mock
    private GenericsAndCastingHelper genericsAndCastingHelper;

    @Before
    public void setUp() {
        transformer = new CharacterSeparatedStringToStringSetTransformer();
    }

    @Test
    public void testTransformer() {
        Set<String> expectedResult = Sets.newHashSet("Wayne", "André", "Kanye", "Lebron");

        transformer.initialize(fieldValueTransformer, configBuilderFactory, ",");
        Set<String> actualResult = transformer.transform("Wayne,André,Kanye,Lebron");
        assertThat(actualResult,equalTo(expectedResult));

        transformer.initialize(fieldValueTransformer, configBuilderFactory, ";");
        actualResult = transformer.transform("Wayne;André;Kanye;Lebron");
        assertThat(actualResult,equalTo(expectedResult));
    }

    @Test
    public void testIsMatching() throws Exception {
        initializeFactoryAndHelperMocks();
        transformer.initialize(fieldValueTransformer, configBuilderFactory, ",");
        assertTrue(transformer.isMatching("someString", Set.class));
        assertTrue(transformer.isMatching("someString", Collection.class));
        assertFalse(transformer.isMatching(new Object(), List.class));
    }

    private void initializeFactoryAndHelperMocks(){
        when(configBuilderFactory.getInstance(GenericsAndCastingHelper.class)).thenReturn(genericsAndCastingHelper);

        when(genericsAndCastingHelper.getWrapperClassIfPrimitive(String.class)).thenReturn((Class)String.class);
        when(genericsAndCastingHelper.getWrapperClassIfPrimitive(List.class)).thenReturn((Class)List.class);
        when(genericsAndCastingHelper.getWrapperClassIfPrimitive(Collection.class)).thenReturn((Class)Collection.class);
        when(genericsAndCastingHelper.getWrapperClassIfPrimitive(Set.class)).thenReturn((Class)Set.class);
        when(genericsAndCastingHelper.getWrapperClassIfPrimitive(Object.class)).thenReturn((Class) Object.class);

        when(genericsAndCastingHelper.castTypeToClass(Set.class)).thenReturn((Class)Set.class);
        when(genericsAndCastingHelper.castTypeToClass(List.class)).thenReturn((Class)List.class);
        when(genericsAndCastingHelper.castTypeToClass(String.class)).thenReturn((Class)String.class);
        when(genericsAndCastingHelper.castTypeToClass(Collection.class)).thenReturn((Class)Collection.class);
        when(genericsAndCastingHelper.castTypeToClass(Object.class)).thenReturn((Class)Object.class);
        when(genericsAndCastingHelper.castTypeToClass(((ParameterizedType)(transformer.getClass().getGenericSuperclass())).getActualTypeArguments()[1])).thenReturn((Class)Set.class);
    }
}
