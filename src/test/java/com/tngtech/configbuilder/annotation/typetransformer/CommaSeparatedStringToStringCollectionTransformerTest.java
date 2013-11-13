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
        Collection<String> expectedResult = Lists.newArrayList("Wayne","André","Kanye","Lebron");
        
        Collection<String> actualResult = transformer.transform("Wayne,André,Kanye,Lebron");
        assertThat(actualResult,equalTo(expectedResult));
    }

    @Test
    public void testIsMatching() throws Exception {

    }
}
