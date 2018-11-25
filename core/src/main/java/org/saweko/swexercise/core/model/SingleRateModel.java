package org.saweko.swexercise.core.model;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;

/**
 * Model for singe rate row.
 * Parses jcr loaded {@link #ageRange} and {@link #zipCodeRange} performing string.split with {@link #RANGE_SEPARATOR} as separator
 * if field contains incorrect data it will throw {@link IllegalStateException}
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SingleRateModel {
    private static final String RANGE_SEPARATOR = "-";
    @ValueMapValue
    private String ageRange;

    @ValueMapValue
    private String zipCodeRange;

    @ValueMapValue()
    private Double rate;

    private Integer ageRangeStart;
    private Integer ageRangeEnd;

    private Integer zipCodeRangeStart;
    private Integer zipCodeRangeEnd;

    public SingleRateModel() {
    }

    @PostConstruct
    private void postConstruct() {
        final IntegerPair parsedAge = parseRange("age", ageRange);
        if (parsedAge != null) {
            ageRangeStart = parsedAge.getLeft();
            ageRangeEnd = parsedAge.getRight();
        }

        final IntegerPair parsedZipCodes = parseRange("zipCode", zipCodeRange);

        if (parsedZipCodes != null) {
            zipCodeRangeStart = parsedZipCodes.getLeft();
            zipCodeRangeEnd = parsedZipCodes.getRight();
        }
    }

    public boolean matchBoth(final int age, final int  zipCode) {
        return ageMatch(age) && zipCodeMatch(zipCode);
    }

    public boolean ageMatch(int age) {
        return ageRangeStart != null && ageRangeEnd != null && age >= ageRangeStart && age < ageRangeEnd;
    }

    public boolean zipCodeMatch(int zipCode) {
        return zipCodeRangeStart != null && zipCodeRangeEnd != null && zipCode >= zipCodeRangeStart && zipCode < zipCodeRangeEnd;
    }

    public Integer getAgeRangeStart() {
        return ageRangeStart;
    }

    public Integer getAgeRangeEnd() {
        return ageRangeEnd;
    }

    public Integer getZipCodeRangeStart() {
        return zipCodeRangeStart;
    }

    public Integer getZipCodeRangeEnd() {
        return zipCodeRangeEnd;
    }

    public Double getRate() {
        return rate;
    }

    public boolean anyNull() {
        return !ObjectUtils.allNotNull(ageRangeStart, ageRangeEnd, zipCodeRangeStart, zipCodeRangeEnd, rate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("ageRangeStart", ageRangeStart)
                .append("ageRangeEnd", ageRangeEnd)
                .append("zipCodeRangeStart", zipCodeRangeStart)
                .append("zipCodeRangeEnd", zipCodeRangeEnd)
                .append("rate", rate)
                .toString();
    }

    /**
     * will parse value argument exactly as described in {@link SingleRateModel} class description
     * @param fieldName used for exception declaration
     * @param value - value to parse
     * @return pair of integers containing parsed values
     */
    private IntegerPair parseRange(final String fieldName, final String value) {
        if (StringUtils.isNotBlank(value)) {
            String[] ranges = value.split(RANGE_SEPARATOR);
            if (ranges.length != 2 || !StringUtils.isNumeric(ranges[0]) || !StringUtils.isNumeric(ranges[1])) {
                throw new IllegalStateException("Cannot parse age range for field: " + fieldName + " with value: " + ageRange);
            }
            return IntegerPair.of(Integer.parseInt(ranges[0]), Integer.parseInt(ranges[1]));
        }
        return null;
    }

    /**
     * Helper tuple class
     */
    private static final class IntegerPair extends Pair<Integer, Integer> {
        private Integer left;
        private Integer right;

        @Override
        public Integer getLeft() {
            return left;
        }

        @Override
        public Integer getRight() {
            return right;
        }

        @Override
        public Integer setValue(final Integer value) {
            throw new UnsupportedOperationException("Unsupported");
        }

        public static IntegerPair of(final Integer left, final Integer right) {
            final IntegerPair toReturn = new IntegerPair();
            toReturn.left = left;
            toReturn.right = right;
            return toReturn;
        }
    }
}
