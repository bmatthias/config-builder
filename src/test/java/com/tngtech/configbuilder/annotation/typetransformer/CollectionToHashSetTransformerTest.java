package com.tngtech.configbuilder.annotation.typetransformer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CollectionToHashSetTransformerTest {

    private CollectionToHashSetTransformer collectionToHashSetTransformer;

    @Mock
    private ParameterizedType type;
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

        collectionToHashSetTransformer = new CollectionToHashSetTransformer();
        collectionToHashSetTransformer.initialize(fieldValueTransformer, configBuilderFactory);
        collectionToHashSetTransformer.setTargetType(type);
    }

    @Test
    public void testTransform() throws Exception {
        Set<Integer> input = Sets.newHashSet(1,2,3);
        when(type.getActualTypeArguments()).thenReturn(new Class[]{Double.class});
        when(fieldValueTransformer.performNecessaryTransformations(1, Double.class)).thenReturn(1.0);
        when(fieldValueTransformer.performNecessaryTransformations(2, Double.class)).thenReturn(2.0);
        when(fieldValueTransformer.performNecessaryTransformations(3, Double.class)).thenReturn(3.0);
        assertEquals(Sets.newHashSet(1.0, 2.0, 3.0), collectionToHashSetTransformer.transform(input));
    }

    @Test
    public void testIsMatching() throws Exception {
        collectionToHashSetTransformer.initialize(fieldValueTransformer, configBuilderFactory);

        initializeFactoryAndHelper();

        assertTrue(collectionToHashSetTransformer.isMatching(ArrayList.class, Set.class));
        assertFalse(collectionToHashSetTransformer.isMatching(ArrayList.class, ArrayList.class));
        assertFalse(collectionToHashSetTransformer.isMatching(Collection.class, Double.class));
    }

    private void initializeFactoryAndHelper() {
        when(genericsAndCastingHelper.castTypeToClass(Collection.class)).thenReturn(Collection.class);
        when(genericsAndCastingHelper.castTypeToClass(Set.class)).thenReturn(Set.class);
        when(genericsAndCastingHelper.castTypeToClass(HashSet.class)).thenReturn(HashSet.class);
        when(genericsAndCastingHelper.castTypeToClass(ArrayList.class)).thenReturn(ArrayList.class);
        when(genericsAndCastingHelper.castTypeToClass(Double.class)).thenReturn(Double.class);
    }
}
