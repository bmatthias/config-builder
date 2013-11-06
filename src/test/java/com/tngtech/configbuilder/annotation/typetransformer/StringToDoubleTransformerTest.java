package com.tngtech.configbuilder.annotation.typetransformer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class StringToDoubleTransformerTest {
    private StringToDoubleTransformer transformer;

    @Before
    public void setUp() {
        transformer = new StringToDoubleTransformer();
    }

    @Test
    public void testTransformer() {
        Double actualResult = transformer.transform("18.3");
        assertThat(actualResult, equalTo(18.3));
    }
}
