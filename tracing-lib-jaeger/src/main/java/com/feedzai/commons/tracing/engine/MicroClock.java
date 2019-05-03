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