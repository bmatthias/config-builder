package com.tngtech.configbuilder.util;

import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
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

    public class InnerClass {}
    public static class NestedClass {}

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
