package com.tngtech.configbuilder.util;

import com.tngtech.configbuilder.annotation.typetransformer.CharacterSeparatedStringToStringListTransformer;
import com.tngtech.configbuilder.annotation.typetransformer.CharacterSeparatedStringToStringSetTransformer;
import com.tngtech.configbuilder.annotation.typetransformer.CollectionToArrayListTransformer;
import com.tngtech.configbuilder.annotation.typetransformer.CollectionToHashSetTransformer;
import com.tngtech.configbuilder.annotation.typetransformer.StringCollectionToCommaSeparatedStringTransformer;
import com.tngtech.configbuilder.annotation.typetransformer.StringOrPrimitiveToPrimitiveTransformer;
import com.tngtech.configbuilder.annotation.typetransformer.StringToPathTransformer;
import com.tngtech.configbuilder.annotation.typetransformer.TypeTransformers;
import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.exception.TypeTransformerException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FieldValueTransformerTest {

    private FieldValueTransformer fieldValueTransformer;

    @TypeTransformers(FieldValueTransformerComponentTest.TestTransformer.class)
    private ArrayList<Path> testField;

    @Mock
    private ConfigBuilderFactory configBuilderFactory;
    @Mock
    private ErrorMessageSetup errorMessageSetup;
    @Mock
    private GenericsAndCastingHelper genericsAndCastingHelper;
    @Mock
    private EnumTypeExtractor enumTypeExtractor;
    @Mock
    private CollectionToArrayListTransformer collectionToArrayListTransformer;
    @Mock
    private CharacterSeparatedStringToStringListTransformer characterSeparatedStringToStringListTransformer;
    @Mock
    private StringCollectionToCommaSeparatedStringTransformer stringCollectionToCommaSeparatedStringTransformer;
    @Mock
    private StringOrPrimitiveToPrimitiveTransformer stringOrPrimitiveToPrimitiveTransformer;
    @Mock
    private StringToPathTransformer stringToPathTransformer;
    @Mock
    private FieldValueTransformerComponentTest.TestTransformer testTransformer;
    @Mock
    private CollectionToHashSetTransformer collectionToHashSetTransformer;
    @Mock
    private
    CharacterSeparatedStringToStringSetTransformer characterSeparatedStringToStringSetTransformer;

    private Field field;

    @BeforeEach
    public void setUp() throws Exception {
        when(configBuilderFactory.getInstance(ErrorMessageSetup.class)).thenReturn(errorMessageSetup);
        when(configBuilderFactory.getInstance(GenericsAndCastingHelper.class)).thenReturn(genericsAndCastingHelper);
        when(configBuilderFactory.getInstance(EnumTypeExtractor.class)).thenReturn(enumTypeExtractor);

        fieldValueTransformer = new FieldValueTransformer(configBuilderFactory);
        field = this.getClass().getDeclaredField("testField");
    }

    @Test
    public void testPerformNecessaryTransformationsForMatchingTypes() {
        when(genericsAndCastingHelper.typesMatch(any(Object.class), any(Type.class))).thenReturn(true);
        assertThat(fieldValueTransformer.transformFieldValue(field, 1)).isEqualTo(1);
        verify(genericsAndCastingHelper).typesMatch(1, field.getGenericType());
    }

    @Test
    public void testPerformNecessaryTransformationsForNonMatchingTypes() {
        String input = "/etc,/usr";
        ArrayList<Path> expectedOutput = newArrayList(Paths.get("/etc"), Paths.get("/usr"));

        initializeFactoryAndHelperMocks(input);
        when(genericsAndCastingHelper.typesMatch(newArrayList(input.split(",")), field.getGenericType())).thenReturn(false);
        when(genericsAndCastingHelper.typesMatch(expectedOutput, field.getGenericType())).thenReturn(true);
        when(characterSeparatedStringToStringListTransformer.isMatching(String.class, ArrayList.class)).thenReturn(true);
        when(collectionToArrayListTransformer.isMatching(ArrayList.class, ArrayList.class)).thenReturn(true);
        when(characterSeparatedStringToStringListTransformer.transform(input)).thenReturn(newArrayList(input.split(",")));
        when(collectionToArrayListTransformer.transform(newArrayList(input.split(",")))).thenReturn(expectedOutput);

        assertThat(fieldValueTransformer.transformFieldValue(field, input)).isEqualTo(expectedOutput);

        verifyMethodCalls();
    }

    @Test
    public void testPerformNecessaryTransformationsThrowsTypeTransformerException() {
        String input = "input";

        initializeFactoryAndHelperMocks(input);

        //All mock TypeTransformers return false
        assertThrows(TypeTransformerException.class, () -> fieldValueTransformer.transformFieldValue(field, input));
    }

    private void verifyMethodCalls() {
        InOrder inOrder = inOrder(testTransformer, characterSeparatedStringToStringListTransformer, testTransformer, characterSeparatedStringToStringListTransformer, collectionToArrayListTransformer);
        inOrder.verify(testTransformer, times(2)).isMatching(any(Class.class), any(Class.class));
        verify(testTransformer, times(2)).initialize(fieldValueTransformer, configBuilderFactory, ",");
        verify(characterSeparatedStringToStringListTransformer, times(2)).initialize(fieldValueTransformer, configBuilderFactory, ",");
        verify(collectionToArrayListTransformer).initialize(fieldValueTransformer, configBuilderFactory, ",");
        verify(characterSeparatedStringToStringListTransformer).setTargetType(any(Type.class));
        verify(collectionToArrayListTransformer).setTargetType(any(Type.class));
    }

    private void initializeFactoryAndHelperMocks(String input) {
        when(configBuilderFactory.getInstance(FieldValueTransformerComponentTest.TestTransformer.class)).thenReturn(testTransformer);
        when(configBuilderFactory.getInstance(CollectionToArrayListTransformer.class)).thenReturn(collectionToArrayListTransformer);
        when(configBuilderFactory.getInstance(CollectionToHashSetTransformer.class)).thenReturn(collectionToHashSetTransformer);
        when(configBuilderFactory.getInstance(CharacterSeparatedStringToStringListTransformer.class)).thenReturn(characterSeparatedStringToStringListTransformer);
        when(configBuilderFactory.getInstance(CharacterSeparatedStringToStringSetTransformer.class)).thenReturn(characterSeparatedStringToStringSetTransformer);
        when(configBuilderFactory.getInstance(StringCollectionToCommaSeparatedStringTransformer.class)).thenReturn(stringCollectionToCommaSeparatedStringTransformer);
        when(configBuilderFactory.getInstance(StringOrPrimitiveToPrimitiveTransformer.class)).thenReturn(stringOrPrimitiveToPrimitiveTransformer);
        when(configBuilderFactory.getInstance(StringToPathTransformer.class)).thenReturn(stringToPathTransformer);

        when(genericsAndCastingHelper.typesMatch(input,field.getGenericType())).thenReturn(false);
        when(genericsAndCastingHelper.getWrapperClassIfPrimitive(String.class)).thenReturn((Class)String.class);
        when(genericsAndCastingHelper.getWrapperClassIfPrimitive(ArrayList.class)).thenReturn((Class)ArrayList.class);
        when(genericsAndCastingHelper.castTypeToClass(field.getGenericType())).thenReturn((Class)ArrayList.class);
    }
}
