/*
 *
 *  * Copyright 2018 Feedzai
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

package com.feedzai.commons.tracing.api;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Specifies an API for distributed Tracing of Feedzai's products in situations where the execution spans multiple
 * threads, or multiple requests are processed by a single thread, and as such it's not possible to store the tracing
 * context in thread-local variables. Use this if there is an application level event ID that we can map the traceID to
 * and propagate context out-of-band.
 *
 * @author Gonçalo Garcia (goncalo.garcia@feedzai.com)
 */
public interface TracingWithId {

    /**
     * Begins a new trace by creating a parentless span and associates it to {@code eventId}. Traces operations that
     * return a value of any type.
     * <p>Similar to {@link TracingWithId#newTrace(Runnable, String, String)}, {@link
     * TracingWithId#newTraceAsync(Supplier, String, String)} and {@link TracingWithId#newTracePromise(Supplier, String,
     * String)} but returning any value.
     *
     * @param toTrace     Lambda containing the code that should be wrapped in a trace.
     * @param description The description or name that best describes this operation.
     * @param eventId     The ID that represents a request throughout the whole execution.
     * @param <R>         The Return type of the traced code.
     * @return Returns whatever the traced code would have returned.
     */
    <R> R newTrace(Supplier<R> toTrace, String description, String eventId);

    /**
     * Traces operations that do not return any values.
     *
     * <p>Similar to {@link TracingWithId#newTrace(Supplier, String, String)}, {@link
     * TracingWithId#newTraceAsync(Supplier, String, String)} and {@link TracingWithId#newTracePromise(Supplier, String,
     * String)} but without returning any object.
     *
     * @param toTrace     Lambda containing the code that should be wrapped in a trace.
     * @param eventId     The ID that represents a request throughout the whole execution.
     * @param description The description or name that best describes this operation.
     */
    void newTrace(Runnable toTrace, String description, String eventId);


    /**
     * Traces operations that are performed in the background and return a {@link CompletableFuture}, where tracing the
     * call does not trace the full execution.
     *
     * <p>Similar to {@link TracingWithId#newTrace(Supplier, String, String)}, {@link TracingWithId#newTrace(Runnable,
     * String, String)} and {@link TracingWithId#newTracePromise(Supplier, String, String)} but returning a {@link
     * CompletableFuture}
     *
     * @param toTraceAsync Lambda containing the code that should be wrapped in a trace.
     * @param description  The description or name that best describes this operation.
     * @param eventId      The ID that represents a request throughout the whole execution.
     * @param <R>          The Return type of the traced code.
     * @return Returns the {@link CompletableFuture} the traced code would have returned.
     */
    <R> CompletableFuture<R> newTraceAsync(Supplier<CompletableFuture<R>> toTraceAsync, String description,
                                           String eventId);

    /**
     * Traces operations that are performed in the background and return a {@link Promise}, where tracing the call does
     * not trace the full execution.
     *
     * <p>Similar to {@link TracingWithId#newTrace(Supplier, String, String)}, {@link TracingWithId#newTrace(Runnable,
     * String, String)} and {@link TracingWithId#newTraceAsync(Supplier, String, String)} but returning a {@link
     * Promise}
     *
     * @param toTraceAsync Lambda containing the code that should be wrapped in a trace.
     * @param description  The description or name that best describes this operation.
     * @param eventId      The ID that represents a request throughout the whole execution.
     * @return Returns the {@link Promise} the traced code would've returned.
     */
    Promise newTracePromise(Supplier<Promise> toTraceAsync, String description, String eventId);

    /**
     * Creates a new Span that is the root of this process's portion of the trace. Similarly to {@link
     * TracingWithId#newTrace(Supplier, String, String)} this method creates a "root dependency",  meaning future spans
     * can become children of this span even after it has finished. However, unlike the aforementioned method, this will
     * become a child of the previous process's context.
     *
     * <p>Similar to {@link TracingWithId#newProcess(Runnable, String, String)}, {@link
     * TracingWithId#newProcessFuture(Supplier, String, String)} and {@link TracingWithId#newProcessPromise(Supplier,
     * String, String)} but returning what was returned by the traced code.
     *
     * @param toTrace     The code that should be traced.
     * @param description The description/name of the new context.
     * @param eventId     The ID that represents a request throughout the whole execution.
     * @param <R>         The return type.
     * @return What was to be returned by the traced code.
     */
    <R> R newProcess(final Supplier<R> toTrace, final String description, final String eventId);

    /**
     * Creates a new Span that is the root of this process's portion of the trace.
     *
     * <p>Similar to {@link TracingWithId#newProcess(Supplier, String, String)}, {@link
     * TracingWithId#newProcessFuture(Supplier, String, String)} and {@link TracingWithId#newProcessPromise(Supplier,
     * String, String)} but returning what was returning nothing.
     *
     * @param toTrace     The code that should be traced.
     * @param description The description/name of the new context.
     * @param eventId     The ID that represents a request throughout the whole execution.
     */
    void newProcess(final Runnable toTrace, final String description, final String eventId);

    /**
     * Creates a new Span that is the root of this process's portion of the trace.
     *
     * <p>Similar to {@link TracingWithId#newProcess(Supplier, String, String)}, {@link
     * TracingWithId#newProcess(Runnable, String, String)} and {@link TracingWithId#newProcessPromise(Supplier, String,
     * String)} but returning a CompletableFuture object.
     *
     * @param toTrace     The code that should be traced.
     * @param description The description/name of the new context.
     * @param eventId     The ID that represents a request throughout the whole execution.
     * @return What was to be returned by the traced code.
     */
    CompletableFuture newProcessFuture(final Supplier<CompletableFuture> toTrace, final String description,
                                       final String eventId);

    /**
     * Creates a new Span that is the root of this process's portion of the trace.
     *
     * <p>Similar to {@link TracingWithId#newProcess(Supplier, String, String)}, {@link
     * TracingWithId#newProcess(Runnable, String, String)} and {@link TracingWithId#newProcessFuture(Supplier, String,
     * String)} but returning a Promise object.
     *
     * @param toTrace     The code that should be traced.
     * @param description The description/name of the new context.
     * @param eventId     The ID that represents a request throughout the whole execution.
     * @return What was to be returned by the traced code.
     */
    Promise newProcessPromise(final Supplier<Promise> toTrace, final String description, final String eventId);

    /**
     * Traces operations that return a value of any type. This method will add a Span to an existing trace which will
     * become a child of the currently active trace context.
     *
     * <p>Similar to {@link TracingWithId#addToTrace(Runnable, String, String)} and
     * {@link TracingWithId#addToTraceAsync(Supplier, String, String)} but returning any value.
     *
     * @param toTrace     Lambda containing the code that should be wrapped in a trace.
     * @param description The description or name that best describes this operation.
     * @param eventId     The ID that represents a request throughout the whole execution.
     * @param <R>         The Return type of the traced code.
     * @return Returns whatever the traced code would have returned.
     */
    <R> R addToTrace(Supplier<R> toTrace, String description, String eventId);

    /**
     * Traces operations that do not return any values.
     *
     * <p>Similar to {@link TracingWithId#addToTrace(Supplier, String, String)} and
     * {@link TracingWithId#addToTraceAsync(Supplier, String, String)} but without returning any object.
     *
     * @param toTrace     Lambda containing the code that should be wrapped in a trace.
     * @param description The description or name that best describes this operation.
     * @param eventId     The ID that represents a request throughout the whole execution.
     */
    void addToTrace(Runnable toTrace, String description, String eventId);


    /**
     * Traces operations that are performed in the background and return a {@link CompletableFuture}, where tracing the
     * call does not trace the full execution.
     *
     * <p>Similar to {@link TracingWithId#addToTrace(Supplier, String, String)} and
     * {@link TracingWithId#addToTrace(Runnable, String, String)} but returning a {@link CompletableFuture}
     *
     * @param toTraceAsync Lambda containing the code that should be wrapped in a trace.
     * @param description  The description or name that best describes this operation.
     * @param eventId      The ID that represents a request throughout the whole execution.
     * @param <R>          The Return type of the traced code.
     * @return Returns the {@link CompletableFuture} the traced code would have returned.
     */
    <R> CompletableFuture<R> addToTraceAsync(Supplier<CompletableFuture<R>> toTraceAsync, String description,
                                             String eventId);


    /**
     * Traces operations that are performed in the background and return a {@link Promise}, where tracing the call does
     * not trace the full execution.
     *
     * <p>Similar to {@link TracingWithId#addToTrace(Supplier, String, String)},
     * {@link TracingWithId#addToTrace(Runnable, String, String)} and {@link TracingWithId#addToTraceAsync(Supplier,
     * String, String)} but returning a {@link Promise}
     *
     * @param toTraceAsync Lambda containing the code that should be wrapped in a trace.
     * @param description  The description or name that best describes this operation.
     * @param eventId      The ID that represents a request throughout the whole execution.
     * @return Returns the {@link Promise} the traced code would've returned.
     */
    Promise addToTracePromise(Supplier<Promise> toTraceAsync, String description, String eventId);



    /**
     * Returns the current context associated to an eventId.
     *
     * @param eventId The application level eventId
     * @return The current context associated to the eventId
     */
    TraceContext currentContextforId(final String eventId);

    /**
     * Returns true if a traceId has been associated to the eventId, and false otherwise.
     *
     * @param eventId The application level eventId.
     * @return true if a traceId has been associated to the eventId, and false otherwise.
     */
    boolean traceHasStarted(final String eventId);

}
