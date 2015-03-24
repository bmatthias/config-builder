package com.tngtech.configbuilder.annotation.typetransformer;

import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.util.ConfigBuilderFactory;
import com.tngtech.configbuilder.util.FieldValueTransformer;
import com.tngtech.configbuilder.util.GenericsAndCastingHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StringToEnumTypeTransformerTest {

    private StringToTestEnumTransformer transformer;

    enum TestEnum {
        ONE
    }

    public static class StringToTestEnumTransformer extends StringToEnumTypeTransformer<TestEnum> {
        public StringToTestEnumTransformer() {
            super(TestEnum.class);
        }
    }

    @Mock
    private ConfigBuilderFactory configBuilderFactory;

    @Before
    public void setUp() {
        initializeFactoryMocks();
        transformer = new StringToTestEnumTransformer();
        transformer.initialize(new FieldValueTransformer(configBuilderFactory), configBuilderFactory);
    }

    private void initializeFactoryMocks() {
        when(configBuilderFactory.getInstance(ErrorMessageSetup.class)).thenReturn(new ErrorMessageSetup());
        when(configBuilderFactory.getInstance(GenericsAndCastingHelper.class)).thenReturn(new GenericsAndCastingHelper());
    }

    @Test
    public void testTransform() throws Exception {
        assertThat(transformer.transform(TestEnum.ONE.name()), is(TestEnum.ONE));
    }

    @Test
    public void testIsMatching() throws Exception {
        assertTrue(transformer.isMatching(String.class, TestEnum.class));
        assertFalse(transformer.isMatching(String.class, Integer.class));
        assertTrue(transformer.isMatching(String.class, Enum.class));
    }
}