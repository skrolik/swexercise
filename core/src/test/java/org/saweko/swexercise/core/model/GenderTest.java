package org.saweko.swexercise.core.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class GenderTest {

    @Test
    public void testParse_null() {
        Gender g = Gender.parse(null);
        assertNull(g);
    }

    @Test
    public void testParse_validGenders() {
        Gender g = Gender.parse(Gender.MALE.name());
        assertEquals(g, Gender.MALE);

        g = Gender.parse(Gender.FEMALE.name());
        assertEquals(g, Gender.FEMALE);
    }

    @Test
    public void testParse_invalidGender() {
        Gender g = Gender.parse("incorrect");
        assertNull(g);
    }

}