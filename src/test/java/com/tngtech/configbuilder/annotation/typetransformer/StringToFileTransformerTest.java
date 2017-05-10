package com.tngtech.configbuilder.annotation.typetransformer;

import com.tngtech.configbuilder.util.ConfigBuilderFactory;
import com.tngtech.configbuilder.util.FieldValueTransformer;
import com.tngtech.configbuilder.util.GenericsAndCastingHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StringToFileTransformerTest {

    private StringToFileTransformer transformer = new StringToFileTransformer();

    @Mock
    private FieldValueTransformer fieldValueTransformer;
    @Mock
    private ConfigBuilderFactory configBuilderFactory;

    @Test
    public void testTransform() {
        assertThat(transformer.transform(".")).isEqualTo(new File("."));
    }

    @Test
    public void testIsMatching() {
        initializeFactoryAndHelperMocks();
        transformer.initialize(fieldValueTransformer, configBuilderFactory);

        assertThat(transformer.isMatching(String.class, File.class)).isTrue();
        assertThat(transformer.isMatching(Object.class, File.class)).isFalse();
    }

    private void initializeFactoryAndHelperMocks(){
        when(configBuilderFactory.getInstance(GenericsAndCastingHelper.class)).thenReturn(new GenericsAndCastingHelper());
    }
}