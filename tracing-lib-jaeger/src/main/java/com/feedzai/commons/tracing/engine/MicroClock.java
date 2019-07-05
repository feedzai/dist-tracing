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

package com.feedzai.commons.tracing.engine;

import io.jaegertracing.internal.clock.Clock;

/**
 * Clock implementation with microsecond precision.
 *
 * @author Gonçalo Garcia (goncalo.garcia@feedzai.com)
 */
public class MicroClock implements Clock {

    /**
     * Conversion factor. 1 milliseconds equals 1.000.000 nanoseconds.
     */
    private static final int MILLIS_TO_NANOS_MULTIPLIER = 1_000_000;

    /**
     * The offset between currentTimeMillis() and nanoTime(). This is calculated when this class is instantiated in
     * order to get the nanos elapsed since Epoch.
     */
    private final long offset;

    public MicroClock() {
        offset = MILLIS_TO_NANOS_MULTIPLIER * System.currentTimeMillis() - System.nanoTime();
    }

    @Override
    public long currentTimeMicros() {
        final long timestamp = System.nanoTime();
        return (timestamp + offset) / 1000;
    }

    @Override
    public boolean isMicrosAccurate() {
        return false;
    }

    @Override
    public long currentNanoTicks() {
        return System.nanoTime();
    }

}