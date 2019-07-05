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
