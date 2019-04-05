package com.feedzai.commons.tracing.engine.configuration;

import java.time.Duration;

/**
 * Configuration for the caches used in {@link com.feedzai.commons.tracing.engine.AbstractTracingEngine}.
 *
 * @author Gonçalo Garcia (goncalo.garcia@feedzai.com)
 */
public class CacheConfiguration {

    /**
     * The duration after which the entries will expire.
     */
    private final Duration expirationAfterWrite;

    /**
     * The maximum number of elements.
     */
    private final long maximumSize;


    /**
     * The constructor for this {@link CacheConfiguration}.
     * @param expirationAfterWrite The duration after which the entries will expire.
     * @param maximumSize The maximum number of elements.
     */
    public CacheConfiguration(final Duration expirationAfterWrite, final long maximumSize) {
        this.expirationAfterWrite = expirationAfterWrite;
        this.maximumSize = maximumSize;
    }


    /**
     * Getter for {@code expirationAfterWrite}.
     * @return The value of {@code expirationAfterWrite}.
     */
    public Duration getExpirationAfterWrite() {
        return expirationAfterWrite;
    }

    /**
     * Getter for {@code maximumSize}.
     * @return The value of {@code maximumSize}.
     */
    public long getMaximumSize() {
        return maximumSize;
    }
}
