package com.tngtech.configbuilder.annotation.typetransformer;


import com.tngtech.configbuilder.exception.TypeTransformerException;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;

public class StringToPrimitiveTransformer extends ITypeTransformer<String,Object>{

    Class<?> primitiveType;

    public StringToPrimitiveTransformer(Class primitiveType) {
        this.primitiveType = primitiveType;
    }

    @Override
    public Object transform(String argument) {
        PropertyEditor editor = PropertyEditorManager.findEditor(primitiveType);
        if (editor != null) {
            try {
                editor.setAsText(argument);
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
