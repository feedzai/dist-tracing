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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;


public class LazyConfigTracer implements TracingEngine {


    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(LoggingTracingEngine.class.getName());

    /**
     * The last tracing engine in use.
     */
    private TracingEngine oldEngine;

    /**
     * The last configuration in use.
     */
    private TracingConfiguration oldConfiguration;

    /**
     * The supplier that reloads the configuration file.
     */
    private Supplier<TracingConfiguration> tracingConfigurationSupplier;

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
        engine = CacheBuilder.newBuilder().maximumSize(1).expireAfterWrite(10, TimeUnit.MINUTES).build();
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
            TracingConfiguration config = tracingConfigurationSupplier.get();
            if (config.equals(oldConfiguration)) {
                logger.info("Tracing Configuration hasn't changed. Will not change tracer.");
                return oldEngine;
            } else {
                TracingEngine engine = null;
                switch (config.activeEngine) {
                    case JAEGER:
                        JaegerConfiguration jaegerConfiguration = config.jaegerConfiguration;
                        engine = new JaegerTracingEngine.Builder().fromConfig(jaegerConfiguration).build();
                        break;
                    case NOOP:
                        engine = new NoopTracingEngine();
                        break;
                    case LOGGER:
                        engine = new LoggingTracingEngine();
                        break;
                    default:
                        engine = new LoggingTracingEngine();
                }
                oldConfiguration = config;
                oldEngine = engine;
                logger.info("Starting tracer with {} engine", oldConfiguration.activeEngine);
                if (oldConfiguration.activeEngine == Engines.JAEGER) {
                    JaegerConfiguration cfg = oldConfiguration.jaegerConfiguration;
                    logger.info("Jaeger Tracer configuration = samplingRate={}, cacheMaxSize={}, cacheDurationInMinutes={}, processName={} and IP={}"
                            , cfg.sampleRate, cfg.cacheMaxSize, cfg.cacheDurationInMinutes, cfg.processName, cfg.ip);
                }
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
        } catch (ExecutionException e) {
            return new NoopTracingEngine();
        }
    }

    @Override
    public <R> Promise<R> addToTraceOpenPromise(Supplier<Promise<R>> toTraceAsync, Object object,
                                                String description) {
        return getEngine().addToTraceOpenPromise(toTraceAsync, object, description);
    }

    @Override
    public <R> CompletableFuture<R> addToTraceOpenFuture(Supplier<CompletableFuture<R>> toTraceAsync,
                                                         Object object, String description) {
        return getEngine().addToTraceOpenFuture(toTraceAsync, object, description);
    }

    @Override
    public void addToTraceOpen(Runnable toTraceAsync, Object object, String description) {
        getEngine().addToTraceOpen(toTraceAsync, object, description);
    }

    @Override
    public <R> R addToTraceOpen(Supplier<R> toTraceAsync, Object value, String description) {
        return getEngine().addToTraceOpen(toTraceAsync, value, description);
    }

    @Override
    public <R> Promise<R> addToTraceOpenPromise(Supplier<Promise<R>> toTraceAsync, Object object,
                                                String description, String eventId) {
        return getEngine().addToTraceOpenPromise(toTraceAsync, object, description, eventId);
    }

    @Override
    public <R> CompletableFuture<R> addToTraceOpenFuture(Supplier<CompletableFuture<R>> toTraceAsync,
                                                         Object object, String description,
                                                         String eventId) {
        return getEngine().addToTraceOpenFuture(toTraceAsync, object, description, eventId);
    }

    @Override
    public void addToTraceOpen(Runnable toTraceAsync, Object object, String description,
                               String eventId) {
        this.getEngine().addToTraceOpen(toTraceAsync, object, description, eventId);
    }

    @Override
    public <R> R addToTraceOpen(Supplier<R> toTraceAsync, Object value, String description,
                                String eventId) {
        return this.getEngine().addToTraceOpen(toTraceAsync, value, description, eventId);
    }

    @Override
    public void closeOpen(Object object) {
        this.getEngine().closeOpen(object);
    }

    @Override
    public <R> Promise<R> addToTraceOpenPromise(Supplier<Promise<R>> toTraceAsync, Object object,
                                                String description, TraceContext context) {
        return this.getEngine().addToTraceOpenPromise(toTraceAsync, object, description, context);
    }

    @Override
    public <R> CompletableFuture<R> addToTraceOpenFuture(Supplier<CompletableFuture<R>> toTraceAsync,
                                                         Object object, String description,
                                                         TraceContext context) {
        return this.getEngine().addToTraceOpenFuture(toTraceAsync, object, description, context);
    }

    @Override
    public void addToTraceOpen(Runnable toTraceAsync, Object object, String description,
                               TraceContext context) {
        this.getEngine().addToTraceOpen(toTraceAsync, object, description, context);
    }

    @Override
    public <R> R addToTraceOpen(Supplier<R> toTraceAsync, Object value, String description,
                                TraceContext context) {
        return this.getEngine().addToTraceOpen(toTraceAsync, value, description, context);
    }

    @Override
    public <R> R newProcess(Supplier<R> toTrace, String description, TraceContext context) {
        return this.getEngine().newProcess(toTrace, description, context);
    }

    @Override
    public void newProcess(Runnable toTrace, String description, TraceContext context) {
        this.getEngine().newProcess(toTrace, description, context);
    }

    @Override
    public <R> Promise<R> newProcessPromise(Supplier<Promise<R>> toTrace, String description,
                                            TraceContext context) {
        return this.getEngine().newProcessPromise(toTrace, description, context);
    }

    @Override
    public <R> CompletableFuture<R> newProcessFuture(Supplier<CompletableFuture<R>> toTrace,
                                                     String description, TraceContext context) {
        return this.getEngine().newProcessFuture(toTrace, description, context);
    }

    @Override
    public <R> R addToTrace(Supplier<R> toTrace, String description, TraceContext context) {
        return this.getEngine().addToTrace(toTrace, description, context);
    }

    @Override
    public void addToTrace(Runnable toTrace, String description, TraceContext context) {
        this.getEngine().addToTrace(toTrace, description, context);
    }

    @Override
    public <R> CompletableFuture<R> addToTraceAsync(Supplier<CompletableFuture<R>> toTraceAsync, String description,
                                                    TraceContext context) {
        return this.getEngine().addToTraceAsync(toTraceAsync, description, context);
    }

    @Override
    public <R> Promise<R> addToTracePromise(Supplier<Promise<R>> toTraceAsync, String description,
                                            TraceContext context) {
        return this.getEngine().addToTracePromise(toTraceAsync, description, context);
    }

    @Override
    public Serializable serializeContext() {
        return this.getEngine().serializeContext();
    }

    @Override
    public TraceContext deserializeContext(Serializable headers) {
        return this.getEngine().deserializeContext(headers);
    }

    @Override
    public TraceContext currentContext() {
        return this.getEngine().currentContext();
    }

    @Override
    public TraceContext currentContextforObject(Object obj) {
        return this.getEngine().currentContextforObject(obj);
    }

    @Override
    public <R> R newTrace(Supplier<R> toTrace, String description) {
        return this.getEngine().newTrace(toTrace, description);
    }

    @Override
    public void newTrace(Runnable toTrace, String description) {
        this.getEngine().newTrace(toTrace, description);
    }

    @Override
    public <R> CompletableFuture<R> newTraceAsync(Supplier<CompletableFuture<R>> toTraceAsync, String description) {
        return this.getEngine().newTraceAsync(toTraceAsync, description);
    }

    @Override
    public <R> Promise<R> newTracePromise(Supplier<Promise<R>> toTraceAsync, String description) {
        return this.getEngine().newTracePromise(toTraceAsync, description);
    }

    @Override
    public <R> R addToTrace(Supplier<R> toTrace, String description) {
        return this.getEngine().addToTrace(toTrace, description);
    }

    @Override
    public void addToTrace(Runnable toTrace, String description) {
        this.getEngine().addToTrace(toTrace, description);
    }

    @Override
    public <R> CompletableFuture<R> addToTraceAsync(Supplier<CompletableFuture<R>> toTraceAsync, String description) {
        return this.getEngine().addToTraceAsync(toTraceAsync, description);
    }

    @Override
    public <R> Promise<R> addToTracePromise(Supplier<Promise<R>> toTraceAsync, String description) {
        return this.getEngine().addToTracePromise(toTraceAsync, description);
    }

    @Override
    public boolean isActive() {
        return getEngine().isActive();
    }

    @Override
    public <R> R newTrace(Supplier<R> toTrace, String description, String eventId) {
        return this.getEngine().newTrace(toTrace, description, eventId);
    }

    @Override
    public void newTrace(Runnable toTrace, String description, String eventId) {
        this.getEngine().newTrace(toTrace, description, eventId);
    }

    @Override
    public <R> CompletableFuture<R> newTraceAsync(Supplier<CompletableFuture<R>> toTraceAsync, String description,
                                                  String eventId) {
        return this.getEngine().newTraceAsync(toTraceAsync, description, eventId);
    }

    @Override
    public <R> Promise<R> newTracePromise(Supplier<Promise<R>> toTraceAsync, String description, String eventId) {
        return this.getEngine().newTracePromise(toTraceAsync, description, eventId);
    }

    @Override
    public <R> R newProcess(Supplier<R> toTrace, String description, String eventId) {
        return this.getEngine().newProcess(toTrace, description, eventId);
    }

    @Override
    public void newProcess(Runnable toTrace, String description, String eventId) {
        this.getEngine().newProcess(toTrace, description, eventId);
    }

    @Override
    public <R> CompletableFuture newProcessFuture(Supplier<CompletableFuture<R>> toTrace,
                                                  String description, String eventId) {
        return this.getEngine().newProcessFuture(toTrace, description, eventId);
    }

    @Override
    public <R> Promise<R> newProcessPromise(Supplier<Promise<R>> toTrace, String description,
                                            String eventId) {
        return this.getEngine().newProcessPromise(toTrace, description, eventId);
    }

    @Override
    public <R> R addToTrace(Supplier<R> toTrace, String description, String eventId) {
        return this.getEngine().addToTrace(toTrace, description, eventId);
    }

    @Override
    public void addToTrace(Runnable toTrace, String description, String eventId) {
        this.getEngine().addToTrace(toTrace, description, eventId);
    }

    @Override
    public <R> CompletableFuture<R> addToTraceAsync(Supplier<CompletableFuture<R>> toTraceAsync, String description,
                                                    String eventId) {
        return this.getEngine().addToTraceAsync(toTraceAsync, description, eventId);
    }

    @Override
    public <R> Promise<R> addToTracePromise(Supplier<Promise<R>> toTraceAsync, String description, String eventId) {
        return this.getEngine().addToTracePromise(toTraceAsync, description, eventId);
    }

    @Override
    public TraceContext currentContextforId(String eventId) {
        return this.getEngine().currentContextforId(eventId);
    }

    @Override
    public boolean traceHasStarted(String eventId) {
        return this.getEngine().traceHasStarted(eventId);
    }
}
