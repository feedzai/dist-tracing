package com.feedzai.commons.tracing.util.configuration;

import com.feedzai.commons.tracing.engine.configuration.JaegerConfiguration;
import com.feedzai.commons.tracing.util.Engines;

/**
 * Class that holds the configuration for for tracing.
 *
 * @author Gonçalo Garcia (goncalo.garcia@feedzai.com)
 */
public class TracingConfiguration {

    /**
     * The engine that should be used.
     */
    public Engines activeEngine;

    /**
     * The configuration parameters for {@link com.feedzai.commons.tracing.engine.JaegerTracingEngine}.
     */
    public JaegerConfiguration jaegerConfiguration;

}
