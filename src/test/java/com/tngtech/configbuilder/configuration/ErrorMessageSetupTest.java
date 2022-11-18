package com.tngtech.configbuilder.configuration;

import com.tngtech.propertyloader.PropertyLoader;
import java.util.Locale;
import java.util.Properties;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ErrorMessageSetupTest {

    @Mock
    private PropertyLoader propertyLoader;

    private final ErrorMessageSetup errorMessageSetup = new ErrorMessageSetup();

    @Test
    public void testInitializeDE() {
        when(propertyLoader.load("errors")).thenReturn(new Properties());
        Locale.setDefault(Locale.GERMAN);
        errorMessageSetup.initialize("errors", propertyLoader);
        assertThat(errorMessageSetup.getErrorMessage(ParseException.class)).isEqualTo("Command Line Argumente konnten nicht verarbeitet werden.");
    }

    @Test
    public void testInitializeEN() {
        when(propertyLoader.load("errors")).thenReturn(new Properties());
        Locale.setDefault(Locale.ENGLISH);
        errorMessageSetup.initialize("errors", propertyLoader);
        assertThat(errorMessageSetup.getErrorMessage(ParseException.class)).isEqualTo("unable to parse command line arguments");
    }

    @Test
    public void testInitializeOther() {
        when(propertyLoader.load("errors")).thenReturn(new Properties());
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
