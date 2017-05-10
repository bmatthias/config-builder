package com.tngtech.configbuilder.annotation.propertyloaderconfiguration;

import com.tngtech.propertyloader.PropertyLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PropertiesFilesProcessorTest {

    @Mock
    private PropertiesFiles propertiesFiles;
    @Mock
    private PropertyLoader propertyLoader;

    private PropertiesFilesProcessor propertiesFilesProcessor = new PropertiesFilesProcessor();

    @Test
    public void testPropertiesFilesProcessor() {
        String[] fileNames = {"file1", "file2"};
        when(propertiesFiles.value()).thenReturn(fileNames);

        propertiesFilesProcessor.configurePropertyLoader(propertiesFiles, propertyLoader);

        verify(propertyLoader).withBaseNames(newArrayList(fileNames));
    }
}
