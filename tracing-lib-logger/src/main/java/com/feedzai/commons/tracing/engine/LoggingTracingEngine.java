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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;


/**
 * Implementation of all Tracing APIs that, instead of building an actual trace, simply logs calls to disk.
 *
 * @author Gonçalo Garcia (goncalo.garcia@feedzai.com)
 */
public class LoggingTracingEngine implements TracingEngine {


    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(LoggingTracingEngine.class.getName());

    /**
     * Null trace context to be used wherever is needed, since the logs won't take into account the causality between
     * calls.
     */
    private static final TraceContext TRACE_CONTEXT = () -> null;


    public LoggingTracingEngine() {
        logger.info("Probe for trace(timestamp:LONG, OPTIONAL(eventId:STRING), original_timestamp:LONG, latency:LONG, description:STRING) is ready.");
    }


    @Override
    public <R> Promise<R> addToTraceOpenPromise(final Supplier<Promise<R>> toTraceAsync, final Object object,
                                                final String description) {
        return logPromise(toTraceAsync, description, Optional.empty());

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
        logMessage(description, start, Optional.empty());
    }

    @Override
    public <R> R addToTraceOpen(final Supplier<R> toTraceAsync, final Object value, final String description) {
        final double start = System.nanoTime();
        final R result = toTraceAsync.get();
        logMessage(description, start, Optional.empty());
        return result;
    }

    @Override
    public <R> Promise<R> addToTraceOpenPromise(final Supplier<Promise<R>> toTraceAsync, final Object object,
                                                final String description, final String eventId) {
        return logPromise(toTraceAsync, description, Optional.empty());
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
        logMessage(description, start, Optional.empty());
    }

    @Override
    public <R> R addToTraceOpen(final Supplier<R> toTraceAsync, final Object value, final String description,
                                final String eventId) {
        final double start = System.nanoTime();
        final R result = toTraceAsync.get();
        logMessage(description, start, Optional.empty());
        return result;
    }

    @Override
    public void closeOpen(final Object object) {
        //Empty because it does not make sense to log this
    }

    @Override
    public <R> Promise<R> addToTraceOpenPromise(final Supplier<Promise<R>> toTraceAsync, final Object object,
                                                final String description, final TraceContext context) {
        return logPromise(toTraceAsync, description, Optional.empty());
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
        logMessage(description, start, Optional.empty());

    }

    @Override
    public <R> R addToTraceOpen(final Supplier<R> toTraceAsync, final Object value, final String description,
                                final TraceContext context) {
        final double start = System.nanoTime();
        final R result = toTraceAsync.get();
        logMessage(description, start, Optional.empty());
        return result;
    }

    @Override
    public <R> R newProcess(final Supplier<R> toTrace, final String description, final TraceContext context) {
        final double start = System.nanoTime();
        final R result = toTrace.get();
        logMessage(description, start, Optional.empty());
        return result;
    }

    @Override
    public void newProcess(final Runnable toTrace, final String description, final TraceContext context) {
        final double start = System.nanoTime();
        toTrace.run();
        logMessage(description, start, Optional.empty());
    }

    @Override
    public <R> Promise<R> newProcessPromise(final Supplier<Promise<R>> toTrace, final String description,
                                            final TraceContext context) {
        return logPromise(toTrace, description, Optional.empty());
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
        logMessage(description, start, Optional.empty());

        return result;
    }

    @Override
    public void addToTrace(final Runnable toTrace, final String description, final TraceContext context) {
        final double start = System.nanoTime();
        toTrace.run();
        logMessage(description, start, Optional.empty());

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
        return logPromise(toTraceAsync, description, Optional.empty());
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
    public TraceContext currentContextforObject(final Object obj) {
        return TRACE_CONTEXT;
    }

    @Override
    public <R> R newTrace(final Supplier<R> toTrace, final String description) {
        final double start = System.nanoTime();
        final R result = toTrace.get();
        logMessage(description, start, Optional.empty());
        return result;
    }

    @Override
    public void newTrace(final Runnable toTrace, final String description) {
        final double start = System.nanoTime();
        toTrace.run();
        logMessage(description, start, Optional.empty());
    }

    @Override
    public <R> CompletableFuture<R> newTraceAsync(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                  final String description) {
        return logFuture(toTraceAsync, description);
    }

    @Override
    public <R> Promise<R> newTracePromise(final Supplier<Promise<R>> toTraceAsync, final String description) {
        return logPromise(toTraceAsync, description, Optional.empty());
    }

    @Override
    public <R> R addToTrace(final Supplier<R> toTrace, final String description) {
        final double start = System.nanoTime();
        final R result = toTrace.get();
        logMessage(description, start, Optional.empty());
        return result;
    }

    @Override
    public void addToTrace(final Runnable toTrace, final String description) {
        final double start = System.nanoTime();
        toTrace.run();
        logMessage(description, start, Optional.empty());
    }

    @Override
    public <R> CompletableFuture<R> addToTraceAsync(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                    final String description) {
        return logFuture(toTraceAsync, description);
    }

    @Override
    public <R> Promise<R> addToTracePromise(final Supplier<Promise<R>> toTraceAsync, final String description) {
        return logPromise(toTraceAsync, description, Optional.empty());

    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public <R> R newTrace(final Supplier<R> toTrace, final String description, final String eventId) {
        final double start = System.nanoTime();
        final R result = toTrace.get();
        logMessage(description, start, Optional.of(eventId));
        return result;
    }

    @Override
    public void newTrace(final Runnable toTrace, final String description, final String eventId) {
        final double start = System.nanoTime();
        toTrace.run();
        logMessage(description, start, Optional.of(eventId));
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
        return logPromise(toTraceAsync, description, Optional.of(eventId));

    }

    @Override
    public <R> R newProcess(final Supplier<R> toTrace, final String description, final String eventId) {
        final double start = System.nanoTime();
        final R result = toTrace.get();
        logMessage(description, start, Optional.of(eventId));
        return result;
    }

    @Override
    public void newProcess(final Runnable toTrace, final String description, final String eventId) {
        final double start = System.nanoTime();
        toTrace.run();
        logMessage(description, start, Optional.of(eventId));
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
        return logPromise(toTrace, description, Optional.of(eventId));
    }

    @Override
    public <R> R addToTrace(final Supplier<R> toTrace, final String description, final String eventId) {
        final double start = System.nanoTime();
        final R result = toTrace.get();
        logMessage(description, start, Optional.of(eventId));
        return result;
    }

    @Override
    public void addToTrace(final Runnable toTrace, final String description, final String eventId) {
        final double start = System.nanoTime();
        toTrace.run();
        logMessage(description, start, Optional.of(eventId));
    }

    @Override
    public <R> CompletableFuture<R> addToTraceAsync(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                    final String description,
                                                    final String eventId) {
        final double start = System.nanoTime();
        final CompletableFuture<R> future = toTraceAsync.get();
        future.handle((f, t) -> {
            logMessage(description, start, Optional.of(eventId));
            return this;
        });
        return future;
    }

    @Override
    public <R> Promise<R> addToTracePromise(final Supplier<Promise<R>> toTraceAsync, final String description,
                                            final String eventId) {
        return logPromise(toTraceAsync, description, Optional.of(eventId));
    }

    /**
     * Logs the execution of a Promise.
     * @param toTraceAsync The code to be executed.
     * @param description A textual representation of the execution.
     * @param <R> The return type.
     * @return Whatever would be returned by the Promise.
     */
    private <R> Promise<R> logPromise(Supplier<Promise<R>> toTraceAsync, String description, Optional<String> eventId) {
        final double start = System.nanoTime();
        final Promise<R> promise = toTraceAsync.get();
        promise.onErrorPromise(throwable -> logMessage(description, start, eventId));
        promise.onCompletePromise(throwable -> logMessage(description, start, eventId));
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

    /**
     * Writes a log message in the required format.
     * @param description A textual representation of the execution.
     * @param start the starting timestamp of the execution.
     */
    private void logMessage(String description, double start, Optional<String> eventId) {
        final double end = System.nanoTime();
        final double latency = start - end;
        final String parsedEventId = eventId.map(s -> "," + s).orElse("");
        logger.trace("{}{},{},{}", start, parsedEventId, latency, description);
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
