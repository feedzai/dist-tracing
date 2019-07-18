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

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;


/**
 * Specifies an API for Tracing executions that cannot be encapsulated into a single lambda or method and there is an
 * application specific eventId that is used propagate the tracing context out-of-band. It should be used when the start
 * and end point of a span are in different methods.
 *
 * @author Gonçalo Garcia (goncalo.garcia@feedzai.com)
 */
public interface TracingOpenWithId extends TracingWithId {


    /**
     * Begins a new span that must be finished manually in the future. To do this, an object that is present when the
     * span is created and later closed is associated to the span so that it can be retrieved later.
     *
     * <p>Similar to {@link TracingOpenWithId#addToTraceOpen(Runnable, Object, String, String)}, {@link
     * TracingOpenWithId#addToTraceOpen(Supplier, Object, String, String)} and {@link
     * TracingOpenWithId#addToTraceOpenFuture(Supplier, Object, String, String)} but returning a Promise object.
     *
     * @param <R>          The return type.
     * @param toTraceAsync The code that should be traced.
     * @param description  The description/name of the new context.
     * @param object       A uniquely identifying object that can be matched to this span and used to retrieve it when
     *                     it is time to finish it.
     * @param eventId      The ID that represents a request throughout the whole execution.
     * @param <P>          The class implementing {@link Promise}
     * @return What was to be returned by the traced code.
     */
    <P extends Promise<R,P>, R> P addToTraceOpenPromise(final Supplier<P> toTraceAsync, final Object object,
                                      final String description,
                                      final String eventId);

    /**
     * Begins a new span that must be finished manually in the future.
     *
     * <p>Similar to {@link TracingOpenWithId#addToTraceOpen(Runnable, Object, String, String)}, {@link
     * TracingOpenWithId#addToTraceOpen(Supplier, Object, String, String)} and {@link
     * TracingOpenWithId#addToTraceOpenPromise(Supplier, Object, String, String)} but returning a CompletableFuture
     * object.
     *
     * @param <R>          The return type.
     * @param toTraceAsync The code that should be traced.
     * @param description  The description/name of the new context.
     * @param object       A uniquely identifying object that can be matched to this span and used to retrieve it when
     *                     it is time to finish it.
     * @param eventId      The ID that represents a request throughout the whole execution.
     * @return What was to be returned by the traced code.
     */
    <R> CompletableFuture<R> addToTraceOpenFuture(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                  final Object object,
                                                  final String description,
                                                  final String eventId);

    /**
     * Begins a new span that must be finished manually in the future.
     *
     * <p>Similar to {@link TracingOpenWithId#addToTraceOpenFuture(Supplier, Object, String, String)}, {@link
     * TracingOpenWithId#addToTraceOpen(Supplier, Object, String, String)} and {@link
     * TracingOpenWithId#addToTraceOpenPromise(Supplier, Object, String, String)} but returning nothing.
     *
     * @param toTraceAsync The code that should be traced.
     * @param object       A uniquely identifying object that can be matched to this span and used to retrieve it when
     *                     it is time to finish it.
     * @param description  The description/name of the new context.
     * @param eventId      The ID that represents a request throughout the whole execution.
     */
    void addToTraceOpen(final Runnable toTraceAsync, final Object object, final String description,
                        final String eventId);

    /**
     * Begins a new span that must be finished manually in the future.
     *
     * <p>Similar to {@link TracingOpenWithId#addToTraceOpenFuture(Supplier, Object, String, String)}, {@link
     * TracingOpenWithId#addToTraceOpen(Runnable, Object, String, String)} and {@link
     * TracingOpenWithId#addToTraceOpenPromise(Supplier, Object, String, String)} but returning any object.
     *
     * @param <R>          The return type.
     * @param toTraceAsync The code that should be traced.
     * @param description  The description/name of the new context.
     * @param value        The value.
     * @param eventId      The ID that represents a request throughout the whole execution.
     * @return What was to be returned by the traced code.
     */
    <R> R addToTraceOpen(final Supplier<R> toTraceAsync, final Object value, final String description,
                         final String eventId);

    /**
     * Closes a span that has been left open.
     *
     * @param object A uniquely identifying object that can be matched to this span.
     */
    void closeOpen(final Object object);
}
