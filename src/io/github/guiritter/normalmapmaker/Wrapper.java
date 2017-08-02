package io.github.guiritter.normalmapmaker;

/**
 * Object that wraps another object so the outer one can be passed via parameter
 * and the inner one can be set.
 * @author Guilherme Alan Ritter
 * @param <T> inner object type
 */
public final class Wrapper<T> {

    /**
     * Inner object.
     */
    public T o;

    public Wrapper() {}

    public Wrapper(T o) {
        this.o = o;
    }

    @Override
    public String toString() {
        return o.toString();
    }
}
