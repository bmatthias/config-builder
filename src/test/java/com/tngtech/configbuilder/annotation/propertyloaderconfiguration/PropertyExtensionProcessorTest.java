package com.tngtech.configbuilder.annotation.propertyloaderconfiguration;

import com.tngtech.propertyloader.PropertyLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PropertyExtensionProcessorTest {

    private final PropertyExtensionProcessor propertyExtensionProcessor = new PropertyExtensionProcessor();

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
