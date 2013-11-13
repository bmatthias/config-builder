package com.tngtech.configbuilder.annotation.typetransformer;

import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.util.ClassCastingHelper;
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

    private Type targetType;
    private FieldValueTransformer fieldValueTransformer;
    private ClassCastingHelper classCastingHelper;
    private ErrorMessageSetup errorMessageSetup;
    private ArrayList<Class> availableTransformers;

    public abstract TargetClass transform(SourceClass argument);

    public boolean isMatching(Class<?> sourceClass, Class<?> targetClass) {
        Type typeOfInterface = this.getClass().getGenericSuperclass();
        Type[] genericTypes = ((ParameterizedType) typeOfInterface).getActualTypeArguments();
        Class<?> transformerSourceClass = classCastingHelper.castTypeToClass(genericTypes[0]);
        Class<?> transformerTargetClass = classCastingHelper.castTypeToClass(genericTypes[1]);
        if(transformerSourceClass.isAssignableFrom(sourceClass) && targetClass.isAssignableFrom(transformerTargetClass)) {
            return true;
        }
        return false;
    }

    public Type getTargetType() {
        return targetType;
    }

    public void setTargetType(Type targetType) {
        this.targetType = targetType;
    }

    public ClassCastingHelper getClassCastingHelper() {
        return classCastingHelper;
    }

    public void setClassCastingHelper(ClassCastingHelper classCastingHelper) {
        this.classCastingHelper = classCastingHelper;
    }

    public ErrorMessageSetup getErrorMessageSetup() {
        return errorMessageSetup;
    }

    public void setErrorMessageSetup(ErrorMessageSetup errorMessageSetup) {
        this.errorMessageSetup = errorMessageSetup;
    }

    public FieldValueTransformer getFieldValueTransformer() {
        return fieldValueTransformer;
    }

    public void setFieldValueTransformer(FieldValueTransformer fieldValueTransformer) {
        this.fieldValueTransformer = fieldValueTransformer;
    }

    public ArrayList<Class> getAvailableTransformers() {
        return availableTransformers;
    }

    public void setAvailableTransformers(ArrayList<Class> availableTransformers) {
        this.availableTransformers = availableTransformers;
    }
}
