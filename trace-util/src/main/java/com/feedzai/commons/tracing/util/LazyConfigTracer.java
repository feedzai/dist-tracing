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

package com.feedzai.commons.tracing.util;

import com.feedzai.commons.tracing.api.Promise;
import com.feedzai.commons.tracing.api.TraceContext;
import com.feedzai.commons.tracing.engine.JaegerTracingEngine;
import com.feedzai.commons.tracing.engine.LoggingTracingEngine;
import com.feedzai.commons.tracing.engine.NoopTracingEngine;
import com.feedzai.commons.tracing.engine.TracingEngine;
import com.feedzai.commons.tracing.engine.configuration.JaegerConfiguration;
import com.feedzai.commons.tracing.util.configuration.TracingConfiguration;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.opentracing.Span;
import io.opentracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;


/**
 * Tracer decorator that periodically realoads the configuration, allowing for configuration changes on the fly.
 *
 * @author Gonçalo Garcia (goncalo.garcia@feedzai.com)
 */
public class LazyConfigTracer implements TracingEngine {


    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(LoggingTracingEngine.class.getName());

    /**
     * The last tracing engine in use.
     */
    private volatile TracingEngine oldEngine;

    /**
     * The last configuration in use.
     */
    private volatile TracingConfiguration oldConfiguration;

    /**
     * The supplier that reloads the configuration file.
     */
    private final Supplier<TracingConfiguration> tracingConfigurationSupplier;

    /**
     * The cached tracing engine to be used for tracing.
     */
    private final Cache<String, TracingEngine> engine;

    /**
     * The constructor used to build an instance of LazyConfigTracer.
     *
     * @param configurationSupplier The supplier that returns the TracingConfiguration loaded by this class.
     */
    public LazyConfigTracer(final Supplier<TracingConfiguration> configurationSupplier) {
        engine = CacheBuilder.newBuilder().maximumSize(1).expireAfterWrite(1, TimeUnit.MINUTES).build();
        this.tracingConfigurationSupplier = configurationSupplier;
        oldEngine = getEngine();
        oldConfiguration = configurationSupplier.get();

    }

    /**
     * Creates a callable that will reload the configuration file and return a new engine.
     *
     * @return The callable returning a new engine.
     */
    private Callable<? extends TracingEngine> reloadEngine() {
        return () -> {
            final TracingConfiguration config = tracingConfigurationSupplier.get();
            if (config.equals(oldConfiguration)) {
                logger.info("Tracing Configuration hasn't changed. Will not change tracer.");
                return oldEngine;
            } else {
                TracingEngine engine = null;
                switch (config.activeEngine) {
                    case JAEGER:
                        final JaegerConfiguration cfg = config.jaegerConfiguration;
                        engine = new JaegerTracingEngine.Builder().fromConfig(cfg).build();
                        logger.debug("Jaeger Tracer configuration = samplingRate={}, cacheMaxSize={}, cacheDurationInMinutes={}, processName={} and IP={}"
                                ,cfg.sampleRate, cfg.cacheMaxSize, cfg.cacheDurationInMinutes, cfg.processName, cfg.ip);
                        break;
                    case NOOP:
                        engine = new NoopTracingEngine();
                        break;
                    case LOGGER:
                        engine = new LoggingTracingEngine();
                        break;
                    default:
                        logger.warn("Config is unknow, defaulting to Logging");
                        engine = new NoopTracingEngine();
                }
                oldConfiguration = config;
                oldEngine = engine;
                logger.debug("Starting tracer with {} engine", oldConfiguration.activeEngine);
                return engine;
            }
        };
    }

    /**
     * Gets the engine to be used at the current instant.
     *
     * @return the available engine.
     */
    private TracingEngine getEngine() {
        try {
            return engine.get("", reloadEngine());
        } catch (final ExecutionException e) {
            return new NoopTracingEngine();
        }
    }

    @Override
    public <R> Promise<R> addToTraceOpenPromise(final Supplier<Promise<R>> toTraceAsync, final Object object,
                                                final String description) {
        return getEngine().addToTraceOpenPromise(toTraceAsync, object, description);
    }

    @Override
    public <R> CompletableFuture<R> addToTraceOpenFuture(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                         final Object object, final String description) {
        return getEngine().addToTraceOpenFuture(toTraceAsync, object, description);
    }

    @Override
    public void addToTraceOpen(final Runnable toTraceAsync, final Object object, final String description) {
        getEngine().addToTraceOpen(toTraceAsync, object, description);
    }

    @Override
    public <R> R addToTraceOpen(final Supplier<R> toTraceAsync, final Object value, final String description) {
        return getEngine().addToTraceOpen(toTraceAsync, value, description);
    }

    @Override
    public <R> Promise<R> addToTraceOpenPromise(final Supplier<Promise<R>> toTraceAsync, final Object object,
                                                final String description, final String eventId) {
        return getEngine().addToTraceOpenPromise(toTraceAsync, object, description, eventId);
    }

    @Override
    public <R> CompletableFuture<R> addToTraceOpenFuture(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                         final Object object, final String description,
                                                         final String eventId) {
        return getEngine().addToTraceOpenFuture(toTraceAsync, object, description, eventId);
    }

    @Override
    public void addToTraceOpen(final Runnable toTraceAsync, final Object object, final String description,
                               final String eventId) {
        this.getEngine().addToTraceOpen(toTraceAsync, object, description, eventId);
    }

    @Override
    public <R> R addToTraceOpen(final Supplier<R> toTraceAsync, final Object value, final String description,
                                final String eventId) {
        return this.getEngine().addToTraceOpen(toTraceAsync, value, description, eventId);
    }

    @Override
    public void closeOpen(final Object object) {
        this.getEngine().closeOpen(object);
    }

    @Override
    public <R> Promise<R> addToTraceOpenPromise(final Supplier<Promise<R>> toTraceAsync, final Object object,
                                                final String description, final TraceContext context) {
        return this.getEngine().addToTraceOpenPromise(toTraceAsync, object, description, context);
    }

    @Override
    public <R> CompletableFuture<R> addToTraceOpenFuture(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                         final Object object, final String description,
                                                         final TraceContext context) {
        return this.getEngine().addToTraceOpenFuture(toTraceAsync, object, description, context);
    }

    @Override
    public void addToTraceOpen(final Runnable toTraceAsync, final Object object, final String description,
                               final TraceContext context) {
        this.getEngine().addToTraceOpen(toTraceAsync, object, description, context);
    }

    @Override
    public <R> R addToTraceOpen(final Supplier<R> toTraceAsync, final Object value, final String description,
                                final TraceContext context) {
        return this.getEngine().addToTraceOpen(toTraceAsync, value, description, context);
    }

    @Override
    public <R> R newProcess(final Supplier<R> toTrace, final String description, final TraceContext context) {
        return this.getEngine().newProcess(toTrace, description, context);
    }

    @Override
    public void newProcess(final Runnable toTrace, final String description, final TraceContext context) {
        this.getEngine().newProcess(toTrace, description, context);
    }

    @Override
    public <R> Promise<R> newProcessPromise(final Supplier<Promise<R>> toTrace, final String description,
                                            final TraceContext context) {
        return this.getEngine().newProcessPromise(toTrace, description, context);
    }

    @Override
    public <R> CompletableFuture<R> newProcessFuture(final Supplier<CompletableFuture<R>> toTrace,
                                                     final String description, final TraceContext context) {
        return this.getEngine().newProcessFuture(toTrace, description, context);
    }

    @Override
    public <R> R addToTrace(final Supplier<R> toTrace, final String description, final TraceContext context) {
        return this.getEngine().addToTrace(toTrace, description, context);
    }

    @Override
    public void addToTrace(final Runnable toTrace, final String description, final TraceContext context) {
        this.getEngine().addToTrace(toTrace, description, context);
    }

    @Override
    public <R> CompletableFuture<R> addToTraceAsync(final Supplier<CompletableFuture<R>> toTraceAsync, final String description,
                                                    final TraceContext context) {
        return this.getEngine().addToTraceAsync(toTraceAsync, description, context);
    }

    @Override
    public <R> Promise<R> addToTracePromise(final Supplier<Promise<R>> toTraceAsync, final String description,
                                            final TraceContext context) {
        return this.getEngine().addToTracePromise(toTraceAsync, description, context);
    }

    @Override
    public Serializable serializeContext() {
        return this.getEngine().serializeContext();
    }

    @Override
    public TraceContext deserializeContext(final Serializable headers) {
        return this.getEngine().deserializeContext(headers);
    }

    @Override
    public TraceContext currentContext() {
        return this.getEngine().currentContext();
    }

    @Override
    public TraceContext currentContextforObject(final Object obj) {
        return this.getEngine().currentContextforObject(obj);
    }

    @Override
    public <R> R newTrace(final Supplier<R> toTrace, final String description) {
        return this.getEngine().newTrace(toTrace, description);
    }

    @Override
    public void newTrace(final Runnable toTrace, final String description) {
        this.getEngine().newTrace(toTrace, description);
    }

    @Override
    public <R> CompletableFuture<R> newTraceAsync(final Supplier<CompletableFuture<R>> toTraceAsync, final String description) {
        return this.getEngine().newTraceAsync(toTraceAsync, description);
    }

    @Override
    public <R> Promise<R> newTracePromise(final Supplier<Promise<R>> toTraceAsync, final String description) {
        return this.getEngine().newTracePromise(toTraceAsync, description);
    }

    @Override
    public <R> R addToTrace(final Supplier<R> toTrace, final String description) {
        return this.getEngine().addToTrace(toTrace, description);
    }

    @Override
    public void addToTrace(final Runnable toTrace, final String description) {
        this.getEngine().addToTrace(toTrace, description);
    }

    @Override
    public <R> CompletableFuture<R> addToTraceAsync(final Supplier<CompletableFuture<R>> toTraceAsync, final String description) {
        return this.getEngine().addToTraceAsync(toTraceAsync, description);
    }

    @Override
    public <R> Promise<R> addToTracePromise(final Supplier<Promise<R>> toTraceAsync, final String description) {
        return this.getEngine().addToTracePromise(toTraceAsync, description);
    }

    @Override
    public boolean isActive() {
        return getEngine().isActive();
    }

    @Override
    public <R> R newTrace(final Supplier<R> toTrace, final String description, final String eventId) {
        return this.getEngine().newTrace(toTrace, description, eventId);
    }

    @Override
    public void newTrace(final Runnable toTrace, final String description, final String eventId) {
        this.getEngine().newTrace(toTrace, description, eventId);
    }

    @Override
    public <R> CompletableFuture<R> newTraceAsync(final Supplier<CompletableFuture<R>> toTraceAsync, final String description,
                                                  final String eventId) {
        return this.getEngine().newTraceAsync(toTraceAsync, description, eventId);
    }

    @Override
    public <R> Promise<R> newTracePromise(final Supplier<Promise<R>> toTraceAsync, final String description, final String eventId) {
        return this.getEngine().newTracePromise(toTraceAsync, description, eventId);
    }

    @Override
    public <R> R newProcess(final Supplier<R> toTrace, final String description, final String eventId) {
        return this.getEngine().newProcess(toTrace, description, eventId);
    }

    @Override
    public void newProcess(final Runnable toTrace, final String description, final String eventId) {
        this.getEngine().newProcess(toTrace, description, eventId);
    }

    @Override
    public <R> CompletableFuture newProcessFuture(final Supplier<CompletableFuture<R>> toTrace,
                                                  final String description, final String eventId) {
        return this.getEngine().newProcessFuture(toTrace, description, eventId);
    }

    @Override
    public <R> Promise<R> newProcessPromise(final Supplier<Promise<R>> toTrace, final String description,
                                            final String eventId) {
        return this.getEngine().newProcessPromise(toTrace, description, eventId);
    }

    @Override
    public <R> R addToTrace(final Supplier<R> toTrace, final String description, final String eventId) {
        return this.getEngine().addToTrace(toTrace, description, eventId);
    }

    @Override
    public void addToTrace(final Runnable toTrace, final String description, final String eventId) {
        this.getEngine().addToTrace(toTrace, description, eventId);
    }

    @Override
    public <R> CompletableFuture<R> addToTraceAsync(final Supplier<CompletableFuture<R>> toTraceAsync, final String description,
                                                    final String eventId) {
        return this.getEngine().addToTraceAsync(toTraceAsync, description, eventId);
    }

    @Override
    public <R> Promise<R> addToTracePromise(final Supplier<Promise<R>> toTraceAsync, final String description, final String eventId) {
        return this.getEngine().addToTracePromise(toTraceAsync, description, eventId);
    }

    @Override
    public TraceContext currentContextforId(final String eventId) {
        return this.getEngine().currentContextforId(eventId);
    }

    @Override
    public boolean traceHasStarted(final String eventId) {
        return this.getEngine().traceHasStarted(eventId);
    }

    @Override
    public Tracer getTracer() {
        return this.getEngine().getTracer();
    }

    @Override
    public Span currentSpan() {
        return this.getEngine().currentSpan();
    }
}
