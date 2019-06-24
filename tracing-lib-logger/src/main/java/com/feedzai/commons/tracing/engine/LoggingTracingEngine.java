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

package com.feedzai.commons.tracing.engine;import com.feedzai.commons.tracing.api.Promise;
import com.feedzai.commons.tracing.api.TraceContext;
import com.feedzai.commons.tracing.api.TracingOpen;
import com.feedzai.commons.tracing.api.TracingOpenWithContext;
import com.feedzai.commons.tracing.api.TracingOpenWithId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;


/**
 * Implementation of all Tracing APIs that, instead of building an actual trace, simply logs calls to disk.
 *
 * @author Gonçalo Garcia (goncalo.garcia@feedzai.com)
 */
public class LoggingTracingEngine implements TracingOpenWithContext, TracingOpen, TracingOpenWithId {


    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(LoggingTracingEngine.class.getName());

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


    public LoggingTracingEngine() {
        logger.info("Probe for trace(description:STRING, timestamp:LONG, original_timestamp:LONG, latency:LONG) is ready.");
    }


    @Override
    public <R> Promise<R> addToTraceOpenPromise(final Supplier<Promise<R>> toTraceAsync, final Object object,
                                                final String description) {
        return logPromise(toTraceAsync, description);

    }

    @Override
    public <R> CompletableFuture<R> addToTraceOpenFuture(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                         final Object object, final String description) {
        return logFuture(toTraceAsync, description);
    }

    private <R> CompletableFuture<R> logFuture(Supplier<CompletableFuture<R>> toTraceAsync, String description) {
        return logFuture(toTraceAsync, description);

    }

    @Override
    public void addToTraceOpen(final Runnable toTraceAsync, final Object object, final String description) {
        final double start = System.nanoTime();
        toTraceAsync.run();
        logMessage(description, start);
    }

    @Override
    public <R> R addToTraceOpen(final Supplier<R> toTraceAsync, final Object value, final String description) {
        final double start = System.nanoTime();
        final R result = toTraceAsync.get();
        logMessage(description, start);
        return result;
    }

    @Override
    public <R> Promise<R> addToTraceOpenPromise(final Supplier<Promise<R>> toTraceAsync, final Object object,
                                                final String description, final String eventId) {
        return logPromise(toTraceAsync, description);
    }

    @Override
    public <R> CompletableFuture<R> addToTraceOpenFuture(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                         final Object object, final String description,
                                                         final String eventId) {
        return logFuture(toTraceAsync, description);

    }

    @Override
    public void addToTraceOpen(final Runnable toTraceAsync, final Object object, final String description,
                               final String eventId) {
        final double start = System.nanoTime();
        toTraceAsync.run();
        logMessage(description, start);
    }

    @Override
    public <R> R addToTraceOpen(final Supplier<R> toTraceAsync, final Object value, final String description,
                                final String eventId) {
        final double start = System.nanoTime();
        final R result = toTraceAsync.get();
        logMessage(description, start);
        return result;
    }

    @Override
    public void closeOpen(final Object object) {
        //Empty because it does not make sense to log this
    }

    @Override
    public <R> Promise<R> addToTraceOpenPromise(final Supplier<Promise<R>> toTraceAsync, final Object object,
                                                final String description, final TraceContext context) {
        return logPromise(toTraceAsync, description);
    }

    @Override
    public <R> CompletableFuture<R> addToTraceOpenFuture(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                         final Object object, final String description,
                                                         final TraceContext context) {
        return logFuture(toTraceAsync, description);

    }

    @Override
    public void addToTraceOpen(final Runnable toTraceAsync, final Object object, final String description,
                               final TraceContext context) {
        final double start = System.nanoTime();
        toTraceAsync.run();
        logMessage(description, start);

    }

    @Override
    public <R> R addToTraceOpen(final Supplier<R> toTraceAsync, final Object value, final String description,
                                final TraceContext context) {
        final double start = System.nanoTime();
        final R result = toTraceAsync.get();
        logMessage(description, start);
        return result;
    }

    @Override
    public <R> R newProcess(final Supplier<R> toTrace, final String description, final TraceContext context) {
        final double start = System.nanoTime();
        final R result = toTrace.get();
        logMessage(description, start);
        return result;
    }

    @Override
    public void newProcess(final Runnable toTrace, final String description, final TraceContext context) {
        final double start = System.nanoTime();
        toTrace.run();
        logMessage(description, start);
    }

    @Override
    public <R> Promise<R> newProcessPromise(final Supplier<Promise<R>> toTrace, final String description,
                                            final TraceContext context) {
        return logPromise(toTrace, description);
    }


    @Override
    public <R> CompletableFuture<R> newProcessFuture(final Supplier<CompletableFuture<R>> toTrace,
                                                     final String description, final TraceContext context) {
        return logFuture(toTrace, description);

    }

    @Override
    public <R> R addToTrace(final Supplier<R> toTrace, final String description, final TraceContext context) {
        final double start = System.nanoTime();
        final R result = toTrace.get();
        logMessage(description, start);

        return result;
    }

    @Override
    public void addToTrace(final Runnable toTrace, final String description, final TraceContext context) {
        final double start = System.nanoTime();
        toTrace.run();
        logMessage(description, start);

    }

    @Override
    public <R> CompletableFuture<R> addToTraceAsync(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                    final String description,
                                                    final TraceContext context) {
        return logFuture(toTraceAsync, description);
    }

    @Override
    public <R> Promise<R> addToTracePromise(final Supplier<Promise<R>> toTraceAsync, final String description,
                                            final TraceContext context) {
        return logPromise(toTraceAsync, description);
    }

    @Override
    public Serializable serializeContext() {
        return "";
    }

    @Override
    public TraceContext deserializeContext(final Serializable headers) {
        return TRACE_CONTEXT;
    }

    @Override
    public TraceContext currentContext() {
        return TRACE_CONTEXT;
    }

    @Override
    public TraceContext currentContextforObject(final Object obj) {
        return TRACE_CONTEXT;
    }

    @Override
    public <R> R newTrace(final Supplier<R> toTrace, final String description) {
        final double start = System.nanoTime();
        final R result = toTrace.get();
        logMessage(description, start);
        return result;
    }

    @Override
    public void newTrace(final Runnable toTrace, final String description) {
        final double start = System.nanoTime();
        toTrace.run();
        logMessage(description, start);
    }

    @Override
    public <R> CompletableFuture<R> newTraceAsync(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                  final String description) {
        return logFuture(toTraceAsync, description);
    }

    @Override
    public <R> Promise<R> newTracePromise(final Supplier<Promise<R>> toTraceAsync, final String description) {
        return logPromise(toTraceAsync, description);
    }

    @Override
    public <R> R addToTrace(final Supplier<R> toTrace, final String description) {
        final double start = System.nanoTime();
        final R result = toTrace.get();
        logMessage(description, start);
        return result;
    }

    @Override
    public void addToTrace(final Runnable toTrace, final String description) {
        final double start = System.nanoTime();
        toTrace.run();
        logMessage(description, start);
    }

    @Override
    public <R> CompletableFuture<R> addToTraceAsync(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                    final String description) {
        return logFuture(toTraceAsync, description);
    }

    @Override
    public <R> Promise<R> addToTracePromise(final Supplier<Promise<R>> toTraceAsync, final String description) {
        return logPromise(toTraceAsync, description);

    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public <R> R newTrace(final Supplier<R> toTrace, final String description, final String eventId) {
        final double start = System.nanoTime();
        final R result = toTrace.get();
        logMessage(description, start);
        return result;
    }

    @Override
    public void newTrace(final Runnable toTrace, final String description, final String eventId) {
        final double start = System.nanoTime();
        toTrace.run();
        logMessage(description, start);
    }

    @Override
    public <R> CompletableFuture<R> newTraceAsync(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                  final String description,
                                                  final String eventId) {
        return logFuture(toTraceAsync, description);
    }

    @Override
    public <R> Promise<R> newTracePromise(final Supplier<Promise<R>> toTraceAsync, final String description,
                                          final String eventId) {
        return logPromise(toTraceAsync, description);

    }

    @Override
    public <R> R newProcess(final Supplier<R> toTrace, final String description, final String eventId) {
        final double start = System.nanoTime();
        final R result = toTrace.get();
        logMessage(description, start);
        return result;
    }

    @Override
    public void newProcess(final Runnable toTrace, final String description, final String eventId) {
        final double start = System.nanoTime();
        toTrace.run();
        logMessage(description, start);
    }

    @Override
    public <R> CompletableFuture newProcessFuture(final Supplier<CompletableFuture<R>> toTrace,
                                                  final String description,
                                                  final String eventId) {
        return logFuture(toTrace, description);
    }

    @Override
    public <R> Promise<R> newProcessPromise(final Supplier<Promise<R>> toTrace, final String description,
                                            final String eventId) {
        return logPromise(toTrace, description);
    }

    @Override
    public <R> R addToTrace(final Supplier<R> toTrace, final String description, final String eventId) {
        final double start = System.nanoTime();
        final R result = toTrace.get();
        logMessage(description, start);
        return result;
    }

    @Override
    public void addToTrace(final Runnable toTrace, final String description, final String eventId) {
        final double start = System.nanoTime();
        toTrace.run();
        logMessage(description, start);
    }

    @Override
    public <R> CompletableFuture<R> addToTraceAsync(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                    final String description,
                                                    final String eventId) {
        final double start = System.nanoTime();
        final CompletableFuture<R> future = toTraceAsync.get();
        future.handle((f, t) -> {
            logMessage(description, start);
            return this;
        });
        return future;
    }

    @Override
    public <R> Promise<R> addToTracePromise(final Supplier<Promise<R>> toTraceAsync, final String description,
                                            final String eventId) {
        return logPromise(toTraceAsync, description);
    }

    private <R> Promise<R> logPromise(Supplier<Promise<R>> toTraceAsync, String description) {
        final double start = System.nanoTime();
        final Promise<R> promise = toTraceAsync.get();
        promise.onErrorPromise(throwable -> logMessage(description, start));
        promise.onCompletePromise(throwable -> logMessage(description, start));
        return toTraceAsync.get();
    }

    @Override
    public TraceContext currentContextforId(final String eventId) {
        return TRACE_CONTEXT;
    }

    @Override
    public boolean traceHasStarted(final String eventId) {
        return true;
    }

    private void logMessage(String description, double start) {
        final double end = System.nanoTime();
        final double latency = start - end;
        logger.info("trace,{},{},{}ms", description, end, start, latency / 1000);
    }

}
