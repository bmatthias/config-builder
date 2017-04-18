package com.tngtech.configbuilder.util;

import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.*;
import com.tngtech.configbuilder.testclasses.TestConfig;
import com.tngtech.propertyloader.PropertyLoader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.annotation.Annotation;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
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

    private PropertySuffixes propertySuffixes = TestConfig.class.getAnnotation(PropertySuffixes.class);
    private PropertyLocations propertyLocations = TestConfig.class.getAnnotation(PropertyLocations.class);

    private PropertyLoaderConfigurator propertyLoaderConfigurator;

    @Before
    public void setUp() {
        when(configBuilderFactory.getInstance(AnnotationHelper.class)).thenReturn(annotationHelper);

        propertyLoaderConfigurator = new PropertyLoaderConfigurator(configBuilderFactory);

        List<Annotation> annotationList = newArrayList(propertySuffixes, propertyLocations);
        when(annotationHelper.getAnnotationsAnnotatedWith(TestConfig.class.getDeclaredAnnotations(), PropertyLoaderConfigurationAnnotation.class)).thenReturn(annotationList);
        when(configBuilderFactory.getInstance(PropertyLocationsProcessor.class)).thenReturn(propertyLocationsProcessor);
        when(configBuilderFactory.getInstance(PropertySuffixProcessor.class)).thenReturn(propertySuffixProcessor);
        when(configBuilderFactory.createInstance(PropertyLoader.class)).thenReturn(propertyLoader);
        when(propertyLoader.withDefaultConfig()).thenReturn(propertyLoader);
    }

    @Test
    public void testConfigurePropertyLoader() {
        assertThat(propertyLoaderConfigurator.configurePropertyLoader(TestConfig.class)).isSameAs(propertyLoader);
        verify(configBuilderFactory).createInstance(PropertyLoader.class);
        verify(propertyLoader).withDefaultConfig();
        verify(annotationHelper).getAnnotationsAnnotatedWith(TestConfig.class.getDeclaredAnnotations(), PropertyLoaderConfigurationAnnotation.class);
        verify(configBuilderFactory).getInstance(PropertyLocationsProcessor.class);
        verify(configBuilderFactory).getInstance(PropertySuffixProcessor.class);
        verify(propertyLocationsProcessor).configurePropertyLoader(propertyLocations, propertyLoader);
        verify(propertySuffixProcessor).configurePropertyLoader(propertySuffixes, propertyLoader);
    }
}
