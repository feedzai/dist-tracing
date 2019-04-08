package com.feedzai.commons.tracing.engine;

import com.feedzai.commons.tracing.api.Promise;
import com.feedzai.commons.tracing.api.TraceContext;
import com.feedzai.commons.tracing.api.TracingWithContext;
import com.feedzai.commons.tracing.api.TracingWithId;
import com.feedzai.commons.tracing.engine.configuration.CacheConfiguration;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * Base implementation of the tracing functionality with eventID. his implementation relies on the OpenTracing API in
 * order to remain independent from the underlying tracing engine.
 *
 * @author Gonçalo Garcia (goncalo.garcia@feedzai.com)
 */
public abstract class AbstractTracingEngine implements TracingWithContext, TracingWithId {

    /**
     * The Tracer object that will be used by the library. This is an OpenTracing API class so it does not make any
     * assumption regarding the underlying tracing engine.
     */
    private final Tracer tracer;

    /**
     * Maps a traceID to the span that currently represents its point in the execution.
     */
    final Cache<String, Span> spanIdMappings;

    /**
     * The logger.
     */
    static final Logger logger = LoggerFactory.getLogger(AbstractTracingEngine.class.getName());

    /**
     * Constructor for this abstract class to be called by the extension classes to supply the implementation specific
     * parameters.
     *
     * @param tracer        The Tracer implementation of the underlying tracing Engine.
     * @param configuration The configuration parameters for the caches.
     */
    AbstractTracingEngine(final Tracer tracer, final CacheConfiguration configuration) {
        this.tracer = tracer;
        this.spanIdMappings = CacheBuilder.newBuilder().expireAfterWrite(configuration.getExpirationAfterWrite().getNano(), TimeUnit.NANOSECONDS)
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
    public void newTrace(final Runnable toTrace, final String description) {
        final Span span = buildActiveParentSpan(description);
        traceSafely(toTrace, span);
    }


    @Override
    public <R> CompletableFuture<R> newTraceAsync(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                  final String description) {
        final Span span = buildActiveParentSpan(description);
        return finishFutureSpan(toTraceAsync.get(), span);
    }


    @Override
    public Promise newTracePromise(final Supplier<Promise> toTraceAsync, final String description) {
        final Span span = buildActiveParentSpan(description);
        return finishPromiseSpan(toTraceAsync.get(), span);
    }


    @Override
    public <R> R addToTrace(final Supplier<R> toTrace, final String description) {
        final Span span = buildActiveSpan(description);
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
                                     final TraceContext context) {
        final Span span = buildSpanFromAsyncContext(description, (SpanTraceContext) context);
        return finishPromiseSpan(toTraceAsync.get(), span);
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
    <R> CompletableFuture<R> finishFutureSpan(final CompletableFuture<R> toTraceAsync, final Span span) {
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
    Promise finishPromiseSpan(final Promise toTraceAsync, final Span span) {
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
    <R> R traceSafelyAndReturn(final Supplier<R> toTrace, final Span span) {
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
    void traceSafely(final Runnable toTrace, final Span span) {
        try {
            toTrace.run();
        } finally {
            span.finish();
        }
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
     * Creates a new parent span (a span with no parents) and activates it.
     *
     * @param description The description or name that best describes this operation.
     * @return The new parent span
     */
    Span buildActiveParentSpan(final String description) {
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
    Span buildActiveSpanAsChild(final String description, final SpanTraceContext context) {
        final Span span = buildSpanFromAsyncContext(description, context);
        this.tracer.activateSpan(span);
        updateSpanMappings(span);
        return span;
    }

    /**
     * Builds a span and activates it.
     *
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
