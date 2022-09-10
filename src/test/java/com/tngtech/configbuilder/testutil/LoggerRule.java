package com.tngtech.configbuilder.testutil;

import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.junit.rules.ExternalResource;

import java.io.ByteArrayOutputStream;

public class LoggerRule extends ExternalResource {
    private final ByteArrayOutputStream content = new ByteArrayOutputStream();

    @Override
    protected void before() {
        Logger.getRootLogger().removeAllAppenders();
        Logger.getRootLogger().addAppender(new WriterAppender(new SimpleLayout(), content));
    }

    public String getLog() {
        return content.toString();
    }
}
