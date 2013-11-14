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
public abstract class ITypeTransformer<SourceClass, TargetClass> {

    protected Type targetType;
    protected FieldValueTransformer fieldValueTransformer;
    protected GenericsAndCastingHelper genericsAndCastingHelper;
    protected ErrorMessageSetup errorMessageSetup;
    protected ArrayList<Class> availableTransformers;

    public abstract TargetClass transform(SourceClass argument);

    public void initialize(FieldValueTransformer fieldValueTransformer, ConfigBuilderFactory configBuilderFactory, Type targetType, ArrayList<Class> availableTransformers) {
        this.availableTransformers = availableTransformers;
        this.targetType = targetType;
        this.errorMessageSetup = configBuilderFactory.getInstance(ErrorMessageSetup.class);
        this.genericsAndCastingHelper = configBuilderFactory.getInstance(GenericsAndCastingHelper.class);
        this.fieldValueTransformer = fieldValueTransformer;
    }

    public boolean isMatching(Class<?> sourceClass, Class<?> targetClass) {
        Type typeOfInterface = this.getClass().getGenericSuperclass();
        Type[] genericTypes = ((ParameterizedType) typeOfInterface).getActualTypeArguments();
        Class<?> transformerSourceClass = genericsAndCastingHelper.castTypeToClass(genericTypes[0]);
        Class<?> transformerTargetClass = genericsAndCastingHelper.castTypeToClass(genericTypes[1]);
        if(transformerSourceClass.isAssignableFrom(sourceClass) && targetClass.isAssignableFrom(transformerTargetClass)) {
            return true;
        }
        return false;
    }


    public void setGenericsAndCastingHelper(GenericsAndCastingHelper genericsAndCastingHelper) {
        this.genericsAndCastingHelper = genericsAndCastingHelper;
    }
}
