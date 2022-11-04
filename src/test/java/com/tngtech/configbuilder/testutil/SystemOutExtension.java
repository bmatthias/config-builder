package com.tngtech.configbuilder.testutil;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class SystemOutExtension implements BeforeEachCallback, AfterEachCallback {

    private final ByteArrayOutputStream content = new ByteArrayOutputStream();
    private PrintStream originalOutStream;

    @Override
    public void beforeEach(ExtensionContext context) {
        originalOutStream = new PrintStream(System.out);
        System.setOut(new PrintStream(content));
    }

    @Override
    public void afterEach(ExtensionContext context) {
        System.setOut(originalOutStream);
    }

    public String getLog() {
        return content.toString();
    }
}
