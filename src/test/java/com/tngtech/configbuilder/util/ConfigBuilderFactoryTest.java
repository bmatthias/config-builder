package com.tngtech.configbuilder.util;

import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.util.ConfigBuilderFactory;
import org.apache.commons.cli.GnuParser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.validation.Validation;

import java.util.List;

import static org.junit.Assert.assertEquals;


public class ConfigBuilderFactoryTest {

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
        assertEquals(null, configBuilderFactory.getInstance(GnuParser.class));
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

    @Test
    public void testGetValidatorFactory() throws Exception {
        assertEquals(Validation.buildDefaultValidatorFactory().getClass(), configBuilderFactory.getValidatorFactory().getClass());
    }
}
