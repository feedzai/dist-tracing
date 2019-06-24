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

package com.feedzai.commons.tracing.api;


import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Specifies an API for distributed tracing of Feedzai's products. This API should be used in situations where there is
 * no need to trace across thread context switches, or process boundaries.
 *
 * @author Gonçalo Garcia (goncalo.garcia@feedzai.com)
 */

public interface Tracing {


    /**
     * Begins a new trace by creating a parentless span. This method should be used in situations where there is thread
     * reuse, whenever the entry point of the request is executed. Traces operations that return a value of any type.
     *
     *
     * <p>Similar to {@link Tracing#newTrace(Runnable, String)}, {@link Tracing#newTraceAsync(Supplier, String)} and
     * {@link Tracing#newTracePromise(Supplier, String)} but returning any value.
     *
     * @param toTrace     Lambda containing the code that should be wrapped in a trace.
     * @param description The description or name that best describes this operation.
     * @param <R>         The Return type of the traced code.
     * @return Returns whatever the traced code would have returned.
     */
    <R> R newTrace(Supplier<R> toTrace, String description);

    /**
     * Traces operations that do not return any values.
     *
     * <p>Similar to {@link Tracing#newTrace(Supplier, String)}, {@link Tracing#newTraceAsync(Supplier, String)} and
     * {@link Tracing#newTracePromise(Supplier, String)} but without returning any object.
     *
     * @param toTrace     Lambda containing the code that should be wrapped in a trace.
     * @param description The description or name that best describes this operation.
     */
    void newTrace(Runnable toTrace, String description);

    /**
     * Traces operations that are performed in the background and return a {@link CompletableFuture}, where tracing the
     * call does not trace the full execution.
     *
     * <p>Similar to {@link Tracing#newTrace(Supplier, String)}, {@link Tracing#newTrace(Runnable,
     * String)} and {@link Tracing#newTracePromise(Supplier, String)} but returning a {@link CompletableFuture}
     *
     * @param toTraceAsync Lambda containing the code that should be wrapped in a trace.
     * @param description  The description or name that best describes this operation.
     * @param <R>          The Return type of the traced code.
     * @return Returns the {@link CompletableFuture} the traced code would have returned.
     */
    <R> CompletableFuture<R> newTraceAsync(Supplier<CompletableFuture<R>> toTraceAsync, String description);

    /**
     * Traces operations that are performed in the background and return a {@link Promise}, where tracing the call does
     * not trace the full execution.
     *
     * <p>Similar to {@link Tracing#newTrace(Supplier, String)}, {@link Tracing#newTrace(Runnable, String)} and {@link
     * Tracing#newTraceAsync(Supplier, String)} but returning a {@link Promise}
     *
     * @param toTraceAsync Lambda containing the code that should be wrapped in a trace.
     * @param description  The description or name that best describes this operation.
     * @return Returns the {@link Promise} the traced code would've returned.
     */
    <R> Promise<R> newTracePromise(Supplier<Promise<R>> toTraceAsync, String description);


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
     * Traces operations that do not return any values.
     *
     * <p>Similar to {@link Tracing#addToTrace(Supplier, String)} and {@link Tracing#addToTraceAsync(Supplier, String)}
     * but without returning any object.
     *
     * @param toTrace     Lambda containing the code that should be wrapped in a trace.
     * @param description The description or name that best describes this operation.
     */
    void addToTrace(Runnable toTrace, String description);

    /**
     * Traces operations that are performed in the background and return a {@link CompletableFuture}, where tracing the
     * call does not trace the full execution.
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
     * Traces operations that are performed in the background and return a {@link Promise}, where tracing the call does
     * not trace the full execution.
     *
     * <p>Similar to {@link Tracing#addToTrace(Supplier, String)}, {@link Tracing#addToTrace(Runnable, String)} and
     * {@link Tracing#addToTraceAsync(Supplier, String)} but returning a {@link Promise}
     *
     * @param toTraceAsync Lambda containing the code that should be wrapped in a trace.
     * @param description  The description or name that best describes this operation.
     * @return Returns the {@link Promise} the traced code would've returned.
     */
    <R> Promise<R> addToTracePromise(Supplier<Promise<R>> toTraceAsync, String description);

    /**
     * Returns whether or not there is a currently active span.
     *
     * @return True if there is an active span, false otherwise.
     */
    boolean isActive();


}
