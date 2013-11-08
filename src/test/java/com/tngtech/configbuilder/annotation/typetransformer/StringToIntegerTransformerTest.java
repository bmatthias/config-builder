package com.tngtech.configbuilder.annotation.typetransformer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class StringToIntegerTransformerTest {

    private StringToIntegerTransformer transformer;

    @Before
    public void setUp() {
        transformer = new StringToIntegerTransformer();
    }

    @Test
    public void testTransformer() {
        Integer actualResult = transformer.transform("12");
        assertThat(actualResult, equalTo(12));
    }

    @Test(expected = NumberFormatException.class)
    public void testForExceptionWithMalformedString() {
        transformer.transform("Not a number");
    }
}
