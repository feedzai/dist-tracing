package com.feedzai.commons.tracing.api;

/**
 * Encapsulates objects that represents trace contexts in the underlying implementations.
 * @param <T> The type of the context object.
 *
 * @author Gonçalo Garcia (goncalo.garcia@feedzai.com)
 */
public interface TraceContext<T> {

    /**
     * Returns the context.
     * @return The context object.
     */
    T get();



}
