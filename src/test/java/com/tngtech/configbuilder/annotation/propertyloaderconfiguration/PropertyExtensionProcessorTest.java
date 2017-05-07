package com.tngtech.configbuilder.annotation.propertyloaderconfiguration;

import com.tngtech.propertyloader.PropertyLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PropertyExtensionProcessorTest {

    private PropertyExtensionProcessor propertyExtensionProcessor = new PropertyExtensionProcessor();

    @Mock
    private PropertyExtension propertyExtension;
    @Mock
    private PropertyLoader propertyLoader;

    @Test
    public void testPropertyExtensionProcessor() {
        when(propertyExtension.value()).thenReturn("extension");

        propertyExtensionProcessor.configurePropertyLoader(propertyExtension, propertyLoader);

        verify(propertyLoader).withExtension("extension");
    }
}
