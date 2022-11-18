package com.tngtech.configbuilder.testutil;

import java.io.ByteArrayOutputStream;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class LoggerExtension implements BeforeEachCallback {

    private final ByteArrayOutputStream content = new ByteArrayOutputStream();

    @Override
    public void beforeEach(ExtensionContext context) {
        Logger.getRootLogger().removeAllAppenders();
        Logger.getRootLogger().addAppender(new WriterAppender(new SimpleLayout(), content));
    }

    public String getLog() {
        return content.toString();
    }

}
