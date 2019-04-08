package com.feedzai.commons.tracing.api;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Specifies an API for distributed Tracing of Feedzai's products. Should be used for propagating context across
 * multiple processes that belong on the same trace and when there is no way to avoid propagating context explicitly in
 * the code.
 *
 * @author Gonçalo Garcia (goncalo.garcia@feedzai.com)
 */
public interface TracingWithContext extends Tracing {

    /**
     * Traces operations that return a value of any type. This method will add a Span to an existing trace which will
     * become a child of passed {@link TraceContext}
     *
     *
     * <p>Similar to {@link TracingWithContext#addToTrace(Runnable, String, TraceContext)} and
     * {@link TracingWithContext#addToTraceAsync(Supplier, String, TraceContext)} but returning any value.
     *
     * @param toTrace     Lambda containing the code that should be wrapped in a trace.
     * @param description The description or name that best describes this operation.
     * @param context     Represents the context of the current execution. Receiving this context as TraceContext, as
     *                    opposed to a tracing specific object decouples this API from any lower level tracing API
     *                    constructs (i.e., OpenTracing's Span or SpanContext).
     * @param <R>         The Return type of the traced code.
     * @return Returns whatever the traced code would have returned.
     */
    <R> R addToTrace(Supplier<R> toTrace, String description, TraceContext context);

    /**
     * Traces operations that do not return any values.
     *
     * <p>Similar to {@link TracingWithContext#addToTrace(Supplier, String, TraceContext)} and
     * {@link TracingWithContext#addToTraceAsync(Supplier, String, TraceContext)} but without returning any object.
     *
     * @param toTrace     Lambda containing the code that should be wrapped in a trace.
     * @param description The description or name that best describes this operation.
     * @param context     Represents the context of the current execution.
     */
    void addToTrace(Runnable toTrace, String description, TraceContext context);


    /**
     * Traces operations that are performed in the background and return a {@link CompletableFuture}, where tracing the
     * call does not trace the full execution.
     *
     *
     * <p>Similar to {@link TracingWithContext#addToTrace(Supplier, String, TraceContext)} and
     * {@link TracingWithContext#addToTrace(Runnable, String, TraceContext)} but returning a {@link CompletableFuture}
     *
     * @param toTraceAsync Lambda containing the code that should be wrapped in a trace.
     * @param description  The description or name that best describes this operation.
     * @param context      Represents the context of the current execution.
     * @param <R>          The Return type of the traced code.
     * @return Returns the {@link CompletableFuture} the traced code would have returned.
     */
    <R> CompletableFuture<R> addToTraceAsync(Supplier<CompletableFuture<R>> toTraceAsync, String description,
                                             TraceContext context);

    /**
     * Traces operations that are performed in the background and return a {@link Promise}, where tracing the call does
     * not trace the full execution.
     *
     *
     * <p>Similar to {@link TracingWithContext#addToTrace(Supplier, String, TraceContext)},
     * {@link TracingWithContext#addToTrace(Runnable, String, TraceContext)} and {@link
     * TracingWithContext#addToTraceAsync(Supplier, String, TraceContext)} but returning a {@link Promise}
     *
     * @param toTraceAsync Lambda containing the code that should be wrapped in a trace.
     * @param description  The description or name that best describes this operation.
     * @param context      Represents the context of the current execution.
     * @return Returns the {@link Promise} the traced code would've returned.
     */
    Promise addToTracePromise(Supplier<Promise> toTraceAsync, String description, TraceContext context);
}
