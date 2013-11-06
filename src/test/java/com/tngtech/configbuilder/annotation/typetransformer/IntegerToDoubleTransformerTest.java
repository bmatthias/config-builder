package com.tngtech.configbuilder.annotation.typetransformer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class IntegerToDoubleTransformerTest {
    private IntegerToDoubleTransformer transformer;

    @Before
    public void setUp() {
        transformer = new IntegerToDoubleTransformer();
    }

    @Test
    public void testTransformer() {
        Double actualResult = transformer.transform(92);
        assertThat(actualResult, equalTo(92.0));
    }
}
