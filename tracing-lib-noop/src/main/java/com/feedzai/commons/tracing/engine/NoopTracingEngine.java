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

package com.feedzai.commons.tracing.engine;import com.feedzai.commons.tracing.api.Promise;
import com.feedzai.commons.tracing.api.TraceContext;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.noop.NoopSpan;
import io.opentracing.noop.NoopTracerFactory;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Implementation of all Tracing APIs that does nothing so as not to affect the execution of the application.
 *
 * @author Gonçalo Garcia (goncalo.garcia@feedzai.com)
 */
public class NoopTracingEngine implements TracingEngine {

    /**
     * Null trace context to be used wherever is needed, since the logs won't take into account the causality between
     * calls.
     */
    public static final TraceContext TRACE_CONTEXT = new TraceContext() {
        @Override
        public Object get() {
            return null;
        }
    };

    @Override
    public <R> Promise<R> addToTraceOpenPromise(Supplier<Promise<R>> toTraceAsync, Object object,
                                                String description) {
        return toTraceAsync.get();
    }

    @Override
    public <R> CompletableFuture<R> addToTraceOpenFuture(Supplier<CompletableFuture<R>> toTraceAsync,
                                                         Object object, String description) {
        return toTraceAsync.get();
    }

    @Override
    public void addToTraceOpen(Runnable toTraceAsync, Object object, String description) {
        toTraceAsync.run();
    }

    @Override
    public <R> R addToTraceOpen(Supplier<R> toTraceAsync, Object value, String description) {
        return toTraceAsync.get();
    }

    @Override
    public <R> Promise<R> addToTraceOpenPromise(Supplier<Promise<R>> toTraceAsync, Object object,
                                                String description, String eventId) {
        return toTraceAsync.get();
    }

    @Override
    public <R> CompletableFuture<R> addToTraceOpenFuture(Supplier<CompletableFuture<R>> toTraceAsync,
                                                         Object object, String description,
                                                         String eventId) {
        return toTraceAsync.get();
    }

    @Override
    public void addToTraceOpen(Runnable toTraceAsync, Object object, String description,
                               String eventId) {
        toTraceAsync.run();

    }

    @Override
    public <R> R addToTraceOpen(Supplier<R> toTraceAsync, Object value, String description,
                                String eventId) {
        return toTraceAsync.get();
    }

    @Override
    public void closeOpen(Object object) {

    }

    @Override
    public <R> Promise<R> addToTraceOpenPromise(Supplier<Promise<R>> toTraceAsync, Object object,
                                                String description, TraceContext context) {
        return toTraceAsync.get();
    }

    @Override
    public <R> CompletableFuture<R> addToTraceOpenFuture(Supplier<CompletableFuture<R>> toTraceAsync,
                                                         Object object, String description,
                                                         TraceContext context) {
        return toTraceAsync.get();
    }

    @Override
    public void addToTraceOpen(Runnable toTraceAsync, Object object, String description,
                               TraceContext context) {
        toTraceAsync.run();

    }

    @Override
    public <R> R addToTraceOpen(Supplier<R> toTraceAsync, Object value, String description,
                                TraceContext context) {
        return toTraceAsync.get();
    }

    @Override
    public <R> R newProcess(Supplier<R> toTrace, String description, TraceContext context) {
        return toTrace.get();
    }

    @Override
    public void newProcess(Runnable toTrace, String description, TraceContext context) {
        toTrace.run();

    }

    @Override
    public <R> Promise<R> newProcessPromise(Supplier<Promise<R>> toTrace, String description,
                                            TraceContext context) {
        return toTrace.get();
    }

    @Override
    public <R> CompletableFuture<R> newProcessFuture(Supplier<CompletableFuture<R>> toTrace,
                                                     String description, TraceContext context) {
        return toTrace.get();
    }

    @Override
    public <R> R addToTrace(Supplier<R> toTrace, String description, TraceContext context) {
        return toTrace.get();
    }

    @Override
    public void addToTrace(Runnable toTrace, String description, TraceContext context) {
        toTrace.run();

    }

    @Override
    public <R> CompletableFuture<R> addToTraceAsync(Supplier<CompletableFuture<R>> toTraceAsync, String description,
                                                    TraceContext context) {
        return toTraceAsync.get();
    }

    @Override
    public <R> Promise<R> addToTracePromise(Supplier<Promise<R>> toTraceAsync, String description,
                                            TraceContext context) {
        return toTraceAsync.get();
    }

    @Override
    public Map<String, String> serializeContext() {
        return new HashMap<>();
    }

    @Override
    public TraceContext deserializeContext(final Map<String, String> headers) {
        return TRACE_CONTEXT;
    }

    @Override
    public TraceContext currentContext() {
        return TRACE_CONTEXT;
    }

    @Override
    public TraceContext currentContextforObject(Object obj) {
        return TRACE_CONTEXT;
    }

    @Override
    public <R> R newTrace(Supplier<R> toTrace, String description) {
        return toTrace.get();
    }

    @Override
    public void newTrace(Runnable toTrace, String description) {
        toTrace.run();
    }

    @Override
    public <R> CompletableFuture<R> newTraceAsync(Supplier<CompletableFuture<R>> toTraceAsync, String description) {
        return toTraceAsync.get();
    }

    @Override
    public <R> Promise<R> newTracePromise(Supplier<Promise<R>> toTraceAsync, String description) {
        return toTraceAsync.get();
    }

    @Override
    public <R> R addToTrace(Supplier<R> toTrace, String description) {
        return toTrace.get();
    }

    @Override
    public void addToTrace(Runnable toTrace, String description) {
        toTrace.run();

    }

    @Override
    public <R> CompletableFuture<R> addToTraceAsync(Supplier<CompletableFuture<R>> toTraceAsync, String description) {
        return toTraceAsync.get();
    }

    @Override
    public <R> Promise<R> addToTracePromise(Supplier<Promise<R>> toTraceAsync, String description) {
        return toTraceAsync.get();
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public <R> R newTrace(Supplier<R> toTrace, String description, String eventId) {
        return toTrace.get();
    }

    @Override
    public void newTrace(Runnable toTrace, String description, String eventId) {
        toTrace.run();

    }

    @Override
    public <R> CompletableFuture<R> newTraceAsync(Supplier<CompletableFuture<R>> toTraceAsync, String description,
                                                  String eventId) {
        return toTraceAsync.get();
    }

    @Override
    public <R> Promise<R> newTracePromise(Supplier<Promise<R>> toTraceAsync, String description, String eventId) {
        return toTraceAsync.get();
    }

    @Override
    public <R> R newProcess(Supplier<R> toTrace, String description, String eventId) {
        return toTrace.get();
    }

    @Override
    public void newProcess(Runnable toTrace, String description, String eventId) {
        toTrace.run();

    }

    @Override
    public <R> CompletableFuture<R> newProcessFuture(Supplier<CompletableFuture<R>> toTrace, String description,
                                                     String eventId) {
        return toTrace.get();
    }

    @Override
    public <R> Promise<R> newProcessPromise(Supplier<Promise<R>> toTrace, String description, String eventId) {
        return toTrace.get();
    }

    @Override
    public <R> R addToTrace(Supplier<R> toTrace, String description, String eventId) {
        return toTrace.get();
    }

    @Override
    public void addToTrace(Runnable toTrace, String description, String eventId) {
        toTrace.run();

    }

    @Override
    public <R> CompletableFuture<R> addToTraceAsync(Supplier<CompletableFuture<R>> toTraceAsync, String description,
                                                    String eventId) {
        return toTraceAsync.get();
    }

    @Override
    public <R> Promise<R> addToTracePromise(Supplier<Promise<R>> toTraceAsync, String description, String eventId) {
        return toTraceAsync.get();
    }

    @Override
    public TraceContext currentContextforId(String eventId) {
        return TRACE_CONTEXT;
    }

    @Override
    public boolean traceHasStarted(String eventId) {
        return false;
    }

    @Override
    public Tracer getTracer() {
        return NoopTracerFactory.create();
    }

    @Override
    public Span currentSpan() {
        return NoopSpan.INSTANCE;
    }
}
