package com.tngtech.configbuilder.util;

import com.tngtech.configbuilder.annotation.typetransformer.TypeTransformer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class GenericsAndCastingHelperTest {
    
    private class TestClass extends TypeTransformer<Collection<String>, Iterable<String>> {
        @Override
        public Iterable<String> transform(Collection<String> argument) {
            return argument;
        }
    }
    
    private final GenericsAndCastingHelper genericsAndCastingHelper = new GenericsAndCastingHelper();

    @Test
    public void testForCorrectWrappers() {
        assertThat(genericsAndCastingHelper.getWrapperClassIfPrimitive(int.class)).isEqualTo(Integer.class);
        assertThat(genericsAndCastingHelper.getWrapperClassIfPrimitive(boolean.class)).isEqualTo(Boolean.class);
        assertThat(genericsAndCastingHelper.getWrapperClassIfPrimitive(double.class)).isEqualTo(Double.class);
        assertThat(genericsAndCastingHelper.getWrapperClassIfPrimitive(byte.class)).isEqualTo(Byte.class);

        assertThat(genericsAndCastingHelper.getWrapperClassIfPrimitive(Double.class)).isEqualTo(Double.class);
        assertThat(genericsAndCastingHelper.getWrapperClassIfPrimitive(Object.class)).isEqualTo(Object.class);
    }

    @Test
    public void testTypesMatch() {
        Type interfaceType = TestClass.class.getGenericSuperclass();
        Type stringCollectionType = ((ParameterizedType) interfaceType).getActualTypeArguments()[0];
        Type stringIterableType = ((ParameterizedType) interfaceType).getActualTypeArguments()[1];

        assertThat(genericsAndCastingHelper.typesMatch(null, int.class)).isTrue();
        assertThat(genericsAndCastingHelper.typesMatch(null, stringCollectionType)).isTrue();
        assertThat(genericsAndCastingHelper.typesMatch(newArrayList("1", "2"), stringCollectionType)).isTrue();
        assertThat(genericsAndCastingHelper.typesMatch(newArrayList("1", "2"), stringIterableType)).isTrue();

        assertThat(genericsAndCastingHelper.typesMatch(newArrayList(1, 2), stringCollectionType)).isFalse();
        assertThat(genericsAndCastingHelper.typesMatch(newArrayList(new Object(), new Object()), stringCollectionType)).isFalse();
        assertThat(genericsAndCastingHelper.typesMatch(new Object(), stringCollectionType)).isFalse();
    }
    
    @Test
    public void testCastTypeToClass() {
        Type interfaceType = TestClass.class.getGenericSuperclass();
        Type[] genericTypes = ((ParameterizedType) interfaceType).getActualTypeArguments();

        assertThat(genericsAndCastingHelper.castTypeToClass(genericTypes[0]).getClass()).isEqualTo(Class.class);
        assertThat(genericsAndCastingHelper.castTypeToClass(genericTypes[1]).getClass()).isEqualTo(Class.class);
    }
}
