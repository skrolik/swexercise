package org.saweko.swexercise.core.model;

import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SingleRateModelTest {
    @Rule
    public final AemContext context = new AemContext(ResourceResolverType.JCR_MOCK);

    @Before
    public void setUp() {
        context.addModelsForClasses(SingleRateModel.class);
        context.load(true).json("/single-rate-tests.json", "/path");
    }

    @Test
    public void testModel_goodLoadFromResourceAndMatchers() {
        Resource r = context.resourceResolver().getResource(elementPath("good-rate"));
        SingleRateModel srm = r.adaptTo(SingleRateModel.class);

        Assert.assertEquals(srm.getRate(), new Double(125));

        Assert.assertEquals(srm.getAgeRangeStart(), new Integer(20));
        Assert.assertEquals(srm.getAgeRangeEnd(), new Integer(30));

        Assert.assertEquals(srm.getZipCodeRangeStart(), new Integer(90000));
        Assert.assertEquals(srm.getZipCodeRangeEnd(), new Integer(91000));
        // values parsed correctly

        // now lets test values matchers
        Assert.assertFalse(srm.ageMatch(19));
        Assert.assertTrue(srm.ageMatch(20));
        Assert.assertTrue(srm.ageMatch(29));
        Assert.assertFalse(srm.ageMatch(30)); // expected end range not inclusive


        Assert.assertFalse(srm.zipCodeMatch(899999));
        Assert.assertTrue(srm.zipCodeMatch(90000));
        Assert.assertTrue(srm.zipCodeMatch(90999));
        Assert.assertFalse(srm.zipCodeMatch(91000));
    }

    @Test
    public void testModel_goodLoadFromRequestAndMatchers() {
        context.currentResource(elementPath("good-rate"));
        SingleRateModel srm = context.request().adaptTo(SingleRateModel.class);

        Assert.assertEquals(srm.getRate(), new Double(125));

        Assert.assertEquals(srm.getAgeRangeStart(), new Integer(20));
        Assert.assertEquals(srm.getAgeRangeEnd(), new Integer(30));

        Assert.assertEquals(srm.getZipCodeRangeStart(), new Integer(90000));
        Assert.assertEquals(srm.getZipCodeRangeEnd(), new Integer(91000));
        // values parsed correctly

        // now lets test values matchers
        Assert.assertFalse(srm.ageMatch(19));
        Assert.assertTrue(srm.ageMatch(20));
        Assert.assertTrue(srm.ageMatch(29));
        Assert.assertFalse(srm.ageMatch(30)); // expected end range not inclusive


        Assert.assertFalse(srm.zipCodeMatch(899999));
        Assert.assertTrue(srm.zipCodeMatch(90000));
        Assert.assertTrue(srm.zipCodeMatch(90999));
        Assert.assertFalse(srm.zipCodeMatch(91000));
    }


    @Test
    public void testMissingValues() {
        Resource r = context.resourceResolver().getResource(elementPath("missing-values"));
        SingleRateModel srm = r.adaptTo(SingleRateModel.class);
        Assert.assertNotNull(srm);

        Assert.assertTrue(srm.anyNull());
        Assert.assertNull(srm.getRate());
        Assert.assertNull(srm.getAgeRangeEnd());
        Assert.assertNull(srm.getAgeRangeStart());
        Assert.assertNull(srm.getZipCodeRangeEnd());
        Assert.assertNull(srm.getZipCodeRangeStart());

        Assert.assertFalse(srm.ageMatch(123));
        Assert.assertFalse(srm.zipCodeMatch(123));

        // no exception thrown, safe from empty values
    }

    @Test
    public void testIncorrectRange() {
        Resource r = context.resourceResolver().getResource(elementPath("bad-range"));
        SingleRateModel srm = r.adaptTo(SingleRateModel.class);
        // should not adapt, initialize method thrown exception
        Assert.assertNull(srm);
    }

    @Test
    public void testBadSeparator() {
        Resource r = context.resourceResolver().getResource(elementPath("bad-separator"));
        SingleRateModel srm = r.adaptTo(SingleRateModel.class);
        // should not adapt, initialize method thrown exception
        Assert.assertNull(srm);
    }

    private String elementPath(final String name) {
        return "/path/rates/"+name;
    }
}