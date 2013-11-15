package com.tngtech.configbuilder.annotation.typetransformer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class StringToPathTransformerTest {

    private StringToPathTransformer stringToPathTransformer;

    @Before
    public void setUp() throws Exception {
        stringToPathTransformer = new StringToPathTransformer();
    }

    @Test
    public void testTransform() throws Exception {
        assertEquals(Paths.get("/usr"), stringToPathTransformer.transform("/usr"));
    }

    @Test
    public void testIsMatching() throws Exception {

    }
}
