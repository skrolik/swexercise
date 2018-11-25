package org.saweko.swexercise.core.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import java.util.List;

/**
 * Simple helper model for accessing rates from java
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class InsuranceRatesModel {
    /* TODO: maybe add some validation for duplicated entries? */

    @ChildResource(name = "jcr:content/male-rates")
    private List<SingleRateModel> maleModel;

    @ChildResource(name = "jcr:content/female-rates")
    private List<SingleRateModel> femaleModel;

    public List<SingleRateModel> getMaleModel() {
        return maleModel;
    }

    public List<SingleRateModel> getFemaleModel() {
        return femaleModel;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("maleModel", maleModel)
                .append("femaleModel", femaleModel)
                .toString();
    }
}
