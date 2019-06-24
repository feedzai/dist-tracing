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

import com.feedzai.commons.tracing.api.Promise;
import com.feedzai.commons.tracing.api.TraceContext;
import com.feedzai.commons.tracing.api.TracingOpen;
import com.feedzai.commons.tracing.api.TracingOpenWithContext;
import com.feedzai.commons.tracing.api.TracingOpenWithId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LoggingTracingEngine implements TracingOpenWithContext, TracingOpen, TracingOpenWithId {


    /**
     * The logger.
     */
    static final Logger logger = LoggerFactory.getLogger(LoggingTracingEngine.class.getName());


    @Override
    public <R> Promise<R> addToTraceOpenPromise(final Supplier<Promise<R>> toTraceAsync, final Object object,
                                                final String description) {
        final double start = System.nanoTime();
        final Promise result = toTraceAsync.get().onCompletePromise(promise -> {
            logger.info("Starting traced block for: {} associated to {} in {} ms", description, object, (start - System.nanoTime()) / 1000);
        });
        return result;
    }

    @Override
    public <R> CompletableFuture<R> addToTraceOpenFuture(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                         final Object object, final String description) {
        final double start = System.nanoTime();
        final CompletableFuture<R> result = toTraceAsync.get();
        result.handle((f, t) -> {
            logger.info("Starting traced block for: {} associated to {} in {} ms", description, object, (start - System.nanoTime()) / 1000);
            return this;
        });
        return result;
    }

    @Override
    public void addToTraceOpen(final Runnable toTraceAsync, final Object object, final String description) {
        final double start = System.nanoTime();
        toTraceAsync.run();
        logger.info("Starting traced block for: {} associated to {} in {} ms", description, object, (start - System.nanoTime()) / 1000);
    }

    @Override
    public <R> R addToTraceOpen(final Supplier<R> toTraceAsync, final Object value, final String description) {
        final double start = System.nanoTime();
        final R result = toTraceAsync.get();
        logger.info("Starting traced block for: {} associated to {} in {} ms", description, value, (start - System.nanoTime()) / 1000);
        return result;
    }

    @Override
    public <R> Promise<R> addToTraceOpenPromise(final Supplier<Promise<R>> toTraceAsync, final Object object,
                                                final String description, final String eventId) {
        final double start = System.nanoTime();
        final Promise result = toTraceAsync.get().onCompletePromise(promise -> {
            logger.info("Starting traced block for: {} associated to {} in {} ms", description, object, (start - System.nanoTime()) / 1000);
        });
        return result;
    }

    @Override
    public <R> CompletableFuture<R> addToTraceOpenFuture(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                         final Object object, final String description,
                                                         final String eventId) {
        final double start = System.nanoTime();
        final CompletableFuture<R> result = toTraceAsync.get();
        result.handle((f, t) -> {
            logger.info("Starting traced block for: {} associated to {} in {} ms", description, object, (start - System.nanoTime()) / 1000);
            return this;
        });
        return result;
    }

    @Override
    public void addToTraceOpen(final Runnable toTraceAsync, final Object object, final String description,
                               final String eventId) {
        final double start = System.nanoTime();
        toTraceAsync.run();
        logger.info("Starting traced block for: {} associated to {} in {} ms", description, object, (start - System.nanoTime()) / 1000);
    }

    @Override
    public <R> R addToTraceOpen(final Supplier<R> toTraceAsync, final Object value, final String description,
                                final String eventId) {
        final double start = System.nanoTime();
        final R result = toTraceAsync.get();
        logger.info("Starting traced block for: {} associated to {} in {} ms", description, value, (start - System.nanoTime()) / 1000);
        return result;
    }

    @Override
    public void closeOpen(final Object object) {
        logger.info("Closing traced block associated to {}", object);
    }

    @Override
    public <R> Promise<R> addToTraceOpenPromise(final Supplier<Promise<R>> toTraceAsync, final Object object,
                                                final String description, final TraceContext context) {
        final double start = System.nanoTime();
        final Promise result = toTraceAsync.get().onCompletePromise(promise -> {
            logger.info("Starting traced block for: {} associated to {} in {} ms", description, object, (start - System.nanoTime()) / 1000);
        });
        return result;
    }

    @Override
    public <R> CompletableFuture<R> addToTraceOpenFuture(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                         final Object object, final String description,
                                                         final TraceContext context) {
        final double start = System.nanoTime();
        final CompletableFuture<R> result = toTraceAsync.get();
        result.handle((f, t) -> {
            logger.info("Starting traced block for: {} associated to {} in {} ms", description, object, (start - System.nanoTime()) / 1000);
            return this;
        });
        return result;
    }

    @Override
    public void addToTraceOpen(final Runnable toTraceAsync, final Object object, final String description,
                               final TraceContext context) {
        final double start = System.nanoTime();
        toTraceAsync.run();
        logger.info("Starting traced block for: {} associated to {} in {} ms", description, object, (start - System.nanoTime()) / 1000);

    }

    @Override
    public <R> R addToTraceOpen(final Supplier<R> toTraceAsync, final Object value, final String description,
                                final TraceContext context) {
        final double start = System.nanoTime();
        final R result = toTraceAsync.get();
        logger.info("Starting traced block for: {} associated to {} in {} ms", description, value, (start - System.nanoTime()) / 1000);
        return result;
    }

    @Override
    public <R> R newProcess(final Supplier<R> toTrace, final String description, final TraceContext context) {
        final double start = System.nanoTime();
        final R result = toTrace.get();
        logger.info("New Process {}, duration {} ms", description, (start - System.nanoTime()) / 1000);
        return result;
    }

    @Override
    public void newProcess(final Runnable toTrace, final String description, final TraceContext context) {
        final double start = System.nanoTime();
        toTrace.run();
        logger.info("New Process {}, duration {} ms", description, (start - System.nanoTime()) / 1000);
    }

    @Override
    public <R> Promise<R> newProcessPromise(final Supplier<Promise<R>> toTrace, final String description,
                                            final TraceContext context) {
        final double start = System.nanoTime();
        final Promise<R> promise = toTrace.get();
        promise.onErrorPromise(throwable -> logger.info("New Process {}, duration {} ms", description, (start - System.nanoTime()) / 1000));
        promise.onCompletePromise(throwable -> logger.info("New Process {}, duration {} ms", description, (start - System.nanoTime()) / 1000));
        return toTrace.get();
    }

    @Override
    public <R> CompletableFuture<R> newProcessFuture(final Supplier<CompletableFuture<R>> toTrace,
                                                     final String description, final TraceContext context) {
        final double start = System.nanoTime();
        final CompletableFuture<R> result = toTrace.get();
        result.handle((f,t) -> {
            logger.info("New Process {}, duration {} ms", description, (start - System.nanoTime()) / 1000);
            return this;
        });
        return toTrace.get();
    }

    @Override
    public <R> R addToTrace(final Supplier<R> toTrace, final String description, final TraceContext context) {
        final double start = System.nanoTime();
        final R result = toTrace.get();
        logger.info("Add To Trace {}, duration {} ms", description, (start - System.nanoTime()) / 1000);
        return result;
    }

    @Override
    public void addToTrace(final Runnable toTrace, final String description, final TraceContext context) {
        final double start = System.nanoTime();
        toTrace.run();
        logger.info("Add To Trace {}, duration {} ms", description, (start - System.nanoTime()) / 1000);
    }

    @Override
    public <R> CompletableFuture<R> addToTraceAsync(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                    final String description,
                                                    final TraceContext context) {
        final double start = System.nanoTime();
        final CompletableFuture<R> future = toTraceAsync.get();
        future.handle((f,t) -> {
            logger.info("Add To Trace {}, duration {} ms", description, (start - System.nanoTime()) / 1000);
            return this;
        });
        return toTraceAsync.get();
    }

    @Override
    public <R> Promise<R> addToTracePromise(final Supplier<Promise<R>> toTraceAsync, final String description,
                                            final TraceContext context) {
        final double start = System.nanoTime();
        final Promise<R> promise = toTraceAsync.get();
        promise.onErrorPromise(throwable -> logger.info("Add To Trace {}, duration {} ms", description, (start - System.nanoTime()) / 1000));
        promise.onCompletePromise(throwable -> logger.info("Add To Trace {}, duration {} ms", description, (start - System.nanoTime()) / 1000));
        return toTraceAsync.get();
    }

    @Override
    public Serializable serializeContext() {
        return "";
    }

    @Override
    public TraceContext deserializeContext(final Serializable headers) {
        return new TraceContext() {
            @Override
            public Object get() {
                return null;
            }
        };
    }

    @Override
    public TraceContext currentContext() {
        return null;
    }

    @Override
    public TraceContext currentContextforObject(final Object obj) {
        return null;
    }

    @Override
    public <R> R newTrace(final Supplier<R> toTrace, final String description) {
        final double start = System.nanoTime();
        final R result = toTrace.get();
        logger.info("New Trace {}", description);
        return result;
    }

    @Override
    public void newTrace(final Runnable toTrace, final String description) {
        final double start = System.nanoTime();
        toTrace.run();
        logger.info("New Trace {}", description);
    }

    @Override
    public <R> CompletableFuture<R> newTraceAsync(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                  final String description) {
        final double start = System.nanoTime();
        final CompletableFuture<R> result = toTraceAsync.get();
        result.handle((f, t) -> {
            logger.info("New Trace {}, duration {}", description, (start - System.nanoTime()) / 1000);
            return this;
        });
        return result;
    }

    @Override
    public <R> Promise<R> newTracePromise(final Supplier<Promise<R>> toTraceAsync, final String description) {
        final double start = System.nanoTime();
        final Promise<R> promise = toTraceAsync.get();
        promise.onErrorPromise(throwable -> logger.info("New Trace {}, duration {}", description, (start - System.nanoTime()) / 1000));
        promise.onCompletePromise(throwable -> logger.info("New Trace {}, duration {}", description, (start - System.nanoTime()) / 1000));
        return toTraceAsync.get();
    }

    @Override
    public <R> R addToTrace(final Supplier<R> toTrace, final String description) {
        final double start = System.nanoTime();
        final R result = toTrace.get();
        logger.info("Add To Trace {}, duration {} ms", description, (start - System.nanoTime()) / 1000);
        return result;
    }

    @Override
    public void addToTrace(final Runnable toTrace, final String description) {
        final double start = System.nanoTime();
        toTrace.run();
        logger.info("Add To Trace {}, duration {} ms", description, (start - System.nanoTime()) / 1000);

    }

    @Override
    public <R> CompletableFuture<R> addToTraceAsync(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                    final String description) {
        final double start = System.nanoTime();
        final CompletableFuture<R> result = toTraceAsync.get();
        result.handle((f,t) -> {
            logger.info("Add To Trace {}, duration {} ms", description, (start - System.nanoTime()) / 1000);
            return this;
        });
        return result;
    }

    @Override
    public <R> Promise<R> addToTracePromise(final Supplier<Promise<R>> toTraceAsync, final String description) {
        final double start = System.nanoTime();
        final Promise<R> promise = toTraceAsync.get();
        promise.onErrorPromise(throwable -> logger.info("Add To Trace {}, duration {} ms", description, (start - System.nanoTime()) / 1000));
        promise.onCompletePromise(throwable -> logger.info("Add To Trace {}, duration {} ms", description, (start - System.nanoTime()) / 1000));
        return promise;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public <R> R newTrace(final Supplier<R> toTrace, final String description, final String eventId) {
        final double start = System.nanoTime();
        final R result = toTrace.get();
        logger.info("Add To Trace {}, duration {} ms", description, (start - System.nanoTime()) / 1000);
        return result;
    }

    @Override
    public void newTrace(final Runnable toTrace, final String description, final String eventId) {
        final double start = System.nanoTime();
        toTrace.run();
        logger.info("Add To Trace {}, duration {} ms", description, (start - System.nanoTime()) / 1000);
    }

    @Override
    public <R> CompletableFuture<R> newTraceAsync(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                  final String description,
                                                  final String eventId) {
        final double start = System.nanoTime();
        final CompletableFuture<R> future = toTraceAsync.get();
        future.handle((f, t) -> {
            logger.info("Add To Trace {}, duration {} ms", description, (start - System.nanoTime()) / 1000);
            return this;
        });
        return future;
    }

    @Override
    public <R> Promise<R> newTracePromise(final Supplier<Promise<R>> toTraceAsync, final String description,
                                          final String eventId) {
        final double start = System.nanoTime();
        final Promise<R> promise = toTraceAsync.get();
        promise.onErrorPromise(throwable -> logger.info("Add To Trace {}, duration {} ms", description, (start - System.nanoTime()) / 1000));
        promise.onCompletePromise(throwable -> logger.info("Add To Trace {}, duration {} ms", description, (start - System.nanoTime()) / 1000));
        return toTraceAsync.get();
    }

    @Override
    public <R> R newProcess(final Supplier<R> toTrace, final String description, final String eventId) {
        final double start = System.nanoTime();
        final R result = toTrace.get();
        logger.info("New Process {}, duration {} ms", description, (start - System.nanoTime()) / 1000);
        return result;
    }

    @Override
    public void newProcess(final Runnable toTrace, final String description, final String eventId) {
        final double start = System.nanoTime();
        toTrace.run();
        logger.info("New Process {}, duration {} ms", description, (start - System.nanoTime()) / 1000);
    }

    @Override
    public <R> CompletableFuture newProcessFuture(final Supplier<CompletableFuture<R>> toTrace, final String description,
                                              final String eventId) {
        final double start = System.nanoTime();
        final CompletableFuture<R> result = toTrace.get();
        result.handle((f,t) -> {
            logger.info("New Process {}, duration {} ms", description, (start - System.nanoTime()) / 1000);
            return this;
        });
        return toTrace.get();
    }

    @Override
    public <R> Promise<R> newProcessPromise(final Supplier<Promise<R>> toTrace, final String description,
                                            final String eventId) {
        final double start = System.nanoTime();
        final Promise<R> promise = toTrace.get();
        promise.onErrorPromise(throwable -> logger.info("New Process {}, duration {} ms", description, (start - System.nanoTime()) / 1000));
        promise.onCompletePromise(throwable -> logger.info("New Process {}, duration {} ms", description, (start - System.nanoTime()) / 1000));
        return toTrace.get();
    }

    @Override
    public <R> R addToTrace(final Supplier<R> toTrace, final String description, final String eventId) {
        final double start = System.nanoTime();
        final R result = toTrace.get();
        logger.info("Add To Trace {}, duration {} ms", description, (start - System.nanoTime()) / 1000);
        return result;
    }

    @Override
    public void addToTrace(final Runnable toTrace, final String description, final String eventId) {
        final double start = System.nanoTime();
        toTrace.run();
        logger.info("Add To Trace {}, duration {} ms", description, (start - System.nanoTime()) / 1000);
    }

    @Override
    public <R> CompletableFuture<R> addToTraceAsync(final Supplier<CompletableFuture<R>> toTraceAsync,
                                                    final String description,
                                                    final String eventId) {
        final double start = System.nanoTime();
        final CompletableFuture<R> future = toTraceAsync.get();
        future.handle((f,t) -> {
            logger.info("Add To Trace {}, duration {} ms", description, (start - System.nanoTime()) / 1000);
            return this;
        });
        return future;
    }

    @Override
    public <R> Promise<R> addToTracePromise(final Supplier<Promise<R>> toTraceAsync, final String description,
                                            final String eventId) {
        final double start = System.nanoTime();
        final Promise<R> promise = toTraceAsync.get();
        promise.onErrorPromise(throwable -> logger.info("Add To Trace {}, duration {} ms", description, (start - System.nanoTime()) / 1000));
        promise.onCompletePromise(throwable -> logger.info("Add To Trace {}, duration {} ms", description, (start - System.nanoTime()) / 1000));
        return toTraceAsync.get();
    }

    @Override
    public TraceContext currentContextforId(final String eventId) {
        return new TraceContext() {
            @Override
            public Object get() {
                return null;
            }
        };
    }

    @Override
    public boolean traceHasStarted(final String eventId) {
        return true;
    }
}
