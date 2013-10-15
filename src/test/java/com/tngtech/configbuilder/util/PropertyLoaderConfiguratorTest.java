package com.tngtech.configbuilder.util;

import com.google.common.collect.Lists;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.*;
import com.tngtech.configbuilder.context.ConfigBuilderFactory;
import com.tngtech.configbuilder.testclasses.TestConfig;
import com.tngtech.propertyloader.PropertyLoader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.annotation.Annotation;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PropertyLoaderConfiguratorTest {

    @Mock
    private ConfigBuilderFactory configBuilderFactory;
    @Mock
    private AnnotationHelper annotationHelper;
    @Mock
    private PropertySuffixProcessor propertySuffixProcessor;
    @Mock
    private PropertyLocationsProcessor propertyLocationsProcessor;
    @Mock
    private PropertyLoader propertyLoader;

    PropertySuffixes propertySuffixes = TestConfig.class.getAnnotation(PropertySuffixes.class);
    PropertyLocations propertyLocations = TestConfig.class.getAnnotation(PropertyLocations.class);

    private PropertyLoaderConfigurator propertyLoaderConfigurator;

    @Before
    public void setUp() throws Exception {
        propertyLoaderConfigurator = new PropertyLoaderConfigurator(annotationHelper, configBuilderFactory);

        List<Annotation> annotationList = Lists.newArrayList(propertySuffixes, propertyLocations);
        when(annotationHelper.getAnnotationsAnnotatedWith(TestConfig.class.getDeclaredAnnotations(), PropertyLoaderConfigurationAnnotation.class)).thenReturn(annotationList);
        when(configBuilderFactory.getInstance(PropertyLocationsProcessor.class)).thenReturn(propertyLocationsProcessor);
        when(configBuilderFactory.getInstance(PropertySuffixProcessor.class)).thenReturn(propertySuffixProcessor);
        when(configBuilderFactory.createInstance(PropertyLoader.class)).thenReturn(propertyLoader);
        when(propertyLoader.withDefaultConfig()).thenReturn(propertyLoader);
    }

    @Test
    public void testConfigurePropertyLoader() throws Exception {
        assertEquals(propertyLoader, propertyLoaderConfigurator.configurePropertyLoader(TestConfig.class));
        verify(configBuilderFactory).createInstance(PropertyLoader.class);
        verify(propertyLoader).withDefaultConfig();
        verify(annotationHelper).getAnnotationsAnnotatedWith(TestConfig.class.getDeclaredAnnotations(), PropertyLoaderConfigurationAnnotation.class);
        verify(configBuilderFactory).getInstance(PropertyLocationsProcessor.class);
        verify(configBuilderFactory).getInstance(PropertySuffixProcessor.class);
        verify(propertyLocationsProcessor).configurePropertyLoader(propertyLocations, propertyLoader);
        verify(propertySuffixProcessor).configurePropertyLoader(propertySuffixes, propertyLoader);
    }
}
