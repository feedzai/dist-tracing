/*
 *
 *  * Copyright 2019 Feedzai
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 *
 */

package com.feedzai.commons.tracing.engine;

import com.feedzai.commons.tracing.api.Promise;
import com.feedzai.commons.tracing.engine.configuration.CacheConfiguration;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;

import java.util.LinkedList;
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
    protected final Cache<String, String> traceIdMappings;

    /**
     * The key for the baggage item containing the eventID.
     */
    protected static final String EVENT_ID = "id";

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
        this.traceIdMappings = CacheBuilder.newBuilder().expireAfterWrite(configuration.getExpirationAfterWrite().toNanos(), TimeUnit.NANOSECONDS)
                .maximumSize(configuration.getMaximumSize()).build();
    }

    @Override
    public <R> R newTrace(final Supplier<R> toTrace, final String description, final String eventId) {
        final Span span = newTraceWithId(description, eventId);
        return traceParentSafelyAndReturn(toTrace, span);
    }

    @Override
    public void newTrace(final Runnable toTrace, final String description, final String eventId) {
        final Span span = newTraceWithId(description, eventId);
        traceParentSafely(toTrace, span);
    }

    @Override
    public <R> CompletableFuture<R> newTraceAsync(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                  final String description, final String eventId) {
        final Span span = newTraceWithId(description, eventId);
        return finishParentFutureSpan(toTraceAsync.get(), span);
    }

    @Override
    public Promise newTracePromise(final Supplier<Promise> toTraceAsync, final String description,
                                   final String eventId) {
        final Span span = newTraceWithId(description, eventId);
        return finishParentPromiseSpan(toTraceAsync, span);
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

        return finishPromiseSpan(toTraceAsync, span);
    }

    @Override
    public <R> R newProcess(final Supplier<R> toTrace, final String description, final String eventId) {
        final Span span = buildContextFromId(description, eventId);
        spanIdMappings.put(getTraceIdFromSpan(span), new LinkedList<>());
        updateSpanMappings(span);
        final R result;
        result = traceParentSafelyAndReturn(toTrace, span);
        return result;
    }

    @Override
    public void newProcess(final Runnable toTrace, final String description, final String eventId) {
        final Span span = buildContextFromId(description, eventId);
        spanIdMappings.put(getTraceIdFromSpan(span), new LinkedList<>());
        updateSpanMappings(span);

        traceParentSafely(toTrace, span);
    }

    @Override
    public CompletableFuture newProcessFuture(final Supplier<CompletableFuture> toTrace, final String description,
                                              final String eventId) {
        final Span span = buildContextFromId(description, eventId);
        spanIdMappings.put(getTraceIdFromSpan(span), new LinkedList<>());
        updateSpanMappings(span);

        return finishParentFutureSpan(toTrace.get(), span);
    }

    @Override
    public Promise newProcessPromise(final Supplier<Promise> toTrace, final String description, final String eventId) {
        final Span span = buildContextFromId(description, eventId);
        spanIdMappings.put(getTraceIdFromSpan(span), new LinkedList<>());
        updateSpanMappings(span);
        return finishParentPromiseSpan(toTrace, span);
    }

    @Override
    public <R> Promise addToTraceOpenPromise(final Supplier<Promise<R>> toTraceAsync, final Object object,
                                             final String description,
                                             final String eventId) {
        final Span span = buildContextFromId(description, eventId);
        responseMappings.put(object, span);
        return toTraceAsync.get();
    }

    @Override
    public <R> CompletableFuture<R> addToTraceOpenFuture(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                         final Object object,
                                                         final String description,
                                                         final String eventId) {
        final Span span = buildContextFromId(description, eventId);
        responseMappings.put(object, span);
        return toTraceAsync.get();
    }

    @Override
    public void addToTraceOpen(final Runnable toTraceAsync, final Object object, final String description,
                               final String eventId) {
        final Span span = buildContextFromId(description, eventId);
        final String traceId = getTraceIdFromSpan(span);
        cacheObject(object, span, traceId);
        toTraceAsync.run();
    }

    @Override
    public <R> R addToTraceOpen(final Supplier<R> toTraceAsync, final Object value, final String description,
                                final String eventId) {
        final Span span = buildContextFromId(description, eventId);
        final String traceId = getTraceIdFromSpan(span);
        cacheObject(value, span, traceId);
        return toTraceAsync.get();
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
            final LinkedList<Span> parentStack = super.spanIdMappings.getIfPresent(traceId.get());
            if (!parentStack.isEmpty()) {
                parent = parentStack.peek().context();
            }
        } else {
            return tracer.buildSpan("NoParent " + description).ignoreActiveSpan().start();
        }
        return buildActiveSpanAsChild(description, new SpanTraceContext(parent));
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
        span.setBaggageItem(EVENT_ID, eventId);
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

    /**
     * Returns the current context associated to an eventId.
     *
     * @param eventId The application level eventId
     * @return The current context associated to the eventId
     */
    public SpanTraceContext currentContextforId(final String eventId) {
        String id = eventId;
        if (eventId == null) {
            id = "";
        }
        if (spanIdMappings.getIfPresent(traceIdMappings.getIfPresent(id) != null ? traceIdMappings.getIfPresent(id) : "") != null) {
            return new SpanTraceContext(spanIdMappings.getIfPresent(traceIdMappings.getIfPresent(id)).peek().context());
        } else {
            return null;
        }
    }

    /**
     * Returns true if a traceId has been associated to the eventId, and false otherwise.
     *
     * @param eventId The application level eventId.
     * @return true if a traceId has been associated to the eventId, and false otherwise.
     */
    public boolean traceHasStarted(final String eventId) {
        return traceIdMappings.getIfPresent(eventId) != null;
    }

}
