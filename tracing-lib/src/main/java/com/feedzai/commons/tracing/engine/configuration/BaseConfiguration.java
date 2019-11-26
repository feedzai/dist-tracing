/*
 * Copyright 2018 Feedzai
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.feedzai.commons.tracing.engine.configuration;

import com.feedzai.commons.tracing.engine.AbstractOpenTracingEngine;

import java.time.Duration;

/**
 * Configuration for the caches used in {@link AbstractOpenTracingEngine}.
 *
 * @author Gonçalo Garcia (goncalo.garcia@feedzai.com)
 */
public class BaseConfiguration {

    /**
     * The duration after which the cache entries will expire.
     */
    private final Duration cacheExpirationAfterWrite;

    /**
     * The maximum number of elements in the cache.
     */
    private final long cacheMaxSize;

    /**
     * The rate at which requests are sampled
     */
    private final double samplingRate;


    /**
     * The constructor for this {@link BaseConfiguration}.
     * @param cacheExpirationAfterWrite The duration after which the entries will expire.
     * @param cacheMaxSize The maximum number of elements.
     */
    public BaseConfiguration(final Duration cacheExpirationAfterWrite, final long cacheMaxSize, final double samplingRate) {
        this.cacheExpirationAfterWrite = cacheExpirationAfterWrite;
        this.cacheMaxSize = cacheMaxSize;
        this.samplingRate = samplingRate;
    }

    /**
     * Getter for {@code samplingRate}.
     * @return The value of {@code samplingRate}.
     */
    public double getSamplingRate() {
        return samplingRate;
    }

    /**
     * Getter for {@code cacheExpirationAfterWrite}.
     * @return The value of {@code cacheExpirationAfterWrite}.
     */
    public Duration getCacheExpirationAfterWrite() {
        return cacheExpirationAfterWrite;
    }

    /**
     * Getter for {@code cacheMaxSize}.
     * @return The value of {@code cacheMaxSize}.
     */
    public long getCacheMaxSize() {
        return cacheMaxSize;
    }
}
