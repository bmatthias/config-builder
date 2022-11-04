package com.tngtech.configbuilder.annotation.propertyloaderconfiguration;

import com.tngtech.propertyloader.PropertyLoader;
import com.tngtech.propertyloader.impl.DefaultPropertySuffixContainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PropertySuffixProcessorTest {

    private PropertySuffixProcessor propertySuffixProcessor = new PropertySuffixProcessor();

    @Mock
    private PropertySuffixes propertySuffixes;
    @Mock
    private DefaultPropertySuffixContainer propertySuffix;
    @Mock
    private PropertyLoader propertyLoader;

    @Test
    public void testPropertySuffixProcessor() {
        String[] suffixes = {"suffix1", "suffix2"};
        when(propertyLoader.getSuffixes()).thenReturn(propertySuffix);
        when(propertySuffixes.extraSuffixes()).thenReturn(suffixes);
        when(propertySuffixes.hostNames()).thenReturn(false);

        propertySuffixProcessor.configurePropertyLoader(propertySuffixes, propertyLoader);

        verify(propertySuffix).clear();
        verify(propertySuffix).addString("suffix1");
        verify(propertySuffix).addString("suffix2");
    }

    @Test
    public void testPropertySuffixProcessorWithHostNames() {
        when(propertyLoader.getSuffixes()).thenReturn(propertySuffix);
        when(propertySuffixes.extraSuffixes()).thenReturn(new String[]{});
        when(propertySuffixes.hostNames()).thenReturn(true);

        propertySuffixProcessor.configurePropertyLoader(propertySuffixes, propertyLoader);

        verify(propertySuffix).clear();
        verify(propertySuffix).addLocalHostNames();
    }
}
