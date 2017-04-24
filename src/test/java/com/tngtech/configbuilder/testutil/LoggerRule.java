package com.tngtech.configbuilder.testutil;

import org.apache.log4j.*;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.rules.ExternalResource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class LoggerRule extends ExternalResource {
    private final ByteArrayOutputStream content = new ByteArrayOutputStream();

    @Override
    protected void before() throws Throwable {
        Logger.getRootLogger().removeAllAppenders();
        Logger.getRootLogger().addAppender(new WriterAppender(new SimpleLayout(), content));
    }

    public String getLog() {
        return content.toString();
    }
}
