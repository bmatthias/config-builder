package com.tngtech.configbuilder.annotation.valuetransformer;

import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.util.GenericsAndCastingHelper;
import com.tngtech.configbuilder.util.ConfigBuilderFactory;
import com.tngtech.configbuilder.util.FieldValueTransformer;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Implementations of this interface transform an object into a different type of object
 * @param <SourceClass> the type of the parameter before the transformation
 * @param <TargetClass> return type
 */
public abstract class ValueTransformer<SourceClass, TargetClass> {

    protected Type targetType;
    protected FieldValueTransformer fieldValueTransformer;
    protected GenericsAndCastingHelper genericsAndCastingHelper;
    protected ErrorMessageSetup errorMessageSetup;
    protected Object[] additionalOptions;

    public abstract TargetClass transform(SourceClass argument);

    public void initialize(FieldValueTransformer fieldValueTransformer, ConfigBuilderFactory configBuilderFactory, Object... additionalOptions) {
        this.fieldValueTransformer = fieldValueTransformer;
        this.genericsAndCastingHelper = configBuilderFactory.getInstance(GenericsAndCastingHelper.class);
        this.errorMessageSetup = configBuilderFactory.getInstance(ErrorMessageSetup.class);
        this.additionalOptions = additionalOptions;
    }

    public boolean isMatching(Object sourceValue, Type targetType) {
        if(sourceValue == null) {
            return false;
        }
        Class<?> sourceClass = genericsAndCastingHelper.getWrapperClassIfPrimitive(sourceValue.getClass());
        Class<?> targetClass = genericsAndCastingHelper.getWrapperClassIfPrimitive(genericsAndCastingHelper.castTypeToClass(targetType));
        Class<?> transformerSourceClass = getTransformerSourceClass();
        Class<?> transformerTargetClass = getTransformerTargetClass();
        if(transformerSourceClass.isAssignableFrom(sourceClass) && targetClass.isAssignableFrom(transformerTargetClass)) {
            return true;
        }
        return false;
    }

    public Class<?> getTransformerSourceClass() {
        Type typeOfInterface = this.getClass().getGenericSuperclass();
        Type[] genericTypes = ((ParameterizedType) typeOfInterface).getActualTypeArguments();
        return genericsAndCastingHelper.castTypeToClass(genericTypes[0]);
    }

    public Class<?> getTransformerTargetClass() {
        Type typeOfInterface = this.getClass().getGenericSuperclass();
        Type[] genericTypes = ((ParameterizedType) typeOfInterface).getActualTypeArguments();
        return genericsAndCastingHelper.castTypeToClass(genericTypes[1]);
    }

    public void setTargetType(Type targetType) {
        this.targetType = targetType;
    }

    public boolean isContentTransformer() {
        return getTransformerTargetClass().isAssignableFrom(getTransformerSourceClass());
    }
}
