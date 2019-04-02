package com.feedzai.commons.tracing.api;


import java.io.Serializable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Specifies an API for distributed tracing of Feedzai's products.
 *
 * @author Gonçalo Garcia (goncalo.garcia@feedzai.com)
 */

public interface Tracing {


    /**
     * Begins a new trace by creating a parentless span. This method should be used in situations where there is thread
     * reuse, whenever the entry point of the request is executed. Traces operations that return a value of any type.
     *
     * <p>Similar o {@link Tracing#newTrace(Runnable, String)} and {@link Tracing#newTraceAsync(Supplier, String)} but
     * returning any value.
     *
     * @param toTrace     Lambda containing the code that should be wrapped in a trace.
     * @param description The description or name that best describes this operation.
     * @param <R>         The Return type of the traced code.
     * @return Returns whatever the traced code would have returned.
     */
    <R> R newTrace(Supplier<R> toTrace, String description);

    /**
     * Begins a new trace by creating a parentless span. This method should be used in situations where there is thread
     * reuse, whenever the entry point of the request is executed. Traces operations that do not return any values.
     *
     * <p>Similar to {@link Tracing#newTrace(Supplier, String)} and {@link Tracing#newTraceAsync(Supplier, String)} but
     * without returning any object.
     *
     * @param toTrace     Lambda containing the code that should be wrapped in a trace.
     * @param description The description or name that best describes this operation.
     */
    void newTrace(Runnable toTrace, String description);

    /**
     * Begins a new trace by creating a parentless span. This method should be used in situations where there is thread
     * reuse whenever the entry point of the request is executed. Traces operations that are performed in the background
     * and return a {@link CompletableFuture}, where tracing the call does not trace the full execution.
     *
     * <p>Similar to {@link Tracing#newTrace(Supplier, String)} and {@link Tracing#newTrace(Runnable, String)} but
     * returning a {@link CompletableFuture}
     *
     * @param toTraceAsync Lambda containing the code that should be wrapped in a trace.
     * @param description  The description or name that best describes this operation.
     * @param <R>          The Return type of the traced code.
     * @return Returns the {@link CompletableFuture} the traced code would have returned.
     */
    <R> CompletableFuture<R> newTraceAsync(Supplier<CompletableFuture<R>> toTraceAsync, String description);


    /**
     * Traces operations that return a value of any type. This method will add a Span to an existing trace which will
     * become a child of the currently active trace context.
     *
     * <p>Similar to {@link Tracing#addToTrace(Runnable, String)} and {@link Tracing#addToTraceAsync(Supplier, String)}
     * but returning any value.
     *
     * @param toTrace     Lambda containing the code that should be wrapped in a trace.
     * @param description The description or name that best describes this operation.
     * @param <R>         The Return type of the traced code.
     * @return Returns whatever the traced code would have returned.
     */
    <R> R addToTrace(Supplier<R> toTrace, String description);

    /**
     * Traces operations that return a value of any type. This method will add a Span to an existing trace which will
     * become a child of the currently active trace context.
     *
     * <p>Should be used when the execution of a request spans multiple threads with no instrumentable synchronization
     * points (i.e., not possible to propagate context across threads), or when threads are reused for multiple requests
     * (thread pools).
     *
     * <p>Similar to {@link Tracing#addToTrace(Runnable, String, String)} and
     * {@link Tracing#addToTraceAsync(Supplier, String, String)} but returning any value.
     *
     * @param toTrace         Lambda containing the code that should be wrapped in a trace.
     * @param description     The description or name that best describes this operation.
     * @param fromTraceWideId The ID that represents a request throughout the whole execution.
     * @param <R>             The Return type of the traced code.
     * @return Returns whatever the traced code would have returned.
     */
    <R> R addToTrace(Supplier<R> toTrace, String description, String fromTraceWideId);

    /**
     * Traces operations that return a value of any type. This method will add a Span to an existing trace which will
     * become a child of the currently active trace context.
     *
     * <p>Useful when the request's execution spans multiple components/nodes that belong to the same trace.
     *
     * <p>Similar to {@link Tracing#addToTrace(Runnable, String, Serializable)} and
     * {@link Tracing#addToTraceAsync(Supplier, String, Serializable)} but returning any value.
     *
     * @param toTrace           Lambda containing the code that should be wrapped in a trace.
     * @param description       The description or name that best describes this operation.
     * @param serializedContext Represents the context of the current execution. Receiving this context as Serializable,
     *                          as opposed to a tracing specific object decouples this API from any lower level tracing
     *                          API constructs (i.e., OpenTracing's Span or SpanContext).
     * @param <R>               The Return type of the traced code.
     * @return Returns whatever the traced code would have returned.
     */
    <R> R addToTrace(Supplier<R> toTrace, String description, Serializable serializedContext);

    /**
     * Traces operations that do not return any values. This method will add a Span to an existing trace which will
     * become a child of the currently active trace context.
     *
     * <p>Similar to {@link Tracing#addToTrace(Supplier, String)} and {@link Tracing#addToTraceAsync(Supplier, String)}
     * but without returning any object.
     *
     * @param toTrace     Lambda containing the code that should be wrapped in a trace.
     * @param description The description or name that best describes this operation.
     */
    void addToTrace(Runnable toTrace, String description);


    /**
     * Traces operations that do not return any values. This method will add a Span to an existing trace which will
     * become a child of the currently active trace context.
     *
     * <p>Similar to {@link Tracing#addToTrace(Supplier, String, String)} and
     * {@link Tracing#addToTraceAsync(Supplier, String, String)} but without returning any object.
     *
     * <p>Should be used when the execution of a request spans multiple threads with no instrumentable synchronization
     * points (i.e., not possible to propagate context across threads), or when threads are reused for multiple requests
     * (thread pools).
     *
     * @param toTrace         Lambda containing the code that should be wrapped in a trace.
     * @param description     The description or name that best describes this operation.
     * @param fromTraceWideId The ID that represents a request throughout the whole execution.
     */
    void addToTrace(Runnable toTrace, String description, String fromTraceWideId);

    /**
     * Traces operations that do not return any values. This method will add a Span to an existing trace which will
     * become a child of the currently active trace context.
     *
     * <p>Useful when the request's execution spans multiple components/nodes that belong to the same trace.
     *
     * <p>Similar to {@link Tracing#addToTrace(Supplier, String, Serializable)} and
     * {@link Tracing#addToTraceAsync(Supplier, String, Serializable)} but without returning any object.
     *
     * @param toTrace           Lambda containing the code that should be wrapped in a trace.
     * @param description       The description or name that best describes this operation.
     * @param serializedContext Represents the context of the current execution. Receiving this context as Serializable,
     *                          as opposed to a tracing specific object decouples this API from any lower level tracing
     *                          API constructs (i.e., OpenTracing's Span or SpanContext).
     */
    void addToTrace(Runnable toTrace, String description, Serializable serializedContext);


    /**
     * Traces operations that are performed in the background and return a {@link CompletableFuture}, where tracing the
     * call does not trace the full execution. This method will add a Span to an existing trace which will become a
     * child of the currently active trace context.
     *
     * <p>Similar to {@link Tracing#addToTrace(Supplier, String)} and {@link Tracing#addToTrace(Runnable, String)} but
     * returning a {@link CompletableFuture}
     *
     * @param toTraceAsync Lambda containing the code that should be wrapped in a trace.
     * @param description  The description or name that best describes this operation.
     * @param <R>          The Return type of the traced code.
     * @return Returns the {@link CompletableFuture} the traced code would have returned.
     */
    <R> CompletableFuture<R> addToTraceAsync(Supplier<CompletableFuture<R>> toTraceAsync, String description);

    /**
     * Traces operations that are performed in the background and return a {@link CompletableFuture}, where tracing the
     * call does not trace the full execution. This method will add a Span to an existing trace which will become a
     * child of the currently active trace context.
     *
     * <p>Should be used when the execution of a request spans multiple threads, with no instrumentable synchronization
     * points (i.e., not possible to propagate context across threads), or when threads are reused for multiple requests
     * (thread pools).
     *
     * <p>Similar to {@link Tracing#addToTrace(Supplier, String, String)} and
     * {@link Tracing#addToTrace(Runnable, String, String)} but returning a {@link CompletableFuture}
     *
     * @param toTraceAsync    Lambda containing the code that should be wrapped in a trace.
     * @param description     The description or name that best describes this operation.
     * @param fromTraceWideId The ID that represents a request throughout the whole execution.
     * @param <R>             The Return type of the traced code.
     * @return Returns the {@link CompletableFuture} the traced code would have returned.
     */
    <R> CompletableFuture<R> addToTraceAsync(Supplier<CompletableFuture<R>> toTraceAsync, String description, String fromTraceWideId);


    /**
     * Traces operations that are performed in the background and return a {@link CompletableFuture}, where tracing the
     * call does not trace the full execution. This method will add a Span to an existing trace which will become a
     * child of the currently active trace context.
     *
     * <p>Useful when the request's execution spans multiple components/nodes that belong to the same trace.
     *
     * <p>Similar to {@link Tracing#addToTrace(Supplier, String, Serializable)} and
     * {@link Tracing#addToTrace(Runnable, String, Serializable)} but returning a {@link CompletableFuture}
     *
     * @param toTraceAsync      Lambda containing the code that should be wrapped in a trace.
     * @param description       The description or name that best describes this operation.
     * @param serializedContext Represents the context of the current execution.  Receiving this context as
     *                          Serializable, as opposed to a tracing specific object decouples this API from any lower
     *                          level tracing API constructs (i.e., OpenTracing's Span or SpanContext).
     * @param <R>               The Return type of the traced code.
     * @return Returns the {@link CompletableFuture} the traced code would have returned.
     */
    <R> CompletableFuture<R> addToTraceAsync(Supplier<CompletableFuture<R>> toTraceAsync, String description, Serializable serializedContext);


}
