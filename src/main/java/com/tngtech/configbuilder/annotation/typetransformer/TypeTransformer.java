package com.tngtech.configbuilder.annotation.typetransformer;

import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.util.GenericsAndCastingHelper;
import com.tngtech.configbuilder.util.ConfigBuilderFactory;
import com.tngtech.configbuilder.util.FieldValueTransformer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Implementations of this interface transform an object into a different type of object
 * @param <SourceClass> the type of the parameter before the transformation
 * @param <TargetClass> return type
 */
public abstract class TypeTransformer<SourceClass, TargetClass> {

    protected Type targetType;
    protected FieldValueTransformer fieldValueTransformer;
    protected GenericsAndCastingHelper genericsAndCastingHelper;
    protected ErrorMessageSetup errorMessageSetup;
    protected ArrayList<Class> availableTransformers;

    public abstract TargetClass transform(SourceClass argument);

    public void initialize(FieldValueTransformer fieldValueTransformer, ConfigBuilderFactory configBuilderFactory, Type targetType, ArrayList<Class> availableTransformers) {

        this.targetType = targetType;
        this.fieldValueTransformer = fieldValueTransformer;
        this.genericsAndCastingHelper = configBuilderFactory.getInstance(GenericsAndCastingHelper.class);
        this.errorMessageSetup = configBuilderFactory.getInstance(ErrorMessageSetup.class);
        this.availableTransformers = availableTransformers;
    }

    public boolean isMatching(Class<?> sourceClass, Class<?> targetClass) {
        Class<?> transformerSourceClass = getTransformerSourceClass();
        Class<?> transformerTargetClass = getTransformerTargetClass();
        if(transformerSourceClass.isAssignableFrom(sourceClass) && targetClass.isAssignableFrom(transformerTargetClass)) {
            return true;
        }
        return false;
    }

    protected Class<?> getTransformerSourceClass() {
        Type typeOfInterface = this.getClass().getGenericSuperclass();
        Type[] genericTypes = ((ParameterizedType) typeOfInterface).getActualTypeArguments();
        return genericsAndCastingHelper.castTypeToClass(genericTypes[0]);
    }

    protected Class<?> getTransformerTargetClass() {
        Type typeOfInterface = this.getClass().getGenericSuperclass();
        Type[] genericTypes = ((ParameterizedType) typeOfInterface).getActualTypeArguments();
        return genericsAndCastingHelper.castTypeToClass(genericTypes[1]);
    }


    public void setGenericsAndCastingHelper(GenericsAndCastingHelper genericsAndCastingHelper) {
        this.genericsAndCastingHelper = genericsAndCastingHelper;
    }
}
