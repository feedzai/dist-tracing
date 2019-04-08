package com.feedzai.commons.tracing.engine;

import com.feedzai.commons.tracing.api.Promise;
import com.feedzai.commons.tracing.engine.configuration.CacheConfiguration;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;


/**
 * Base implementation of the tracing functionality with eventID. This implementation relies on the OpenTracing API in
 * order to remain independent from the underlying tracing engine.
 *
 * @author Gonçalo Garcia (goncalo.garcia@feedzai.com)
 */
public abstract class AbstractTracingEngineWithId extends AbstractTracingEngine {

    /**
     * Maps an application specific ID that identifies a trace to the TraceId.
     */
    private final Cache<String, String> traceIdMappings;


    /**
     * Constructor for this abstract class to be called by the extension classes to supply the implementation specific
     * parameters.
     *
     * @param tracer        The Tracer implementation of the underlying tracing Engine.
     * @param configuration The configuration parameters for the caches.
     */
    protected AbstractTracingEngineWithId(final Tracer tracer,
                                          final CacheConfiguration configuration) {
        super(tracer, configuration);
        this.traceIdMappings = CacheBuilder.newBuilder().expireAfterWrite(configuration.getExpirationAfterWrite().getNano(), TimeUnit.NANOSECONDS)
                .maximumSize(configuration.getMaximumSize()).build();
    }

    @Override
    public <R> R newTrace(final Supplier<R> toTrace, final String description, final String eventId) {
        final Span span = newTraceWithId(description, eventId);
        return traceSafelyAndReturn(toTrace, span);
    }

    @Override
    public void newTrace(final Runnable toTrace, final String description, final String eventId) {
        final Span span = newTraceWithId(description, eventId);
        traceSafely(toTrace, span);
    }

    @Override
    public <R> CompletableFuture<R> newTraceAsync(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                  final String description, final String eventId) {
        final Span span = newTraceWithId(description, eventId);
        return finishFutureSpan(toTraceAsync.get(), span);
    }

    @Override
    public Promise newTracePromise(final Supplier<Promise> toTraceAsync, final String description,
                                   final String eventId) {
        final Span span = newTraceWithId(description, eventId);
        return finishPromiseSpan(toTraceAsync.get(), span);
    }

    @Override
    public <R> R addToTrace(final Supplier<R> toTrace, final String description, final String eventId) {
        final Span span = buildContextFromId(description, eventId);
        return traceSafelyAndReturn(toTrace, span);
    }

    @Override
    public void addToTrace(final Runnable toTrace, final String description, final String eventId) {
        final Span span = buildContextFromId(description, eventId);
        traceSafely(toTrace, span);
    }

    @Override
    public <R> CompletableFuture<R> addToTraceAsync(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                    final String description, final String eventId) {
        final Span span = buildContextFromId(description, eventId);
        return finishFutureSpan(toTraceAsync.get(), span);
    }

    @Override
    public Promise addToTracePromise(final Supplier<Promise> toTraceAsync, final String description,
                                     final String eventId) {
        final Span span = buildContextFromId(description, eventId);
        return finishPromiseSpan(toTraceAsync.get(), span);
    }

    /**
     * Creates a new Span as child of the context associated with {@code eventId}.
     *
     * @param description The description/name of the new context.
     * @param eventId     The ID that represents a request throughout the whole execution.
     * @return The new Span.
     */
    private Span buildContextFromId(final String description, final String eventId) {
        final Optional<String> traceId = getTraceIdForAppSpecificId(eventId);
        SpanContext parent = null;
        if (traceId.isPresent()) {
            parent = super.spanIdMappings.getIfPresent(traceId.get()).context();
        }
        //if the parent is null this span will be orphan but no exception is thrown.
        final Span span = buildActiveSpanAsChild(description, new SpanTraceContext(parent));
        updateIdMappings(eventId, span);
        return span;
    }

    /**
     * Starts a parentless span that represents the beginning of a new trace and maps it to {@code eventId}.
     *
     * @param description The description or name that best describes this operation.
     * @param eventId     The ID that represents a request throughout the whole execution.
     * @return The first span of this trace.
     */
    private Span newTraceWithId(final String description, final String eventId) {
        final Span span = buildActiveParentSpan(description);
        updateIdMappings(eventId, span);
        return span;
    }

    /**
     * Gets the trace ID associated to an application specific ID.
     *
     * @param eventId The application specific ID.
     * @return An Optional containing the value of the trace ID if it is present in the cache.
     */
    private Optional<String> getTraceIdForAppSpecificId(final String eventId) {
        final String traceId = this.traceIdMappings.getIfPresent(eventId);
        if (traceId == null) {
            logger.warn("No trace ID was found for application specific ID {}", eventId);
        }
        return Optional.ofNullable(traceId);
    }

    /**
     * Updates the mapping between the application specific ID and the TraceID. If no mapping is found this method will
     * create a new mapping.
     *
     * @param eventId the application specific ID.
     * @param span    the span associated to the traceID.
     */
    private void updateIdMappings(final String eventId, final Span span) {
        this.traceIdMappings.put(eventId, getTraceIdFromSpan(span));
    }


}
