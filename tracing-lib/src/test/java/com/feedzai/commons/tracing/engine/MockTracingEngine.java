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

package com.feedzai.commons.tracing.engine;

import com.feedzai.commons.tracing.api.Promise;
import com.feedzai.commons.tracing.api.TraceContext;
import com.feedzai.commons.tracing.engine.configuration.BaseConfiguration;
import io.opentracing.Span;
import io.opentracing.Tracer;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class MockTracingEngine extends AbstractOpenTracingEngine {

    /**
     * Constructor for this abstract class to be called by the extension classes to supply the implementation specific
     * parameters.
     *
     * @param tracer        The Tracer implementation of the underlying tracing Engine.
     * @param configuration The configuration parameters for the caches.
     */
    MockTracingEngine(Tracer tracer,
                      BaseConfiguration configuration) {
        super(tracer, configuration);
    }

    @Override
    protected String getTraceIdFromSpan(Span span) {
        return "1:1:1";
    }

    @Override
    public <E extends Throwable, P extends Promise<R, P, E>, R> P addToTraceOpenPromise(Supplier<P> toTraceAsync, Object object,
                                             String description, String eventId) {
        return null;
    }

    @Override
    public <R> CompletableFuture<R> addToTraceOpenFuture(Supplier<CompletableFuture<R>> toTraceAsync,
                                                         Object object, String description,
                                                         String eventId) {
        return null;
    }

    @Override
    public void addToTraceOpen(Runnable toTraceAsync, Object object, String description,
                               String eventId) {

    }

    @Override
    public <R> R addToTraceOpen(Supplier<R> toTraceAsync, Object value, String description,
                                String eventId) {
        return null;
    }



    @Override
    public Map<String, String> serializeContext() {
        return null;
    }

    @Override
    public TraceContext deserializeContext(Map<String, String> headers) {
        return null;
    }

    @Override
    public <R> R newTrace(Supplier<R> toTrace, String description, String eventId) {
        return null;
    }

    @Override
    public void newTrace(Runnable toTrace, String description, String eventId) {

    }

    @Override
    public <R> CompletableFuture<R> newTraceAsync(Supplier<CompletableFuture<R>> toTraceAsync, String description,
                                                  String eventId) {
        return null;
    }

    @Override
    public <E extends Throwable, P extends Promise<R, P, E>, R> P newTracePromise(Supplier<P> toTraceAsync, String description, String eventId) {
        return null;
    }

    @Override
    public <R> R newProcess(Supplier<R> toTrace, String description, String eventId) {
        return null;
    }

    @Override
    public void newProcess(Runnable toTrace, String description, String eventId) {

    }

    @Override
    public <R> CompletableFuture<R> newProcessFuture(Supplier<CompletableFuture<R>> toTrace, String description,
                                              String eventId) {
        return null;
    }

    @Override
    public <E extends Throwable, P extends Promise<R, P, E>, R> P newProcessPromise(Supplier<P> toTrace, String description, String eventId) {
        return null;
    }

    @Override
    public <R> R addToTrace(Supplier<R> toTrace, String description, String eventId) {
        return null;
    }

    @Override
    public void addToTrace(Runnable toTrace, String description, String eventId) {

    }

    @Override
    public <R> CompletableFuture<R> addToTraceAsync(Supplier<CompletableFuture<R>> toTraceAsync, String description,
                                                    String eventId) {
        return null;
    }

    @Override
    public <E extends Throwable, P extends Promise<R, P, E>, R> P addToTracePromise(Supplier<P> toTraceAsync, String description, String eventId) {
        return null;
    }

    @Override
    public TraceContext currentContextforId(String eventId) {
        return null;
    }

    @Override
    public boolean traceHasStarted(String eventId) {
        return false;
    }

    @Override
    public Tracer getTracer() {
        return null;
    }

    @Override
    public Span currentSpan() {
        return null;
    }
}
