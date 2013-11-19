package com.tngtech.configbuilder.annotation.typetransformer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tngtech.configbuilder.annotation.valuetransformer.StringCollectionToCommaSeparatedStringTransformer;
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
public class StringCollectionToCommaSeparatedStringTransformerTest {
    private StringCollectionToCommaSeparatedStringTransformer transformer;

    @Mock
    private FieldValueTransformer fieldValueTransformer;
    @Mock
    private ConfigBuilderFactory configBuilderFactory;
    @Mock
    private GenericsAndCastingHelper genericsAndCastingHelper;

    @Before
    public void setUp() {
        transformer = new StringCollectionToCommaSeparatedStringTransformer();
    }

    @Test
    public void testTransformer() {
        Collection<String> collection = Lists.newArrayList("Rakim","Lakim Shabazz","2Pac");

        transformer.initialize(fieldValueTransformer, configBuilderFactory, ",");
        String actualResult = transformer.transform(collection);
        assertThat(actualResult, equalTo("Rakim,Lakim Shabazz,2Pac"));

        transformer.initialize(fieldValueTransformer, configBuilderFactory, ";");
        actualResult = transformer.transform(collection);
        assertThat(actualResult, equalTo("Rakim;Lakim Shabazz;2Pac"));
    }

    @Test
    public void testIsMatching() throws Exception {
        initializeFactoryAndHelperMocks();
        transformer.initialize(fieldValueTransformer, configBuilderFactory, ",");
        assertTrue(transformer.isMatching(Sets.newHashSet("1","2"), String.class));
        assertTrue(transformer.isMatching(Lists.newArrayList("1","2"), String.class));
        assertFalse(transformer.isMatching(new Object(), String.class));
    }

    private void initializeFactoryAndHelperMocks(){
        when(configBuilderFactory.getInstance(GenericsAndCastingHelper.class)).thenReturn(genericsAndCastingHelper);

        when(genericsAndCastingHelper.getWrapperClassIfPrimitive(String.class)).thenReturn((Class)String.class);
        when(genericsAndCastingHelper.getWrapperClassIfPrimitive(ArrayList.class)).thenReturn((Class)ArrayList.class);
        when(genericsAndCastingHelper.getWrapperClassIfPrimitive(HashSet.class)).thenReturn((Class)HashSet.class);
        when(genericsAndCastingHelper.getWrapperClassIfPrimitive(Object.class)).thenReturn((Class) Object.class);

        when(genericsAndCastingHelper.castTypeToClass(HashSet.class)).thenReturn((Class)HashSet.class);
        when(genericsAndCastingHelper.castTypeToClass(ArrayList.class)).thenReturn((Class)ArrayList.class);
        when(genericsAndCastingHelper.castTypeToClass(String.class)).thenReturn((Class)String.class);
        when(genericsAndCastingHelper.castTypeToClass(Object.class)).thenReturn((Class)Object.class);
        when(genericsAndCastingHelper.castTypeToClass(((ParameterizedType)(transformer.getClass().getGenericSuperclass())).getActualTypeArguments()[0])).thenReturn((Class)Collection.class);
    }
}
