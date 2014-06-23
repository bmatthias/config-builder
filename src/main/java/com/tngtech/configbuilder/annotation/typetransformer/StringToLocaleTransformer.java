package com.tngtech.configbuilder.annotation.typetransformer;

import java.util.Locale;

public class StringToLocaleTransformer extends TypeTransformer<String, Locale> {
    @Override
    public Locale transform(String localeText) {
        return Locale.forLanguageTag(localeText);
    }
}
