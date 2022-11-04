package com.tngtech.configbuilder.annotation.propertyloaderconfiguration;

import com.tngtech.propertyloader.PropertyLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PropertiesFilesProcessorTest {

    @Mock
    private PropertiesFiles propertiesFiles;
    @Mock
    private PropertyLoader propertyLoader;

    private final PropertiesFilesProcessor propertiesFilesProcessor = new PropertiesFilesProcessor();

    @Test
    public void testPropertiesFilesProcessor() {
        String[] fileNames = {"file1", "file2"};
        when(propertiesFiles.value()).thenReturn(fileNames);

        propertiesFilesProcessor.configurePropertyLoader(propertiesFiles, propertyLoader);

        verify(propertyLoader).withBaseNames(newArrayList(fileNames));
    }
}
