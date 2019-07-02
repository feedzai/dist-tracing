package com.feedzai.commons.tracing.engine.configuration;

/**
 * Class that holds the configuration for {@link com.feedzai.commons.tracing.engine.JaegerTracingEngine}
 *
 * @author Gonçalo Garcia (goncalo.garcia@feedzai.com)
 */
public class JaegerConfiguration {

    /**
     * The duration of each cache record prior to expiration.
     */
    public long cacheDurationInMinutes;

    /**
     * The maximum size of the caches.
     */
    public long cacheMaxSize;

    /**
     * The rate at which requests will be sampled for tracing.
     */
    public double sampleRate;

    /**
     * The name of the this process.
     */
    public String processName;

    /**
     * The ip address of the jaeger agent.
     */
    public String ip;

}
