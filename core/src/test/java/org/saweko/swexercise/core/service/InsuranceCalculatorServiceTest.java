package org.saweko.swexercise.core.service;

import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.saweko.swexercise.core.model.Gender;
import org.saweko.swexercise.core.model.InsuranceRatesModel;
import org.saweko.swexercise.core.model.SingleRateModel;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class InsuranceCalculatorServiceTest {

    @Rule
    public final AemContext context = new AemContext(ResourceResolverType.JCR_MOCK);

    private InsuranceCalculatorService service;

    @Before
    public void setUp() {
        context.addModelsForClasses(SingleRateModel.class);
        context.addModelsForClasses(InsuranceRatesModel.class);
        context.load(true).json("/all-rates.json", "/rates");
        context.registerInjectActivateService(service = new InsuranceCalculatorService());
    }


    @Test(expected = NullPointerException.class) // thrown by validate
    public void testCalculateRate_nullArgument() throws InsuranceCalculatorException {
        service.calculateRate(context.resourceResolver(), null, 12, 321, Gender.MALE);
    }

    @Test(expected = IllegalArgumentException.class) // thrown by validate
    public void testCalculateRate_blankConfiguration() throws InsuranceCalculatorException {
        service.calculateRate(context.resourceResolver(), "   ", 12, 321, Gender.MALE);
    }

    @Test(expected = InsuranceCalculatorException.class)
    public void testCalculateRate_missingConfigResource() throws InsuranceCalculatorException {
        service.calculateRate(context.resourceResolver(), "/non/existing/resource", 12, 321, Gender.MALE);
    }

    @Test
    public void testCalculateRate_resolvedRates() throws InsuranceCalculatorException {
        Double result = service.calculateRate(context.resourceResolver(), "/rates", 12, 90000, Gender.MALE);
        Assert.assertEquals(new Double(100), result);

        result = service.calculateRate(context.resourceResolver(), "/rates", 20, 90000, Gender.MALE);
        Assert.assertEquals(new Double(120), result);

        result = service.calculateRate(context.resourceResolver(), "/rates", 12, 90000, Gender.FEMALE);
        Assert.assertEquals(new Double(90), result);

        result = service.calculateRate(context.resourceResolver(), "/rates", 19, 91000, Gender.FEMALE);
        Assert.assertEquals(new Double(99), result);
    }

}