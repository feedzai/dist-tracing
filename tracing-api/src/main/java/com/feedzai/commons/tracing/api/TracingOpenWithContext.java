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
 * Specifies an API for Tracing executions that cannot be encapsulated into a single lambda or method and there is a
 * need for explicitly propagating context (for example, to trace across process boundaries). It should be used when the
 * start and end point of a span are in different methods.
 *
 * @author Gonçalo Garcia (goncalo.garcia@feedzai.com)
 */
public interface TracingOpenWithContext extends TracingWithContext {



    /**
     * Begins a new span that must be finished manually in the future. To do this, an object that is present when the
     * span is created and later closed is associated to the span so that it can be retrieved later.
     *
     * <p>Similar to {@link TracingOpenWithContext#addToTraceOpen(Runnable, Object, String, TraceContext)}, {@link
     * TracingOpenWithContext#addToTraceOpen(Supplier, Object, String, TraceContext)} and {@link
     * TracingOpenWithContext#addToTraceOpenFuture(Supplier, Object, String, TraceContext)} but returning a Promise
     * object.
     *
     * @param <R>          The return type.
     * @param toTraceAsync The code that should be traced.
     * @param description  The description/name of the new context.
     * @param object       A uniquely identifying object that can be matched to this span and used to retrieve it when
     *                     it is time to finish it.
     * @param context      Represents the context of the current execution.
     * @return What was to be returned by the traced code.
     */
    <R> Promise<R> addToTraceOpenPromise(final Supplier<Promise<R>> toTraceAsync, final Object object,
                                      final String description,
                                      final TraceContext context);

    /**
     * Begins a new span that must be finished manually in the future.
     *
     * <p>Similar to {@link TracingOpenWithContext#addToTraceOpen(Runnable, Object, String, TraceContext)}, {@link
     * TracingOpenWithContext#addToTraceOpen(Supplier, Object, String, TraceContext)} and {@link
     * TracingOpenWithContext#addToTraceOpenPromise(Supplier, Object, String, TraceContext)} but returning a
     * CompletableFuture object.
     *
     * @param <R>          The return type.
     * @param toTraceAsync The code that should be traced.
     * @param description  The description/name of the new context.
     * @param object       A uniquely identifying object that can be matched to this span and used to retrieve it when
     *                     it is time to finish it.
     * @param context      Represents the context of the current execution.
     * @return What was to be returned by the traced code.
     */
    <R> CompletableFuture<R> addToTraceOpenFuture(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                  final Object object,
                                                  final String description,
                                                  final TraceContext context);


    /**
     * Begins a new span that must be finished manually in the future.
     *
     * <p>Similar to {@link TracingOpenWithContext#addToTraceOpenFuture(Supplier, Object, String, TraceContext)},
     * {@link TracingOpenWithContext#addToTraceOpen(Supplier, Object, String, TraceContext)} and {@link
     * TracingOpenWithContext#addToTraceOpenPromise(Supplier, Object, String, TraceContext)} but returning nothing.
     *
     * @param toTraceAsync The code that should be traced.
     * @param object       A uniquely identifying object that can be matched to this span and used to retrieve it when
     *                     it is time to finish it.
     * @param description  The description/name of the new context.
     * @param context      Represents the context of the current execution.
     */
    void addToTraceOpen(final Runnable toTraceAsync, final Object object, final String description,
                        final TraceContext context);

    /**
     * Begins a new span that must be finished manually in the future.
     *
     * <p>Similar to {@link TracingOpenWithContext#addToTraceOpenFuture(Supplier, Object, String, TraceContext)},
     * {@link TracingOpenWithContext#addToTraceOpen(Runnable, Object, String, TraceContext)} and {@link
     * TracingOpenWithContext#addToTraceOpenPromise(Supplier, Object, String, TraceContext)} but returning any object.
     *
     * @param <R>          The return type.
     * @param toTraceAsync The code that should be traced.
     * @param description  The description/name of the new context.
     * @param value        The value.
     * @param context      Represents the context of the current execution.
     * @return What was to be returned by the traced code.
     */
    <R> R addToTraceOpen(final Supplier<R> toTraceAsync, final Object value, final String description,
                         final TraceContext context);
}
