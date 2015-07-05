package com.tngtech.configbuilder.annotation.typetransformer;


import com.tngtech.configbuilder.exception.PrimitiveParsingException;
import com.tngtech.configbuilder.exception.TypeTransformerException;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Type;

public class StringOrPrimitiveToPrimitiveTransformer extends TypeTransformer<Object,Object> {

    @Override
    public Object transform(Object argument) {
        PropertyEditor editor = PropertyEditorManager.findEditor(genericsAndCastingHelper.castTypeToClass(targetType));
        try {
            editor.setAsText(String.valueOf(argument).trim());
            return editor.getValue();
        }
        catch(IllegalArgumentException e) {
            throw new PrimitiveParsingException(errorMessageSetup.getErrorMessage(PrimitiveParsingException.class, String.valueOf(argument), targetType.toString()));
        }
    }

    @Override
    public boolean isMatching(Class<?> sourceClass, Class<?> targetClass) {
        return !targetClass.equals(sourceClass) && genericsAndCastingHelper.isPrimitiveOrWrapper(targetClass) && (String.class.equals(sourceClass) || genericsAndCastingHelper.isPrimitiveOrWrapper(sourceClass));
    }
}
