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
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CollectionToArrayListTransformerTest {

    private CollectionToArrayListTransformer collectionToArrayListTransformer;

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

        collectionToArrayListTransformer = new CollectionToArrayListTransformer();
        collectionToArrayListTransformer.initialize(fieldValueTransformer, configBuilderFactory);
        collectionToArrayListTransformer.setTargetType(type);
    }

    @Test
    public void testTransform() throws Exception {
        Set<Integer> input = Sets.newHashSet(1,2,3);
        when(type.getActualTypeArguments()).thenReturn(new Class[]{Double.class});
        when(fieldValueTransformer.performNecessaryTransformations(1, Double.class)).thenReturn(1.0);
        when(fieldValueTransformer.performNecessaryTransformations(2, Double.class)).thenReturn(2.0);
        when(fieldValueTransformer.performNecessaryTransformations(3, Double.class)).thenReturn(3.0);
        assertEquals(Lists.newArrayList(1.0,2.0,3.0), collectionToArrayListTransformer.transform(input));
    }

    @Test
    public void testIsMatching() throws Exception {
        collectionToArrayListTransformer.initialize(fieldValueTransformer, configBuilderFactory);

        initializeFactoryAndHelper();

        assertTrue(collectionToArrayListTransformer.isMatching(Collection.class, ArrayList.class));
        assertFalse(collectionToArrayListTransformer.isMatching(Collection.class, Double.class));
    }

    private void initializeFactoryAndHelper() {
        when(genericsAndCastingHelper.castTypeToClass(Collection.class)).thenReturn((Class)Collection.class);
        when(genericsAndCastingHelper.castTypeToClass(ArrayList.class)).thenReturn((Class)ArrayList.class);
        when(genericsAndCastingHelper.castTypeToClass(Double.class)).thenReturn((Class)Double.class);
    }
}
