package com.tngtech.configbuilder.util;

import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.exception.FactoryInstantiationException;
import com.tngtech.propertyloader.PropertyLoader;
import org.apache.commons.cli.GnuParser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import java.util.List;

import static org.junit.Assert.assertEquals;


public class ConfigBuilderFactoryTest {

    public class InnerClass {
        public class InnerInnerClass{}
    }
    public static class NestedClass {}
    public static class ClassWithoutDefaultConstructor {
        private ClassWithoutDefaultConstructor(){}
    }

    private ConfigBuilderFactory configBuilderFactory;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        configBuilderFactory = new ConfigBuilderFactory();
        configBuilderFactory.initialize();
    }

    @Test
    public void testInitialize() throws Exception {

    }

    @Test
    public void testGetInstance() throws Exception {
        assertEquals(ErrorMessageSetup.class, configBuilderFactory.getInstance(ErrorMessageSetup.class).getClass());
        assertEquals(Validation.buildDefaultValidatorFactory().getClass(), configBuilderFactory.getInstance(ValidatorFactory.class).getClass());

        //test instantiation of arbitrary class
        assertEquals(GnuParser.class, configBuilderFactory.getInstance(GnuParser.class).getClass());

        //test instantiation of nested and inner classes
        assertEquals(InnerClass.class, configBuilderFactory.getInstance(InnerClass.class).getClass());
        assertEquals(NestedClass.class, configBuilderFactory.getInstance(NestedClass.class).getClass());
    }

    @Test(expected = FactoryInstantiationException.class)
    public void testGetInstanceThrowsExceptionForInnerClass() throws Exception {
        configBuilderFactory.getInstance(ErrorMessageSetup.class).initialize("errors", new PropertyLoader());
        configBuilderFactory.getInstance(InnerClass.InnerInnerClass.class);
    }

    @Test(expected = FactoryInstantiationException.class)
    public void testGetInstanceThrowsExceptionForClassWithoutDefaultConstructor() throws Exception {
        configBuilderFactory.getInstance(ErrorMessageSetup.class).initialize("errors", new PropertyLoader());
        configBuilderFactory.getInstance(ClassWithoutDefaultConstructor.class);
    }

    @Test
    public void testCreateInstance() throws Exception {
        assertEquals(GnuParser.class, configBuilderFactory.createInstance(GnuParser.class).getClass());
    }

    @Test
    public void testCreateInstanceThrowsException() throws Exception {
        expectedException.expect(RuntimeException.class);
        configBuilderFactory.createInstance(List.class);
    }
}
