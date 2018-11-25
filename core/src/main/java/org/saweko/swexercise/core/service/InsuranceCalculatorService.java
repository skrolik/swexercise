package org.saweko.swexercise.core.service;

import org.apache.commons.lang3.Validate;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.saweko.swexercise.core.model.Gender;
import org.saweko.swexercise.core.model.InsuranceRatesModel;
import org.saweko.swexercise.core.model.SingleRateModel;
import org.saweko.swexercise.core.util.LambdaValueHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component(service = InsuranceCalculatorService.class)
public class InsuranceCalculatorService {
    private static final Logger LOG = LoggerFactory.getLogger(InsuranceCalculatorService.class);

    @Reference
    private ResourceResolver resourceResolver;

    public Double calculateRate(final String configurationPath, final Integer age, final Integer zipCode, final Gender gender)
            throws InsuranceCalculatorException {
        Validate.noNullElements(new Object[]{age, zipCode, gender}, "Arguments cannot be null!");
        Validate.notBlank(configurationPath);
        final Resource configurationResource = resourceResolver.getResource(configurationPath);

        if (configurationResource == null) {
            LOG.error("Cannot access configuration resource under path: {}", configurationPath);
            throw new InsuranceCalculatorException("Unable to access configuration resource under path: " + configurationPath);
        }

        final InsuranceRatesModel ratesModel = configurationResource.adaptTo(InsuranceRatesModel.class);

        Validate.notNull(ratesModel, "Cannot adapt path {} to InsuranceRatesModel, null returned", configurationPath);

        final List<SingleRateModel> ratesForGender = gender == Gender.MALE ? ratesModel.getMaleModel() : ratesModel.getFemaleModel();

        if (ratesForGender == null || ratesForGender.size() == 0) {
            throw new InsuranceCalculatorException("Unable to calculate rate for configuration path: "
                    + configurationPath + " and gender " + gender + ", supplied configuration is missing rates for gender");
        }

        final LambdaValueHolder<SingleRateModel> holder = new LambdaValueHolder<>();

        ratesForGender.stream().filter(e -> e.matchBoth(age, zipCode)).findFirst().ifPresent(holder::set);

        if (holder.has()) {
            return holder.get().getRate();
        }
        LOG.warn("Unable to find rate for age: {}, zip: {}, gender: {} from configuration @ {}",
                new Object[]{age, zipCode, gender, configurationPath});

        return null;
    }


}
