package org.saweko.swexercise.core.model;

import org.apache.commons.lang3.StringUtils;

public enum Gender {
    MALE, FEMALE;

    public static Gender parse(final String value) {
        for (Gender g:Gender.values()) {
            if (StringUtils.equals(g.name(), value)) {
                return g;
            }
        }
        return null;
    }
}
