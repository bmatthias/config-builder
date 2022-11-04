package com.tngtech.configbuilder.util;

import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertyLoaderConfigurationAnnotation;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertyLocations;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertyLocationsProcessor;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertySuffixProcessor;
import com.tngtech.configbuilder.annotation.propertyloaderconfiguration.PropertySuffixes;
import com.tngtech.configbuilder.testclasses.TestConfig;
import com.tngtech.propertyloader.PropertyLoader;
import java.lang.annotation.Annotation;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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

    @BeforeEach
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
