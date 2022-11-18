package com.tngtech.configbuilder.annotation.typetransformer;

import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.exception.PrimitiveParsingException;
import com.tngtech.configbuilder.util.ConfigBuilderFactory;
import com.tngtech.configbuilder.util.FieldValueTransformer;
import com.tngtech.configbuilder.util.GenericsAndCastingHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StringOrPrimitiveToPrimitiveTransformerTest {

    private final StringOrPrimitiveToPrimitiveTransformer stringOrPrimitiveToPrimitiveTransformer = new StringOrPrimitiveToPrimitiveTransformer();

    @Mock
    private FieldValueTransformer fieldValueTransformer;
    @Mock
    private ConfigBuilderFactory configBuilderFactory;
    @Mock
    private ErrorMessageSetup errorMessageSetup;

    @BeforeEach
    void setupConfigBuilderFactoryMock() {
        when(configBuilderFactory.getInstance(ErrorMessageSetup.class)).thenReturn(errorMessageSetup);
        when(configBuilderFactory.getInstance(GenericsAndCastingHelper.class)).thenReturn(new GenericsAndCastingHelper());
    }

    @Test
    public void testTransform() {
        stringOrPrimitiveToPrimitiveTransformer.initialize(fieldValueTransformer, configBuilderFactory);

        stringOrPrimitiveToPrimitiveTransformer.setTargetType(boolean.class);
        assertThat(stringOrPrimitiveToPrimitiveTransformer.transform("true")).isEqualTo(true);

        stringOrPrimitiveToPrimitiveTransformer.setTargetType(int.class);
        assertThat(stringOrPrimitiveToPrimitiveTransformer.transform("1")).isEqualTo(1);

        stringOrPrimitiveToPrimitiveTransformer.setTargetType(double.class);
        assertThat(stringOrPrimitiveToPrimitiveTransformer.transform("1")).isEqualTo(1.0);

        stringOrPrimitiveToPrimitiveTransformer.setTargetType(double.class);
        assertThat(stringOrPrimitiveToPrimitiveTransformer.transform("1.0")).isEqualTo(1.0);

        stringOrPrimitiveToPrimitiveTransformer.setTargetType(boolean.class);
        assertThat(stringOrPrimitiveToPrimitiveTransformer.transform(true)).isEqualTo(true);

        stringOrPrimitiveToPrimitiveTransformer.setTargetType(double.class);
        assertThat(stringOrPrimitiveToPrimitiveTransformer.transform(1)).isEqualTo(1.0);

        stringOrPrimitiveToPrimitiveTransformer.setTargetType(double.class);
        assertThat(stringOrPrimitiveToPrimitiveTransformer.transform(1.0)).isEqualTo(1.0);
    }

    @Test
    public void testThatSurroundingWhiteSpaceIsIgnored() {
        stringOrPrimitiveToPrimitiveTransformer.initialize(fieldValueTransformer, configBuilderFactory);

        stringOrPrimitiveToPrimitiveTransformer.setTargetType(boolean.class);
        assertThat(stringOrPrimitiveToPrimitiveTransformer.transform("true ")).isEqualTo(true);
        assertThat(stringOrPrimitiveToPrimitiveTransformer.transform(" true ")).isEqualTo(true);
        assertThat(stringOrPrimitiveToPrimitiveTransformer.transform(" true")).isEqualTo(true);

        stringOrPrimitiveToPrimitiveTransformer.setTargetType(int.class);
        assertThat(stringOrPrimitiveToPrimitiveTransformer.transform("1 ")).isEqualTo(1);
        assertThat(stringOrPrimitiveToPrimitiveTransformer.transform(" 1 ")).isEqualTo(1);
        assertThat(stringOrPrimitiveToPrimitiveTransformer.transform(" 1")).isEqualTo(1);
    }

    @Test
    public void testTransformThrowsException() {
        stringOrPrimitiveToPrimitiveTransformer.initialize(fieldValueTransformer, configBuilderFactory);
        stringOrPrimitiveToPrimitiveTransformer.setTargetType(int.class);
        assertThrows(PrimitiveParsingException.class, () -> stringOrPrimitiveToPrimitiveTransformer.transform(1.0));
    }

    @Test
    public void testIsMatching() {
        stringOrPrimitiveToPrimitiveTransformer.initialize(fieldValueTransformer, configBuilderFactory);

        assertThat(stringOrPrimitiveToPrimitiveTransformer.isMatching(int.class, Integer.class)).isTrue();
        assertThat(stringOrPrimitiveToPrimitiveTransformer.isMatching(String.class, int.class)).isTrue();
        assertThat(stringOrPrimitiveToPrimitiveTransformer.isMatching(int.class, Object.class)).isFalse();
    }
}
