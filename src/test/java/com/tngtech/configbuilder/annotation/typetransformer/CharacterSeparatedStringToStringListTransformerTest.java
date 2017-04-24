package com.tngtech.configbuilder.annotation.typetransformer;

import com.tngtech.configbuilder.util.ConfigBuilderFactory;
import com.tngtech.configbuilder.util.FieldValueTransformer;
import com.tngtech.configbuilder.util.GenericsAndCastingHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CharacterSeparatedStringToStringListTransformerTest {
    private CharacterSeparatedStringToStringListTransformer transformer = new CharacterSeparatedStringToStringListTransformer();

    @Mock
    private FieldValueTransformer fieldValueTransformer;
    @Mock
    private ConfigBuilderFactory configBuilderFactory;
    @Mock
    private GenericsAndCastingHelper genericsAndCastingHelper;

    @Test
    public void testTransformer() {
        Collection<String> expectedResult = newArrayList("Wayne", "André", "Kanye", "Lebron");

        transformer.initialize(fieldValueTransformer, configBuilderFactory, ",");
        Collection<String> actualResult = transformer.transform("Wayne,André,Kanye,Lebron");
        assertThat(actualResult).isEqualTo(expectedResult);

        transformer.initialize(fieldValueTransformer, configBuilderFactory, ";");
        actualResult = transformer.transform("Wayne;André;Kanye;Lebron");
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    public void testIsMatching() {
        initializeFactoryAndHelperMocks();
        transformer.initialize(fieldValueTransformer, configBuilderFactory, ",");
        assertThat(transformer.isMatching(String.class, List.class)).isTrue();
        assertThat(transformer.isMatching(String.class, Collection.class)).isTrue();
        assertThat(transformer.isMatching(String.class, Set.class)).isFalse();
        assertThat(transformer.isMatching(Object.class, List.class)).isFalse();
    }

    private void initializeFactoryAndHelperMocks() {
        when(configBuilderFactory.getInstance(GenericsAndCastingHelper.class)).thenReturn(genericsAndCastingHelper);

        when(genericsAndCastingHelper.castTypeToClass(List.class)).thenReturn((Class) List.class);
        when(genericsAndCastingHelper.castTypeToClass(String.class)).thenReturn((Class) String.class);
        when(genericsAndCastingHelper.castTypeToClass(Collection.class)).thenReturn((Class) Collection.class);
        when(genericsAndCastingHelper.castTypeToClass(Object.class)).thenReturn((Class) Object.class);
        when(genericsAndCastingHelper.castTypeToClass(((ParameterizedType) (transformer.getClass().getGenericSuperclass())).getActualTypeArguments()[1])).thenReturn((Class) List.class);
    }
}
