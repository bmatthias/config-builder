package com.tngtech.configbuilder.annotation.typetransformer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Set;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class CharacterSeparatedStringToStringSetTransformerTest {
    private CharacterSeparatedStringToStringSetTransformer transformer;

    @Before
    public void setUp() {
        transformer = new CharacterSeparatedStringToStringSetTransformer();
    }

    @Test
    public void testTransformer() {
        Set<String> expectedResult = Sets.newHashSet("Wayne", "André", "Kanye", "Lebron");
        
        Set<String> actualResult = transformer.transform("Wayne,André,Kanye,Lebron");
        assertThat(actualResult,equalTo(expectedResult));
    }

    @Test
    public void testIsMatching() throws Exception {

    }
}
