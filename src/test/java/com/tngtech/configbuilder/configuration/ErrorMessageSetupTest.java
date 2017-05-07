package com.tngtech.configbuilder.configuration;

import com.tngtech.propertyloader.PropertyLoader;
import org.apache.commons.cli.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Locale;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ErrorMessageSetupTest {

    @Mock
    private PropertyLoader propertyLoader;

    private ErrorMessageSetup errorMessageSetup = new ErrorMessageSetup();

    @Before
    public void setUp() {
        when(propertyLoader.withExtension("properties")).thenReturn(propertyLoader);
        when(propertyLoader.load("errors")).thenReturn(new Properties());
    }

    @Test
    public void testInitializeDE() {
        Locale.setDefault(Locale.GERMAN);
        errorMessageSetup.initialize("errors", propertyLoader);
        assertThat(errorMessageSetup.getErrorMessage(ParseException.class)).isEqualTo("Command Line Argumente konnten nicht verarbeitet werden.");
    }

    @Test
    public void testInitializeEN() {
        Locale.setDefault(Locale.ENGLISH);
        errorMessageSetup.initialize("errors", propertyLoader);
        assertThat(errorMessageSetup.getErrorMessage(ParseException.class)).isEqualTo("unable to parse command line arguments");
    }

    @Test
    public void testInitializeOther() {
        Locale.setDefault(Locale.ITALIAN);
        errorMessageSetup.initialize("errors", propertyLoader);
        assertThat(errorMessageSetup.getErrorMessage(ParseException.class)).isEqualTo("unable to parse command line arguments");
    }

    @Test
    public void testGetErrorMessageForExceptionInstance() {
        Locale.setDefault(Locale.ENGLISH);
        errorMessageSetup.initialize(null, propertyLoader);
        ParseException parseException = new ParseException("message");
        assertThat(errorMessageSetup.getErrorMessage(parseException)).isEqualTo("unable to parse command line arguments");
    }

    @Test
    public void testGetErrorMessageForUnknownException() {
        Locale.setDefault(Locale.ENGLISH);
        errorMessageSetup.initialize(null, propertyLoader);
        RuntimeException runtimeException = new RuntimeException();
        assertThat(errorMessageSetup.getErrorMessage(runtimeException)).isEqualTo("java.lang.RuntimeException was thrown");
        assertThat(errorMessageSetup.getErrorMessage(RuntimeException.class)).isEqualTo("java.lang.RuntimeException was thrown");
    }
}
