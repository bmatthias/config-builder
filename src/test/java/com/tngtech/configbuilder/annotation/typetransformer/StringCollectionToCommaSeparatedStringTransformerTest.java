package com.tngtech.configbuilder.annotation.typetransformer;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class StringCollectionToCommaSeparatedStringTransformerTest {
    private StringCollectionToCommaSeparatedStringTransformer transformer;

    @Before
    public void setUp() {
        transformer = new StringCollectionToCommaSeparatedStringTransformer();
    }

    @Test
    public void testTransformer() {
        Collection<String> collection = Lists.newArrayList("Rakim","Lakim Shabazz","2Pac");
        String actualResult = transformer.transform(collection);
        assertThat(actualResult, equalTo("Rakim,Lakim Shabazz,2Pac"));
    }
}
