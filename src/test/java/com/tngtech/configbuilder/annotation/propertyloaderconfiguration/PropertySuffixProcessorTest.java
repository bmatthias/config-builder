package com.tngtech.configbuilder.annotation.propertyloaderconfiguration;

import com.tngtech.propertyloader.PropertyLoader;
import com.tngtech.propertyloader.impl.DefaultPropertySuffixContainer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
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
