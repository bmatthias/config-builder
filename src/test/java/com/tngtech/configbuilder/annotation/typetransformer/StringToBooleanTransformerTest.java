package com.tngtech.configbuilder.annotation.typetransformer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class StringToBooleanTransformerTest {
    private StringToBooleanTransformer transformer;

    @Before
    public void setUp() {
        transformer = new StringToBooleanTransformer();
    }

    @Test
    public void testTransformer() {
        Boolean actualResult = transformer.transform("false");
        assertThat(actualResult, equalTo(false));
    }
}
