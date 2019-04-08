package com.feedzai.commons.tracing.engine;

import com.feedzai.commons.tracing.api.Promise;
import com.feedzai.commons.tracing.api.TraceContext;
import com.feedzai.commons.tracing.api.Tracing;
import com.feedzai.commons.tracing.engine.configuration.CacheConfiguration;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * Base implementation of the tracing functionality. This implementation relies on the OpenTracing API @see <a
 * href="https://opentracing.io/">https://opentracing.io/</a> in order to remain independent from the underlying tracing
 * engine.
 *
 * @author Gonçalo Garcia (goncalo.garcia@feedzai.com)
 */
public abstract class AbstractTracingEngine implements Tracing {

    /**
     * The Tracer object that will be used by the library. This is an OpenTracing API class so it does not make any
     * assumption regarding the underlying tracing engine.
     */
    private final Tracer tracer;

    /**
     * Maps a traceID to the span that currently represents its point in the execution.
     */
    private final Cache<String, Span> spanIdMappings;


    /**
     * Maps an application specific ID that identifies a trace to the TraceId.
     */
    private final Cache<String, String> traceIdMappings;

    /**
     * The logger.
     */
    protected static final Logger logger = LoggerFactory.getLogger(AbstractTracingEngine.class.getName());

    /**
     * Constructor for this abstract class to be called by the extension classes to supply the implementation specific
     * parameters.
     *
     * @param tracer        The Tracer implementation of the underlying tracing Engine.
     * @param configuration The configuration parameters for the caches.
     */
    protected AbstractTracingEngine(final Tracer tracer, final CacheConfiguration configuration) {
        this.tracer = tracer;
        this.spanIdMappings = CacheBuilder.newBuilder().expireAfterWrite(configuration.getExpirationAfterWrite().getNano(), TimeUnit.NANOSECONDS)
                .maximumSize(configuration.getMaximumSize()).build();
        this.traceIdMappings = CacheBuilder.newBuilder().expireAfterWrite(configuration.getExpirationAfterWrite().getNano(), TimeUnit.NANOSECONDS)
                .maximumSize(configuration.getMaximumSize()).build();
    }


    @Override
    public <R> R newTrace(final Supplier<R> toTrace, final String description) {
        final Span span = buildActiveParentSpan(description);
        final R result;
        result = traceSafelyAndReturn(toTrace, span);
        return result;
    }

    @Override
    public <R> R newTrace(final Supplier<R> toTrace, final String description, final String eventId) {
        final Span span = newTraceWithId(description, eventId);
        return traceSafelyAndReturn(toTrace, span);
    }

    @Override
    public void newTrace(final Runnable toTrace, final String description) {
        final Span span = buildActiveParentSpan(description);
        traceSafely(toTrace, span);
    }

    @Override
    public void newTrace(final Runnable toTrace, final String description, final String eventId) {
        final Span span = newTraceWithId(description, eventId);
        traceSafely(toTrace, span);
    }

    @Override
    public <R> CompletableFuture<R> newTraceAsync(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                  final String description) {
        final Span span = buildActiveParentSpan(description);
        return finishFutureSpan(toTraceAsync.get(), span);
    }

    @Override
    public <R> CompletableFuture<R> newTraceAsync(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                  final String description, final String eventId) {
        final Span span = newTraceWithId(description, eventId);
        return finishFutureSpan(toTraceAsync.get(), span);
    }

    @Override
    public Promise newTracePromise(final Supplier<Promise> toTraceAsync, final String description) {
        final Span span = buildActiveParentSpan(description);
        return finishPromiseSpan(toTraceAsync.get(), span);
    }

    @Override
    public Promise newTracePromise(final Supplier<Promise> toTraceAsync, final String description,
                                   final String eventId) {
        final Span span = newTraceWithId(description, eventId);
        return finishPromiseSpan(toTraceAsync.get(), span);
    }

    @Override
    public <R> R addToTrace(final Supplier<R> toTrace, final String description) {
        final Span span = buildActiveSpan(description);
        return traceSafelyAndReturn(toTrace, span);
    }

    @Override
    public <R> R addToTrace(final Supplier<R> toTrace, final String description, final String eventId) {
        final Span span = buildContextFromId(description, eventId);
        return traceSafelyAndReturn(toTrace, span);
    }

    @Override
    public <R> R addToTrace(final Supplier<R> toTrace, final String description, final TraceContext context) {
        final Span span = buildActiveSpanAsChild(description, (SpanTraceContext) context);
        return traceSafelyAndReturn(toTrace, span);
    }

    @Override
    public void addToTrace(final Runnable toTrace, final String description) {
        final Span span = buildActiveSpan(description);
        traceSafely(toTrace, span);
    }

    @Override
    public void addToTrace(final Runnable toTrace, final String description, final String eventId) {
        final Span span = buildContextFromId(description, eventId);
        traceSafely(toTrace, span);
    }

    @Override
    public void addToTrace(final Runnable toTrace, final String description, final TraceContext context) {
        final Span span = buildActiveSpanAsChild(description, (SpanTraceContext) context);
        traceSafely(toTrace, span);
    }

    @Override
    public <R> CompletableFuture<R> addToTraceAsync(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                    final String description) {
        final Span span = buildActiveSpan(description);
        return finishFutureSpan(toTraceAsync.get(), span);
    }

    @Override
    public <R> CompletableFuture<R> addToTraceAsync(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                    final String description, final String eventId) {
        final Span span = buildContextFromId(description, eventId);
        return finishFutureSpan(toTraceAsync.get(), span);
    }


    @Override
    public <R> CompletableFuture<R> addToTraceAsync(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                    final String description, final TraceContext context) {
        final Span span = buildSpanFromAsyncContext(description, (SpanTraceContext) context);
        return finishFutureSpan(toTraceAsync.get(), span);
    }

    @Override
    public Promise addToTracePromise(final Supplier<Promise> toTraceAsync, final String description) {
        final Span span = buildActiveSpan(description);
        return finishPromiseSpan(toTraceAsync.get(), span);
    }

    @Override
    public Promise addToTracePromise(final Supplier<Promise> toTraceAsync, final String description,
                                     final String eventId) {
        final Span span = buildContextFromId(description, eventId);
        return finishPromiseSpan(toTraceAsync.get(), span);
    }


    @Override
    public Promise addToTracePromise(final Supplier<Promise> toTraceAsync, final String description,
                                     final TraceContext context) {
        final Span span = buildSpanFromAsyncContext(description, (SpanTraceContext) context);
        return finishPromiseSpan(toTraceAsync.get(), span);
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
     * Finishes span after the {@link CompletableFuture} has completed, either successfully or exceptionally.
     *
     * <p>Similar to {@link AbstractTracingEngine#finishPromiseSpan(Promise, Span)} but for {@link CompletableFuture}
     *
     * @param toTraceAsync The {@link CompletableFuture} to which the callback will be attached.
     * @param span         The span that is wrapping the execution and should be finished.
     * @param <R>          The return type of the {@link CompletableFuture}
     * @return the same {@link CompletableFuture} that was passed in {@code toTraceAsync}
     */
    private <R> CompletableFuture<R> finishFutureSpan(final CompletableFuture<R> toTraceAsync, final Span span) {
        return toTraceAsync.handle((future, exception) -> {
            span.log(exception.getMessage());
            span.finish();
            return future;
        });
    }

    /**
     * Attaches callback to finish span after the {@link Promise} has completed.
     *
     * <p>Similar to {@link AbstractTracingEngine#finishFutureSpan(CompletableFuture, Span)} but for {@link Promise}
     *
     * @param toTraceAsync The {@link Promise} to which the callback will be attached.
     * @param span         The span that is wrapping the execution and should be finished.
     * @return the same {@link Promise} that was passed in {@code toTraceAsync}
     */
    private Promise finishPromiseSpan(final Promise toTraceAsync, final Span span) {
        final Function<Promise, Promise> onFinish = promise -> {
            span.finish();
            return promise;
        };
        return toTraceAsync.onComplete(onFinish).onError(onFinish);
    }

    /**
     * Closes the enclosing scope when the supplied is finished, regardless of whether it finished correctly or
     * exceptionally.
     *
     * <p>Similar to {@link AbstractTracingEngine#traceSafely(Runnable, Span)} but returning a value.
     *
     * @param toTrace The method that should be executed and traced.
     * @param span    The tracing span that encloses this method.
     * @param <R>     The return type of the executed method.
     * @return The object that is returned by the executed method.
     */
    private <R> R traceSafelyAndReturn(final Supplier<R> toTrace, final Span span) {
        R result;
        try {
            result = toTrace.get();
        } finally {
            span.finish();
        }
        return result;
    }

    /**
     * Closes the enclosing scope when the supplied is finished, regardless of whether it finished correctly or
     * exceptionally.
     *
     * <p>Similar to {@link AbstractTracingEngine#traceSafelyAndReturn(Supplier, Span)} but returning nothing.
     *
     * @param toTrace The method that should be executed and traced.
     * @param span    The tracing span that encloses this method.
     */
    private void traceSafely(final Runnable toTrace, final Span span) {
        try {
            toTrace.run();
        } finally {
            span.finish();
        }
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
            parent = this.spanIdMappings.getIfPresent(traceId.get()).context();
        }
        //if the parent is null this span will be orphan but no exception is thrown.
        final Span span = buildActiveSpanAsChild(description, new SpanTraceContext(parent));
        updateIdMappings(eventId, span);
        return span;
    }

    /**
     * Creates a new Span as child of the passed {@link SpanContext} and activates it.
     *
     * @param description The description/name of the new context.
     * @param context     Represents the context of the current execution.
     * @return The new active Span.
     */
    private Span buildSpanFromAsyncContext(final String description, final SpanTraceContext context) {
        final Span span = this.tracer.buildSpan(description).asChildOf(context.get()).start();
        this.tracer.activateSpan(span);
        updateSpanMappings(span);
        return span;
    }

    /**
     * When given a Span that represents a context update this method will update the mapping between the trace ID and
     * the current context.
     *
     * @param span Span that represents the current context.
     */
    private void updateSpanMappings(final Span span) {
        final String traceId = getTraceIdFromSpan(span);
        this.spanIdMappings.put(traceId, span);
    }

    /**
     * Obtains a TraceID from a Span object. Since the only way to do this is through manipulation of the serialized
     * span String, which is implementation specific, this method cannot be implemented in this class.
     *
     * @param span Span from which we want the traceID.
     * @return A String representing the traceId.
     */
    abstract String getTraceIdFromSpan(final Span span);

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
     * Creates a new parent span (a span with no parents) and activates it.
     *
     * @param description The description or name that best describes this operation.
     * @return The new parent span
     */
    private Span buildActiveParentSpan(final String description) {
        final Span span = this.tracer.buildSpan(description).ignoreActiveSpan().start();
        this.tracer.activateSpan(span);
        updateSpanMappings(span);
        return span;
    }

    /**
     * Creates a new Span that is a child of the passed {@link SpanTraceContext}.
     *
     * @param description The description or name that best describes this operation.
     * @param context     The parent of this new span.
     * @return the new child Span.
     */
    private Span buildActiveSpanAsChild(final String description, final SpanTraceContext context) {
        final Span span = buildSpanFromAsyncContext(description, context);
        this.tracer.activateSpan(span);
        updateSpanMappings(span);
        return span;
    }

    /**
     * Builds a span and activates it.
     * @param description The description or name that best describes this operation.
     * @return the new active span.
     */
    private Span buildActiveSpan(final String description) {
        final Span span = this.tracer.buildSpan(description).start();
        this.tracer.activateSpan(span);
        updateSpanMappings(span);
        return span;
    }

}
