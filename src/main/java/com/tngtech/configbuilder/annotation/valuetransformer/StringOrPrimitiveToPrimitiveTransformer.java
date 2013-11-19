package com.tngtech.configbuilder.annotation.valuetransformer;


import com.tngtech.configbuilder.exception.PrimitiveParsingException;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Type;

public class StringOrPrimitiveToPrimitiveTransformer extends ValueTransformer<Object,Object> {

    @Override
    public Object transform(Object argument) {
        PropertyEditor editor = PropertyEditorManager.findEditor(genericsAndCastingHelper.castTypeToClass(targetType));
        try {
            editor.setAsText(String.valueOf(argument));
            return editor.getValue();
        }
        catch(IllegalArgumentException e) {
            throw new PrimitiveParsingException(errorMessageSetup.getErrorMessage(PrimitiveParsingException.class, String.valueOf(argument), targetType.toString()));
        }
    }

    @Override
    public boolean isMatching(Object sourceValue, Type targetType) {
        if(sourceValue == null) {
            return false;
        }
        Class<?> sourceClass = genericsAndCastingHelper.getWrapperClassIfPrimitive(sourceValue.getClass());
        Class<?> targetClass = genericsAndCastingHelper.getWrapperClassIfPrimitive(genericsAndCastingHelper.castTypeToClass(targetType));
        return !targetClass.equals(sourceClass) && genericsAndCastingHelper.isPrimitiveOrWrapper(targetClass) && (String.class.equals(sourceClass) || genericsAndCastingHelper.isPrimitiveOrWrapper(sourceClass));
    }

    @Override
    public boolean isContentTransformer() {
        return false;
    }
}
