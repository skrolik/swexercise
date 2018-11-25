package org.saweko.swexercise.core.util;

/**
 * Simple class for extracting single results from streams
 */
public class LambdaValueHolder<T> {
    private T value;

    public T get() {
        return value;
    }

    public void set(final T value) {
        this.value = value;
    }

    public boolean has() {
        return value != null;
    }


}
