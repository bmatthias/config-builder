package com.tngtech.configbuilder.annotation.typetransformer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.util.ConfigBuilderFactory;
import com.tngtech.configbuilder.util.FieldValueTransformer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CollectionTransformerTest {

    private CollectionTransformer collectionTransformer;

    @Mock
    private ParameterizedType type;
    @Mock
    private FieldValueTransformer fieldValueTransformer;
    @Mock
    private ConfigBuilderFactory configBuilderFactory;
    @Mock
    private ErrorMessageSetup errorMessageSetup;

    @Before
    public void setUp() throws Exception {
        when(configBuilderFactory.getInstance(ErrorMessageSetup.class)).thenReturn(errorMessageSetup);

        collectionTransformer = new CollectionTransformer();
        collectionTransformer.initialize(fieldValueTransformer, configBuilderFactory, type, Lists.newArrayList(new Class[]{StringOrPrimitiveToPrimitiveTransformer.class}));
    }

    @Test
    public void testTransform() throws Exception {
        Set<Integer> input = Sets.newHashSet(1,2,3);
        when(type.getActualTypeArguments()).thenReturn(new Class[]{Double.class});
        when(fieldValueTransformer.performNecessaryTransformations(1, Double.class, Lists.newArrayList(new Class[]{StringOrPrimitiveToPrimitiveTransformer.class}))).thenReturn(1.0);
        when(fieldValueTransformer.performNecessaryTransformations(2, Double.class, Lists.newArrayList(new Class[]{StringOrPrimitiveToPrimitiveTransformer.class}))).thenReturn(2.0);
        when(fieldValueTransformer.performNecessaryTransformations(3, Double.class, Lists.newArrayList(new Class[]{StringOrPrimitiveToPrimitiveTransformer.class}))).thenReturn(3.0);
        assertEquals(Lists.newArrayList(1.0,2.0,3.0), collectionTransformer.transform(input));
    }

    @Test
    public void testIsMatching() throws Exception {
        assertTrue(collectionTransformer.isMatching(Collection.class, ArrayList.class));
        assertFalse(collectionTransformer.isMatching(Collection.class, Double.class));
    }
}
