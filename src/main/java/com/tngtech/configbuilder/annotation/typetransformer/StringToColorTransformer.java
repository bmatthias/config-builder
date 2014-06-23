package com.tngtech.configbuilder.annotation.typetransformer;

import java.awt.*;

public class StringToColorTransformer extends TypeTransformer<String, Color> {

    @Override
    public Color transform(String colorTextValue) {
        return Color.decode(colorTextValue);
    }
}
