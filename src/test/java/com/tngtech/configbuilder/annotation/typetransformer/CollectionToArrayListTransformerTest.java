package com.tngtech.configbuilder.annotation.typetransformer;

import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.util.ConfigBuilderFactory;
import com.tngtech.configbuilder.util.FieldValueTransformer;
import com.tngtech.configbuilder.util.GenericsAndCastingHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CollectionToArrayListTransformerTest {

    private CollectionToArrayListTransformer collectionToArrayListTransformer = new CollectionToArrayListTransformer();

    @Mock
    private ParameterizedType type;
    @Mock
    private FieldValueTransformer fieldValueTransformer;
    @Mock
    private ConfigBuilderFactory configBuilderFactory;
    @Mock
    private ErrorMessageSetup errorMessageSetup;

    @Before
    public void setUp() {
        when(configBuilderFactory.getInstance(ErrorMessageSetup.class)).thenReturn(errorMessageSetup);
        when(configBuilderFactory.getInstance(GenericsAndCastingHelper.class)).thenReturn(new GenericsAndCastingHelper());
        collectionToArrayListTransformer.initialize(fieldValueTransformer, configBuilderFactory);
        collectionToArrayListTransformer.setTargetType(type);
    }

    @Test
    public void testTransform() {
        Set<Integer> input = newHashSet(1, 2, 3);
        when(type.getActualTypeArguments()).thenReturn(new Class[]{Double.class});
        when(fieldValueTransformer.performNecessaryTransformations(1, Double.class)).thenReturn(1.0);
        when(fieldValueTransformer.performNecessaryTransformations(2, Double.class)).thenReturn(2.0);
        when(fieldValueTransformer.performNecessaryTransformations(3, Double.class)).thenReturn(3.0);
        assertThat(collectionToArrayListTransformer.transform(input)).containsExactly(1.0, 2.0, 3.0);
    }

    @Test
    public void testIsMatching() {
        collectionToArrayListTransformer.initialize(fieldValueTransformer, configBuilderFactory);

        assertThat(collectionToArrayListTransformer.isMatching(Collection.class, ArrayList.class)).isTrue();
        assertThat(collectionToArrayListTransformer.isMatching(Collection.class, Double.class)).isFalse();
    }
}
