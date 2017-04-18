package com.tngtech.configbuilder.testutil;

import org.junit.rules.ExternalResource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class SystemOutRule extends ExternalResource {
    private final ByteArrayOutputStream content = new ByteArrayOutputStream();
    private PrintStream originalOutStream;

    @Override
    protected void before() throws Throwable {
        originalOutStream = new PrintStream(System.out);
        System.setOut(new PrintStream(content));
    }

    @Override
    protected void after() {
        System.setOut(originalOutStream);
    }

    public String getLog() {
        return content.toString();
    }
}
