package com.tngtech.configbuilder.util;

import com.google.common.collect.Lists;
import com.tngtech.configbuilder.annotation.configuration.Separator;
import com.tngtech.configbuilder.annotation.valuetransformer.*;
import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.exception.TypeTransformerException;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

//TODO: Content transformers (transform even if types already match, allow null as argument)
public class FieldValueTransformer {

    private final static Logger log = Logger.getLogger(FieldValueTransformer.class);

    private final ConfigBuilderFactory configBuilderFactory;
    private final GenericsAndCastingHelper genericsAndCastingHelper;
    private Object[] additionalOptions;

    //Order is important: Prefer List over Set if both apply!
    private final ArrayList<Class<? extends ValueTransformer>> defaultTransformers = Lists.<Class<? extends ValueTransformer>>newArrayList(
            StringOrPrimitiveToPrimitiveTransformer.class,
            CharacterSeparatedStringToStringListTransformer.class,
            CharacterSeparatedStringToStringSetTransformer.class,
            CollectionToArrayListTransformer.class,
            CollectionToHashSetTransformer.class,
            StringCollectionToCommaSeparatedStringTransformer.class,
            StringToPathTransformer.class);

    private ArrayList<ValueTransformer> typeTransformers = Lists.newArrayList();
    private ArrayList<ValueTransformer> contentTransformers = Lists.newArrayList();

    public FieldValueTransformer(ConfigBuilderFactory configBuilderFactory) {
        this.configBuilderFactory = configBuilderFactory;
        this.genericsAndCastingHelper = configBuilderFactory.getInstance(GenericsAndCastingHelper.class);
    }

    public Object transformFieldValue(Field field, Object sourceValue) {
        initialize(field);
        sourceValue = performApplicableTransformations(sourceValue, field.getGenericType());
        reset();
        return sourceValue;
    }

    private void initialize(Field field) {
        additionalOptions = field.isAnnotationPresent(Separator.class)? new Object[]{field.getAnnotation(Separator.class).value()} : new Object[]{","};
        for(Class<? extends ValueTransformer> transformerClass : getAllTransformers(field)) {
            ValueTransformer transformer = configBuilderFactory.getInstance(transformerClass);
            transformer.initialize(this, configBuilderFactory, additionalOptions);
            if(transformer.isContentTransformer()) {
                contentTransformers.add(transformer);
            }
            else {
                typeTransformers.add(transformer);
            }
        }
    }

    private ArrayList<Class<? extends ValueTransformer>> getAllTransformers(Field field) {
        ArrayList<Class<? extends ValueTransformer>> allTransformers = getUserSuggestedTransformers(field);
        allTransformers.addAll(defaultTransformers);
        return  allTransformers;
    }

    private ArrayList<Class<? extends ValueTransformer>> getUserSuggestedTransformers(Field field) {
        if(field.isAnnotationPresent(ValueTransformers.class)) {
            ValueTransformers annotation =  field.getAnnotation(ValueTransformers.class);
            return Lists.newArrayList(annotation.value());
        } else {
            return Lists.newArrayList();
        }
    }

    public Object performApplicableTransformations(Object sourceValue, Type targetType) {
        if(genericsAndCastingHelper.typesMatch(sourceValue, targetType)) {
            ValueTransformer transformer = findApplicableTransformer(sourceValue, targetType, contentTransformers);
            return transformer == null ? sourceValue : transformer.transform(sourceValue);
        }
        else {
            log.info(String.format("Searching for a transformer from %s to %s", sourceValue == null ? "null" : sourceValue.getClass().getSimpleName(), genericsAndCastingHelper.castTypeToClass(targetType).getSimpleName()));
            ValueTransformer transformer = findApplicableTransformer(sourceValue, targetType, typeTransformers);
            return transformer == null ? sourceValue : performApplicableTransformations(transformer.transform(sourceValue), targetType);
        }
    }

    private ValueTransformer findApplicableTransformer(Object sourceValue, Type targetType, List<ValueTransformer> transformers) {
        for(ValueTransformer<?,?> transformer: transformers) {
            if(transformer.isMatching(sourceValue, targetType)) {
                transformer.setTargetType(targetType);
                return transformer;
            }
        }
        return null;
    }

    private void reset() {
        typeTransformers = Lists.newArrayList();
        contentTransformers = Lists.newArrayList();
    }
}
