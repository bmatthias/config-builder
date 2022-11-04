package com.tngtech.configbuilder.annotation.propertyloaderconfiguration;

import com.tngtech.propertyloader.PropertyLoader;
import com.tngtech.propertyloader.impl.DefaultPropertyLocationContainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PropertyLocationsProcessorTest {

    private PropertyLocationsProcessor propertyLocationsProcessor = new PropertyLocationsProcessor();

    @Mock
    private PropertyLocations propertyLocations;
    @Mock
    private DefaultPropertyLocationContainer propertyLocation;
    @Mock
    private PropertyLoader propertyLoader;

    @Test
    public void testPropertyLocationsProcessor() {
        String[] dirs = {"dir1", "dir2"};
        Class[] classes = {this.getClass(), Object.class};
        when(propertyLoader.getLocations()).thenReturn(propertyLocation);
        when(propertyLocations.directories()).thenReturn(dirs);
        when(propertyLocations.resourcesForClasses()).thenReturn(classes);
        when(propertyLocations.fromClassLoader()).thenReturn(false);

        propertyLocationsProcessor.configurePropertyLoader(propertyLocations, propertyLoader);

        verify(propertyLocation).clear();
        verify(propertyLoader).atDirectory("dir1");
        verify(propertyLoader).atDirectory("dir2");
        verify(propertyLoader).atRelativeToClass(this.getClass());
        verify(propertyLoader).atRelativeToClass(Object.class);
    }

    @Test
    public void testPropertyLocationsProcessorWithClassLoader() {
        when(propertyLoader.getLocations()).thenReturn(propertyLocation);
        when(propertyLocations.directories()).thenReturn(new String[]{});
        when(propertyLocations.resourcesForClasses()).thenReturn(new Class[]{});
        when(propertyLocations.fromClassLoader()).thenReturn(true);

        propertyLocationsProcessor.configurePropertyLoader(propertyLocations, propertyLoader);

        verify(propertyLocation).clear();
        verify(propertyLoader).atContextClassPath();
    }
}
