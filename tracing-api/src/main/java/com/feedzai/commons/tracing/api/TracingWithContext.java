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

package com.feedzai.commons.tracing.api;

import java.io.Serializable;
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
     * Creates a new Span that is the root of this process's portion of the trace. Similarly to {@link
     * Tracing#newTrace(Supplier, String)} this method creates a "root dependency",  meaning future spans can become
     * children of this span even after it has finished. However, unlike the aforementioned method, this will become a
     * child of the previous process's context.
     *
     * <p>Similar to {@link TracingWithContext#newProcess(Runnable, String, TraceContext)}, {@link
     * TracingWithContext#newProcessFuture(Supplier, String, TraceContext)} and {@link
     * TracingWithContext#newProcessPromise(Supplier, String, TraceContext)} but returning what was returned by the
     * traced code.
     *
     * @param toTrace     The code that should be traced.
     * @param description The description/name of the new context.
     * @param context     The context that should be this span's parent
     * @param <R>         The return type.
     * @return What was to be returned by the traced code.
     */
    <R> R newProcess(final Supplier<R> toTrace, final String description, final TraceContext context);

    /**
     * Creates a new Span that is the root of this process's portion of the trace.
     *
     * <p>Similar to {@link TracingWithContext#newProcess(Supplier, String, TraceContext)}, {@link
     * TracingWithContext#newProcessFuture(Supplier, String, TraceContext)} and {@link
     * TracingWithContext#newProcessPromise(Supplier, String, TraceContext)} but returning what was returning nothing.
     *
     * @param toTrace     The code that should be traced.
     * @param description The description/name of the new context.
     * @param context     The context that should be this span's parent
     */
    void newProcess(final Runnable toTrace, final String description, final TraceContext context);

    /**
     * Creates a new Span that is the root of this process's portion of the trace.
     *
     * <p>Similar to {@link TracingWithContext#newProcess(Supplier, String, TraceContext)}, {@link
     * TracingWithContext#newProcess(Runnable, String, TraceContext)} and {@link TracingWithContext#newProcessPromise(Supplier,
     * String, TraceContext)} but returning a CompletableFuture object.
     *
     * @param toTrace     The code that should be traced.
     * @param description The description/name of the new context.
     * @param context     The context that should be this span's parent
     * @param <R>         The Return type of the traced code.
     * @return The instrumented Promise.
     */
    <R> Promise<R> newProcessPromise(final Supplier<Promise<R>> toTrace, final String description,
                                     final TraceContext context);

    /**
     * Creates a new Span that is the root of this process's portion of the trace.
     *
     * <p>Similar to {@link TracingWithContext#newProcess(Supplier, String, TraceContext)}, {@link
     * TracingWithContext#newProcess(Runnable, String, TraceContext)} and {@link TracingWithContext#newProcessFuture(Supplier, String,
     * TraceContext)} but returning a Promise object.
     *
     * @param toTrace     The code that should be traced.
     * @param description The description/name of the new context.
     * @param context     The context that should be this span's parent
     * @param <R>         The type returned by the future.
     * @return The instrumented future.
     */
    <R> CompletableFuture<R> newProcessFuture(final Supplier<CompletableFuture<R>> toTrace, final String description,
                                              final TraceContext context);

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
     * @param <R>         The Return type of the traced code.
     * @return Returns the {@link Promise} the traced code would've returned.
     */
    <R> Promise<R> addToTracePromise(Supplier<Promise<R>> toTraceAsync, String description, TraceContext context);


    /**
     * Serializes the current context so that it can be deserialized by another service with implementation specific
     * instrumentation.
     *
     * @return Return the serialized context.
     */
    Serializable serializeContext();

    /**
     * Deserializes the context received from another service.
     *
     * @param headers The serialized context.
     * @return The deserialized version of the context.
     */
    TraceContext deserializeContext(final Serializable headers);

    /**
     * Returns the current active content.
     *
     * @return The current active context
     */
    TraceContext currentContext();


    /**
     * Returns the current context associated to an object.
     *
     * @param obj The object to be used as key.
     * @return The current context associated to the object.
     */
    TraceContext currentContextforObject(final Object obj);

}
