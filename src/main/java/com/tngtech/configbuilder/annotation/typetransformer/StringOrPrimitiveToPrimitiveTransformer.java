package com.tngtech.configbuilder.annotation.typetransformer;


import com.tngtech.configbuilder.exception.TypeTransformerException;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;

public class StringOrPrimitiveToPrimitiveTransformer extends ITypeTransformer<Object,Object>{

    @Override
    public Object transform(Object argument) {
        PropertyEditor editor = PropertyEditorManager.findEditor(genericsAndCastingHelper.castTypeToClass(targetType));
        if (editor != null) {
            try {
                editor.setAsText(String.valueOf(argument));
                return editor.getValue();
            }
            catch(IllegalArgumentException e) {
                //TODO: pass a message
                throw new TypeTransformerException();
            }
        } else {
            return argument;
        }
    }

    @Override
    public boolean isMatching(Class<?> sourceClass, Class<?> targetClass) {
        return genericsAndCastingHelper.isPrimitiveOrWrapper(targetClass) && (String.class.equals(sourceClass) || genericsAndCastingHelper.isPrimitiveOrWrapper(sourceClass));
    }
}
