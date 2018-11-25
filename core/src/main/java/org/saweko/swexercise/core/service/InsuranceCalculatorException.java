package org.saweko.swexercise.core.service;

import org.saweko.swexercise.core.model.Gender;

/**
 * Exception thrown by {@link InsuranceCalculatorService#calculateRate(String, Integer, Integer, Gender)}  when error condition occurred
 */
public class InsuranceCalculatorException extends Exception {
    public InsuranceCalculatorException() {
    }

    public InsuranceCalculatorException(final String message) {
        super(message);
    }

    public InsuranceCalculatorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InsuranceCalculatorException(final Throwable cause) {
        super(cause);
    }

    public InsuranceCalculatorException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
