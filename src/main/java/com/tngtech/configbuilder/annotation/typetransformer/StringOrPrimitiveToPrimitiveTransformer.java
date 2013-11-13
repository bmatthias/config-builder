package com.tngtech.configbuilder.annotation.typetransformer;


import com.tngtech.configbuilder.exception.TypeTransformerException;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;

public class StringOrPrimitiveToPrimitiveTransformer extends ITypeTransformer<Object,Object>{

    Class<?> primitiveType;

    public StringOrPrimitiveToPrimitiveTransformer(Class primitiveType) {
        this.primitiveType = primitiveType;
    }

    @Override
    public Object transform(Object argument) {
        PropertyEditor editor = PropertyEditorManager.findEditor(primitiveType);
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
}
