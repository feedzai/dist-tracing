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

import com.feedzai.commons.tracing.api.Promise;
import com.feedzai.commons.tracing.api.TraceContext;
import com.feedzai.commons.tracing.api.TracingOpen;
import com.feedzai.commons.tracing.api.TracingOpenWithContext;
import com.feedzai.commons.tracing.api.TracingOpenWithId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class LoggingTracingEngine implements TracingOpenWithContext, TracingOpen, TracingOpenWithId {


    /**
     * The logger.
     */
    static final Logger logger = LoggerFactory.getLogger(LoggingTracingEngine.class.getName());


    @Override
    public <R> Promise addToTraceOpenPromise(Supplier<Promise<R>> toTraceAsync, Object object,
                                             String description) {
        double start = System.nanoTime();
        Promise result = toTraceAsync.get().onCompletePromise(promise -> {
            logger.info("Starting traced block for: {} associated to {} in {} ms", description, object, (start - System.nanoTime()) / 1000);
        });
        return result;
    }

    @Override
    public <R> CompletableFuture<R> addToTraceOpenFuture(Supplier<CompletableFuture<R>> toTraceAsync,
                                                         Object object, String description) {
        double start = System.nanoTime();
        CompletableFuture<R> result = toTraceAsync.get();
        result.handle((f, t) -> {
            logger.info("Starting traced block for: {} associated to {} in {} ms", description, object, (start - System.nanoTime()) / 1000);
            return this;
        });
        return result;
    }

    @Override
    public void addToTraceOpen(Runnable toTraceAsync, Object object, String description) {
        double start = System.nanoTime();
        toTraceAsync.run();
        logger.info("Starting traced block for: {} associated to {} in {} ms", description, object, (start - System.nanoTime()) / 1000);
    }

    @Override
    public <R> R addToTraceOpen(Supplier<R> toTraceAsync, Object value, String description) {
        double start = System.nanoTime();
        R result = toTraceAsync.get();
        logger.info("Starting traced block for: {} associated to {} in {} ms", description, value, (start - System.nanoTime()) / 1000);
        return result;
    }

    @Override
    public <R> Promise addToTraceOpenPromise(Supplier<Promise<R>> toTraceAsync, Object object,
                                             String description, String eventId) {
        double start = System.nanoTime();
        Promise result = toTraceAsync.get().onCompletePromise(promise -> {
            logger.info("Starting traced block for: {} associated to {} in {} ms", description, object, (start - System.nanoTime()) / 1000);
        });
        return result;
    }

    @Override
    public <R> CompletableFuture<R> addToTraceOpenFuture(Supplier<CompletableFuture<R>> toTraceAsync,
                                                         Object object, String description,
                                                         String eventId) {
        double start = System.nanoTime();
        CompletableFuture<R> result = toTraceAsync.get();
        result.handle((f, t) -> {
            logger.info("Starting traced block for: {} associated to {} in {} ms", description, object, (start - System.nanoTime()) / 1000);
            return this;
        });
        return result;
    }

    @Override
    public void addToTraceOpen(Runnable toTraceAsync, Object object, String description,
                               String eventId) {
        double start = System.nanoTime();
        toTraceAsync.run();
        logger.info("Starting traced block for: {} associated to {} in {} ms", description, object, (start - System.nanoTime()) / 1000);
    }

    @Override
    public <R> R addToTraceOpen(Supplier<R> toTraceAsync, Object value, String description,
                                String eventId) {
        double start = System.nanoTime();
        R result = toTraceAsync.get();
        logger.info("Starting traced block for: {} associated to {} in {} ms", description, value, (start - System.nanoTime()) / 1000);
        return result;
    }

    @Override
    public void closeOpen(Object object) {
        logger.info("Closing traced block associated to {}", object);
    }

    @Override
    public <R> Promise addToTraceOpenPromise(Supplier<Promise<R>> toTraceAsync, Object object,
                                             String description, TraceContext context) {
        double start = System.nanoTime();
        Promise result = toTraceAsync.get().onCompletePromise(promise -> {
            logger.info("Starting traced block for: {} associated to {} in {} ms", description, object, (start - System.nanoTime()) / 1000);
        });
        return result;
    }

    @Override
    public <R> CompletableFuture<R> addToTraceOpenFuture(Supplier<CompletableFuture<R>> toTraceAsync,
                                                         Object object, String description,
                                                         TraceContext context) {
        double start = System.nanoTime();
        CompletableFuture<R> result = toTraceAsync.get();
        result.handle((f, t) -> {
            logger.info("Starting traced block for: {} associated to {} in {} ms", description, object, (start - System.nanoTime()) / 1000);
            return this;
        });
        return result;
    }

    @Override
    public void addToTraceOpen(Runnable toTraceAsync, Object object, String description,
                               TraceContext context) {
        double start = System.nanoTime();
        toTraceAsync.run();
        logger.info("Starting traced block for: {} associated to {} in {} ms", description, object, (start - System.nanoTime()) / 1000);

    }

    @Override
    public <R> R addToTraceOpen(Supplier<R> toTraceAsync, Object value, String description,
                                TraceContext context) {
        double start = System.nanoTime();
        R result = toTraceAsync.get();
        logger.info("Starting traced block for: {} associated to {} in {} ms", description, value, (start - System.nanoTime()) / 1000);
        return result;
    }

    @Override
    public <R> R newProcess(Supplier<R> toTrace, String description, TraceContext context) {
        double start = System.nanoTime();
        logger.info("New Process {}, duration {} ms", description, (start - System.nanoTime()) / 1000);
        return toTrace.get();
    }

    @Override
    public void newProcess(Runnable toTrace, String description, TraceContext context) {
        double start = System.nanoTime();
        logger.info("New Process {}, duration {} ms", description, (start - System.nanoTime()) / 1000);
        toTrace.run();
    }

    @Override
    public Promise newProcessPromise(Supplier<Promise> toTrace, String description,
                                     TraceContext context) {
        double start = System.nanoTime();
        logger.info("New Process {}, duration {} ms", description, (start - System.nanoTime()) / 1000);
        return toTrace.get();
    }

    @Override
    public <R> CompletableFuture<R> newProcessFuture(Supplier<CompletableFuture<R>> toTrace,
                                                     String description, TraceContext context) {
        double start = System.nanoTime();
        logger.info("New Process {}, duration {} ms", description, (start - System.nanoTime()) / 1000);
        return toTrace.get();
    }

    @Override
    public <R> R addToTrace(Supplier<R> toTrace, String description, TraceContext context) {
        double start = System.nanoTime();
        logger.info("Add To Trace {}, duration {} ms", description,(start - System.nanoTime()) / 1000);
        return toTrace.get();
    }

    @Override
    public void addToTrace(Runnable toTrace, String description, TraceContext context) {
        double start = System.nanoTime();
        logger.info("Add To Trace {}, duration {} ms", description,(start - System.nanoTime()) / 1000);

        toTrace.run();

    }

    @Override
    public <R> CompletableFuture<R> addToTraceAsync(Supplier<CompletableFuture<R>> toTraceAsync, String description,
                                                    TraceContext context) {
        double start = System.nanoTime();
        logger.info("Add To Trace {}, duration {} ms", description,(start - System.nanoTime()) / 1000);
        return toTraceAsync.get();
    }

    @Override
    public Promise addToTracePromise(Supplier<Promise> toTraceAsync, String description, TraceContext context) {
        double start = System.nanoTime();
        logger.info("Add To Trace {}, duration {} ms", description,(start - System.nanoTime()) / 1000);
        return toTraceAsync.get();
    }

    @Override
    public Serializable serializeContext() {
        return "";
    }

    @Override
    public TraceContext deserializeContext(Serializable headers) {
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
    public TraceContext currentContextforObject(Object obj) {
        return null;
    }

    @Override
    public <R> R newTrace(Supplier<R> toTrace, String description) {
        double start = System.nanoTime();
        logger.info("New Trace {}", description);
        return toTrace.get();
    }

    @Override
    public void newTrace(Runnable toTrace, String description) {
        double start = System.nanoTime();
        logger.info("New Trace {}, duration {}", description, (start - System.nanoTime()) / 1000);
        toTrace.run();

    }

    @Override
    public <R> CompletableFuture<R> newTraceAsync(Supplier<CompletableFuture<R>> toTraceAsync, String description) {
        double start = System.nanoTime();
        logger.info("New Trace {}, duration {}", description, (start - System.nanoTime()) / 1000);
        return toTraceAsync.get();
    }

    @Override
    public Promise newTracePromise(Supplier<Promise> toTraceAsync, String description) {
        double start = System.nanoTime();
        logger.info("New Trace {}, duration {}", description, (start - System.nanoTime()) / 1000);
        return toTraceAsync.get();
    }

    @Override
    public <R> R addToTrace(Supplier<R> toTrace, String description) {
        double start = System.nanoTime();
        logger.info("Add To Trace {}, duration {} ms", description,(start - System.nanoTime()) / 1000);
        return toTrace.get();
    }

    @Override
    public void addToTrace(Runnable toTrace, String description) {
        double start = System.nanoTime();
        logger.info("Add To Trace {}, duration {} ms", description,(start - System.nanoTime()) / 1000);
        toTrace.run();

    }

    @Override
    public <R> CompletableFuture<R> addToTraceAsync(Supplier<CompletableFuture<R>> toTraceAsync, String description) {
        double start = System.nanoTime();
        logger.info("Add To Trace {}, duration {} ms", description,(start - System.nanoTime()) / 1000);
        return toTraceAsync.get();
    }

    @Override
    public Promise addToTracePromise(Supplier<Promise> toTraceAsync, String description) {
        double start = System.nanoTime();
        logger.info("Add To Trace {}, duration {} ms", description,(start - System.nanoTime()) / 1000);
        return toTraceAsync.get();
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public <R> R newTrace(Supplier<R> toTrace, String description, String eventId) {
        double start = System.nanoTime();
        logger.info("Add To Trace {}, duration {} ms", description,(start - System.nanoTime()) / 1000);
        return toTrace.get();
    }

    @Override
    public void newTrace(Runnable toTrace, String description, String eventId) {
        double start = System.nanoTime();
        logger.info("Add To Trace {}, duration {} ms", description,(start - System.nanoTime()) / 1000);
        toTrace.run();

    }

    @Override
    public <R> CompletableFuture<R> newTraceAsync(Supplier<CompletableFuture<R>> toTraceAsync, String description,
                                                  String eventId) {
        double start = System.nanoTime();
        logger.info("Add To Trace {}, duration {} ms", description,(start - System.nanoTime()) / 1000);
        return toTraceAsync.get();
    }

    @Override
    public Promise newTracePromise(Supplier<Promise> toTraceAsync, String description, String eventId) {
        double start = System.nanoTime();
        logger.info("Add To Trace {}, duration {} ms", description,(start - System.nanoTime()) / 1000);
        return toTraceAsync.get();
    }

    @Override
    public <R> R newProcess(Supplier<R> toTrace, String description, String eventId) {
        double start = System.nanoTime();
        logger.info("New Process {}, duration {} ms", description, (start - System.nanoTime()) / 1000);
        return toTrace.get();
    }

    @Override
    public void newProcess(Runnable toTrace, String description, String eventId) {
        double start = System.nanoTime();
        logger.info("New Process {}, duration {} ms", description, (start - System.nanoTime()) / 1000);
        toTrace.run();
    }

    @Override
    public CompletableFuture newProcessFuture(Supplier<CompletableFuture> toTrace, String description,
                                              String eventId) {
        double start = System.nanoTime();
        logger.info("New Process {}, duration {} ms", description, (start - System.nanoTime()) / 1000);
        return toTrace.get();
    }

    @Override
    public Promise newProcessPromise(Supplier<Promise> toTrace, String description, String eventId) {
        double start = System.nanoTime();
        logger.info("New Process {}, duration {} ms", description, (start - System.nanoTime()) / 1000);
        return toTrace.get();
    }

    @Override
    public <R> R addToTrace(Supplier<R> toTrace, String description, String eventId) {
        double start = System.nanoTime();
        logger.info("Add To Trace {}, duration {} ms", description,(start - System.nanoTime()) / 1000);
        return toTrace.get();
    }

    @Override
    public void addToTrace(Runnable toTrace, String description, String eventId) {
        double start = System.nanoTime();
        logger.info("Add To Trace {}, duration {} ms", description,(start - System.nanoTime()) / 1000);
        toTrace.run();
    }

    @Override
    public <R> CompletableFuture<R> addToTraceAsync(Supplier<CompletableFuture<R>> toTraceAsync, String description,
                                                    String eventId) {
        double start = System.nanoTime();
        logger.info("Add To Trace {}, duration {} ms", description,(start - System.nanoTime()) / 1000);
        return toTraceAsync.get();
    }

    @Override
    public Promise addToTracePromise(Supplier<Promise> toTraceAsync, String description, String eventId) {
        double start = System.nanoTime();
        logger.info("Add To Trace {}, duration {} ms", description,(start - System.nanoTime()) / 1000);
        return toTraceAsync.get();
    }

    @Override
    public TraceContext currentContextforId(String eventId) {
        return new TraceContext() {
            @Override
            public Object get() {
                return null;
            }
        };
    }

    @Override
    public boolean traceHasStarted(String eventId) {
        return true;
    }
}
