package com.tngtech.configbuilder.annotation.typetransformer;

import com.tngtech.configbuilder.util.ConfigBuilderFactory;
import com.tngtech.configbuilder.util.FieldValueTransformer;
import com.tngtech.configbuilder.util.GenericsAndCastingHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TypeTransformerTest {

    private static class TestTypeTransformer extends TypeTransformer<String, Integer> {
        @Override
        public Integer transform(String argument) {
            return Integer.getInteger(argument);
        }
    }

    private static class TestIntermediateTypeTransformer<T> extends TypeTransformer<String, T> {
        @Override
        public T transform(String argument) {
            return null;
        }
    }

    private static class TestInheritedTypeTransformer extends TestIntermediateTypeTransformer<Integer> {
    }

    private static class TestUntypedIntermediateTypeTransformer<U, V> extends TypeTransformer<U, V> {
        @Override
        public V transform(U argument) {
            return null;
        }
    }

    private static class TestUntypedInheritedTypeTransformer extends TestUntypedIntermediateTypeTransformer<String, Integer> {
    }

    private static class TestUntypedRevertedIntermediateTypeTransformer<V, U> extends TypeTransformer<U, V> {
        @Override
        public V transform(U argument) {
            return null;
        }
    }

    private static class TestUntypedRevertedInheritedTypeTransformer extends TestUntypedRevertedIntermediateTypeTransformer<Integer, String> {
    }

    @Mock
    private FieldValueTransformer fieldValueTransformer;
    @Mock
    private ConfigBuilderFactory configBuilderFactory;

    @Before
    public void setUp() {
        when(configBuilderFactory.getInstance(GenericsAndCastingHelper.class)).thenReturn(new GenericsAndCastingHelper());
    }

    private TypeTransformer createSimpleTypeTransformer() {
        TypeTransformer typeTransformer = new TestTypeTransformer();
        typeTransformer.initialize(fieldValueTransformer, configBuilderFactory);
        return typeTransformer;
    }

    private TypeTransformer createInheritedTypeTransformer() {
        TypeTransformer typeTransformer = new TestInheritedTypeTransformer();
        typeTransformer.initialize(fieldValueTransformer, configBuilderFactory);
        return typeTransformer;
    }

    private TypeTransformer createUntypedInheritedTypeTransformer() {
        TypeTransformer typeTransformer = new TestUntypedInheritedTypeTransformer();
        typeTransformer.initialize(fieldValueTransformer, configBuilderFactory);
        return typeTransformer;
    }

    private TypeTransformer createUntypedRevertedInheritedTypeTransformer() {
        TypeTransformer typeTransformer = new TestUntypedRevertedInheritedTypeTransformer();
        typeTransformer.initialize(fieldValueTransformer, configBuilderFactory);
        return typeTransformer;
    }

    @Test
    public void testSimpleTypeTransformer() {
        final TypeTransformer typeTransformer = createSimpleTypeTransformer();

        //noinspection unchecked
        assertThat(typeTransformer.isMatching(String.class, Integer.class)).isTrue();
    }

    @Test
    public void testInheritedTypeTransformerMatch() {
        final TypeTransformer typeTransformer = createInheritedTypeTransformer();

        //noinspection unchecked
        assertThat(typeTransformer.isMatching(String.class, Integer.class)).isTrue();
    }

    @Test
    public void testUntypedInheritedTypeTransformerMatch() {
        final TypeTransformer typeTransformer = createUntypedInheritedTypeTransformer();

        //noinspection unchecked
        assertThat(typeTransformer.isMatching(String.class, Integer.class)).isTrue();
    }

    @Test
    public void testUntypedRevertedInheritedTypeTransformerMatch() {
        final TypeTransformer typeTransformer = createUntypedRevertedInheritedTypeTransformer();

        //noinspection uncheckedy
        assertThat(typeTransformer.isMatching(String.class, Integer.class)).isTrue();
    }
}