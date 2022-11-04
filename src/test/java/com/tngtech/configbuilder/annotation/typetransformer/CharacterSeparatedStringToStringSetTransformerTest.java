package com.tngtech.configbuilder.annotation.typetransformer;

import com.tngtech.configbuilder.util.ConfigBuilderFactory;
import com.tngtech.configbuilder.util.FieldValueTransformer;
import com.tngtech.configbuilder.util.GenericsAndCastingHelper;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CharacterSeparatedStringToStringSetTransformerTest {
    private CharacterSeparatedStringToStringSetTransformer transformer = new CharacterSeparatedStringToStringSetTransformer();

    @Mock
    private FieldValueTransformer fieldValueTransformer;
    @Mock
    private ConfigBuilderFactory configBuilderFactory;

    @Test
    public void testTransformer() {
        Set<String> expectedResult = newHashSet("Wayne", "André", "Kanye", "Lebron");

        transformer.initialize(fieldValueTransformer, configBuilderFactory, ",");
        Set<String> actualResult = transformer.transform("Wayne,André,Kanye,Lebron");
        assertThat(actualResult).isEqualTo(expectedResult);

        transformer.initialize(fieldValueTransformer, configBuilderFactory, ";");
        actualResult = transformer.transform("Wayne;André;Kanye;Lebron");
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    public void testIsMatching() {
        initializeFactoryAndHelperMocks();
        transformer.initialize(fieldValueTransformer, configBuilderFactory, ",");
        assertThat(transformer.isMatching(String.class, Set.class)).isTrue();
        assertThat(transformer.isMatching(String.class, Collection.class)).isTrue();
        assertThat(transformer.isMatching(Object.class, List.class)).isFalse();
    }

    private void initializeFactoryAndHelperMocks(){
        when(configBuilderFactory.getInstance(GenericsAndCastingHelper.class)).thenReturn(new GenericsAndCastingHelper());
    }
}
