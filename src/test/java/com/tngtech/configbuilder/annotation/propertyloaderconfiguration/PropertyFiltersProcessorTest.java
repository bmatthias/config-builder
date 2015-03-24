package com.tngtech.configbuilder.annotation.propertyloaderconfiguration;

import com.tngtech.propertyloader.PropertyLoader;
import com.tngtech.propertyloader.impl.DefaultPropertyFilterContainer;
import com.tngtech.propertyloader.impl.filters.*;
import com.tngtech.propertyloader.impl.interfaces.PropertyLoaderFilter;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Properties;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PropertyFiltersProcessorTest extends TestCase {
  
  private static class TestPropertyFilter extends ValueModifyingFilter {

    @Override
    protected String filterValue( String key, String value, Properties properties ) {
      return null;
    }
  }
  
  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  
  @Mock
  private PropertyFilters propertyFilters;
  
  @Mock
  private DefaultPropertyFilterContainer filterContainer;

  @Mock
  private PropertyLoader propertyLoader;

  private PropertyFiltersProcessor propertyFiltersProcessor;
  
  @Before
  public void setUp()
  {
    propertyFiltersProcessor = new PropertyFiltersProcessor();
    
    when(propertyLoader.getFilters()).thenReturn(filterContainer);
  }
  
  @Test
  public void testAnnotationWithNoValues() {
    @SuppressWarnings("unchecked") Class<? extends PropertyLoaderFilter>[] classes = new Class[0];
    when(propertyFilters.value()).thenReturn(classes);
    
    propertyFiltersProcessor.configurePropertyLoader( propertyFilters, propertyLoader );
    
    verify(filterContainer).clear();
  }

  @Test
  public void testAnnotationWithSomeValues() {
    @SuppressWarnings("unchecked") Class<? extends PropertyLoaderFilter>[] classes = new Class[]{
            VariableResolvingFilter.class,
            DecryptingFilter.class,
            EnvironmentResolvingFilter.class,
            WarnOnSurroundingWhitespace.class,
            ThrowIfPropertyHasToBeDefined.class
    };
    when(propertyFilters.value()).thenReturn(classes);

    propertyFiltersProcessor.configurePropertyLoader( propertyFilters, propertyLoader );

    InOrder order = inOrder(filterContainer);
    order.verify(filterContainer).clear();
    order.verify(filterContainer).withVariableResolvingFilter();
    order.verify(filterContainer).withDecryptingFilter();
    order.verify(filterContainer).withEnvironmentResolvingFilter();
    order.verify(filterContainer).withWarnOnSurroundingWhitespace();
    order.verify(filterContainer).withWarnIfPropertyHasToBeDefined();
    order.verifyNoMoreInteractions();
  }
  
  @Test
  public void testAnnotationWithUnknownValuesShouldThrowException() {
    @SuppressWarnings("unchecked") Class<? extends PropertyLoaderFilter>[] classes = new Class[]{TestPropertyFilter.class};
    when(propertyFilters.value()).thenReturn(classes);

    expectedException.expect( IllegalStateException.class );
    expectedException.expectMessage( "unhandled filter class TestPropertyFilter" );
    propertyFiltersProcessor.configurePropertyLoader( propertyFilters, propertyLoader );

  }
}
