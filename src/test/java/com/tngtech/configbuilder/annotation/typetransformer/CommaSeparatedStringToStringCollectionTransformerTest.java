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
public class CommaSeparatedStringToStringCollectionTransformerTest {
    private CommaSeparatedStringToStringCollectionTransformer transformer;

    @Before
    public void setUp() {
        transformer = new CommaSeparatedStringToStringCollectionTransformer();
    }

    @Test
    public void testTransformer() {
        Collection<String> expectedResult = Lists.newArrayList("12","Game","of","Love");
        
        Collection<String> actualResult = transformer.transform("12,Game,of,Love");
        assertThat(actualResult,equalTo(expectedResult));
    }
}
