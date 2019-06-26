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

package com.feedzai.commons.tracing.engine;

import com.feedzai.commons.tracing.api.TraceContext;
import com.feedzai.commons.tracing.engine.configuration.CacheConfiguration;
import io.opentracing.Span;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.*;

public class AbstractTracingEngineWithIdTest {

    public static final String EVENT_ID = "498c9cf0-295e-44b1-bed8-73e1e0c3e389";
    public static final String TRACE_ID_STRING = "1:1:1";

    @Test
    public void testNewTraceSupplierWithId() {
        MockTracer mockTracer = new MockTracer();
        TracingEngineWithId tracing = new TracingEngineWithId(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));

        assertNull(tracing.traceIdMappings.getIfPresent(EVENT_ID));

        tracing.newTrace(TestUtils::doStuffWithResult, "Do Stuff", EVENT_ID);
        assertEquals(1, mockTracer.finishedSpans().size());

        assertNotNull(tracing.traceIdMappings.getIfPresent(EVENT_ID));
        assertEquals(tracing.traceIdMappings.getIfPresent(EVENT_ID), TRACE_ID_STRING);


        Assert.assertNotNull(tracing.spanIdMappings.getIfPresent(tracing.traceIdMappings.getIfPresent(EVENT_ID)));
        assertEquals(tracing.spanIdMappings.getIfPresent(TRACE_ID_STRING), Collections.singletonList(mockTracer.finishedSpans().get(0)));


        MockSpan span = mockTracer.finishedSpans().get(0);
        assertTrue(span.references().isEmpty());
    }

    @Test
    public void testNewTraceRunnableWithId() {
        MockTracer mockTracer = new MockTracer();
        TracingEngineWithId tracing = new TracingEngineWithId(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));

        assertNull(tracing.traceIdMappings.getIfPresent(EVENT_ID));

        tracing.newTrace(TestUtils::doStuffVoid, "Do Stuff", EVENT_ID);
        assertEquals(1, mockTracer.finishedSpans().size());

        assertNotNull(tracing.traceIdMappings.getIfPresent(EVENT_ID));
        assertEquals(tracing.traceIdMappings.getIfPresent(EVENT_ID), TRACE_ID_STRING);


        Assert.assertNotNull(tracing.spanIdMappings.getIfPresent(tracing.traceIdMappings.getIfPresent(EVENT_ID)));
        assertEquals(tracing.spanIdMappings.getIfPresent(TRACE_ID_STRING), Collections.singletonList(mockTracer.finishedSpans().get(0)));


        MockSpan span = mockTracer.finishedSpans().get(0);
        assertTrue(span.references().isEmpty());
    }

    @Test
    public void testNewTraceAsyncWithId() {
        MockTracer mockTracer = new MockTracer();
        TracingEngineWithId tracing = new TracingEngineWithId(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));

        assertNull(tracing.traceIdMappings.getIfPresent(EVENT_ID));


        CompletableFuture future = new CompletableFuture();
        tracing.newTraceAsync(() -> future, "Do Long Running Stuff", EVENT_ID);
        assertEquals(0, mockTracer.finishedSpans().size());

        future.complete(null);
        assertEquals(1, mockTracer.finishedSpans().size());

        assertNotNull(tracing.traceIdMappings.getIfPresent(EVENT_ID));
        assertEquals(tracing.traceIdMappings.getIfPresent(EVENT_ID), TRACE_ID_STRING);


        Assert.assertNotNull(tracing.spanIdMappings.getIfPresent(tracing.traceIdMappings.getIfPresent(EVENT_ID)));
        assertEquals(tracing.spanIdMappings.getIfPresent(TRACE_ID_STRING), Collections.singletonList(mockTracer.finishedSpans().get(0)));


        MockSpan span = mockTracer.finishedSpans().get(0);
        assertTrue(span.references().isEmpty());
    }

    @Test
    public void testNewTracePromiseWithId() {
        MockTracer mockTracer = new MockTracer();
        TracingEngineWithId tracing = new TracingEngineWithId(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));

        assertNull(tracing.traceIdMappings.getIfPresent(EVENT_ID));


        MockPromise promise = new MockPromise();
        tracing.newTracePromise(() -> promise, "Do Long Running Stuff", EVENT_ID);
        assertEquals(0, mockTracer.finishedSpans().size());

        promise.complete();
        assertEquals(1, mockTracer.finishedSpans().size());


        assertNotNull(tracing.traceIdMappings.getIfPresent(EVENT_ID));
        assertEquals(tracing.traceIdMappings.getIfPresent(EVENT_ID), TRACE_ID_STRING);


        Assert.assertNotNull(tracing.spanIdMappings.getIfPresent(tracing.traceIdMappings.getIfPresent(EVENT_ID)));
        assertEquals(tracing.spanIdMappings.getIfPresent(TRACE_ID_STRING), Collections.singletonList(mockTracer.finishedSpans().get(0)));


        MockSpan span = mockTracer.finishedSpans().get(0);
        assertTrue(span.references().isEmpty());
    }

    @Test
    public void testAddToTraceSupplierWithId() throws ExecutionException, InterruptedException {
        MockTracer mockTracer = new MockTracer();
        TracingEngineWithId tracing = new TracingEngineWithId(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));


        tracing.newTrace(TestUtils::doStuffVoid, "Do Stuff", EVENT_ID);
        assertNotNull(tracing.traceIdMappings.getIfPresent(EVENT_ID));
        assertEquals(tracing.traceIdMappings.getIfPresent(EVENT_ID), TRACE_ID_STRING);
        Assert.assertNotNull(tracing.spanIdMappings.getIfPresent(tracing.traceIdMappings.getIfPresent(EVENT_ID)));
        assertEquals(tracing.spanIdMappings.getIfPresent(TRACE_ID_STRING), Collections.singletonList(mockTracer.finishedSpans().get(0)));

        tracing.addToTrace(() -> {
            TestUtils.doStuffWithResult();
            assertEquals(tracing.spanIdMappings.getIfPresent(TRACE_ID_STRING).size(), 1);
        }, "Do More Stuff", EVENT_ID);
        assertEquals(tracing.spanIdMappings.getIfPresent(TRACE_ID_STRING), Collections.singletonList(mockTracer.finishedSpans().get(0)));
        assertEquals(2, mockTracer.finishedSpans().size());

        CompletableFuture.runAsync(() -> tracing.addToTrace(() -> {
            assertEquals(tracing.spanIdMappings.getIfPresent(TRACE_ID_STRING).size(), 2);
            return TestUtils.doStuffWithResult();
        }, "Do More Stuff Async", EVENT_ID)).get();

        assertEquals(tracing.spanIdMappings.getIfPresent(TRACE_ID_STRING), Collections.singletonList(mockTracer.finishedSpans().get(0)));

        MockSpan parent = mockTracer.finishedSpans().get(0);
        assertTrue(parent.references().isEmpty());

        MockSpan child = mockTracer.finishedSpans().get(1);
        assertEquals(1, child.references().size());
        assertEquals("child_of", child.references().get(0).getReferenceType());
        assertEquals(child.references().get(0).getContext().spanId(), parent.context().spanId());

        MockSpan otherChild = mockTracer.finishedSpans().get(2);
        assertEquals(1, otherChild.references().size());
        assertEquals("child_of", otherChild.references().get(0).getReferenceType());
        assertEquals(otherChild.references().get(0).getContext().spanId(), parent.context().spanId());
    }

    @Test
    public void testAddToTraceRunnableWithId() throws ExecutionException, InterruptedException {
        MockTracer mockTracer = new MockTracer();
        TracingEngineWithId tracing = new TracingEngineWithId(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));


        tracing.newTrace(TestUtils::doStuffVoid, "Do Stuff", EVENT_ID);
        assertNotNull(tracing.traceIdMappings.getIfPresent(EVENT_ID));
        assertEquals(tracing.traceIdMappings.getIfPresent(EVENT_ID), TRACE_ID_STRING);
        Assert.assertNotNull(tracing.spanIdMappings.getIfPresent(tracing.traceIdMappings.getIfPresent(EVENT_ID)));
        assertEquals(tracing.spanIdMappings.getIfPresent(TRACE_ID_STRING), Collections.singletonList(mockTracer.finishedSpans().get(0)));

        tracing.addToTrace(() -> {
            TestUtils.doStuffWithResult();
            assertEquals(tracing.spanIdMappings.getIfPresent(TRACE_ID_STRING).size(), 1);
        }, "Do More Stuff", EVENT_ID);
        assertEquals(tracing.spanIdMappings.getIfPresent(TRACE_ID_STRING), Collections.singletonList(mockTracer.finishedSpans().get(0)));
        assertEquals(2, mockTracer.finishedSpans().size());

        CompletableFuture.runAsync(() -> tracing.addToTrace(() -> {
            TestUtils.doStuffWithResult();
            assertEquals(tracing.spanIdMappings.getIfPresent(TRACE_ID_STRING).size(), 2);
        }, "Do More Stuff Async", EVENT_ID)).get();


        assertEquals(tracing.spanIdMappings.getIfPresent(TRACE_ID_STRING), Collections.singletonList(mockTracer.finishedSpans().get(0)));

        MockSpan parent = mockTracer.finishedSpans().get(0);
        assertTrue(parent.references().isEmpty());

        MockSpan child = mockTracer.finishedSpans().get(1);
        assertEquals(1, child.references().size());
        assertEquals("child_of", child.references().get(0).getReferenceType());
        assertEquals(child.references().get(0).getContext().spanId(), parent.context().spanId());

        MockSpan otherChild = mockTracer.finishedSpans().get(2);
        assertEquals(1, otherChild.references().size());
        assertEquals("child_of", otherChild.references().get(0).getReferenceType());
        assertEquals(otherChild.references().get(0).getContext().spanId(), parent.context().spanId());
    }

    @Test
    public void testAddToTraceAsyncWithId() throws ExecutionException, InterruptedException {
        MockTracer mockTracer = new MockTracer();
        TracingEngineWithId tracing = new TracingEngineWithId(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));


        tracing.newTrace(TestUtils::doStuffVoid, "Do Stuff", EVENT_ID);
        assertNotNull(tracing.traceIdMappings.getIfPresent(EVENT_ID));
        assertEquals(tracing.traceIdMappings.getIfPresent(EVENT_ID), TRACE_ID_STRING);
        Assert.assertNotNull(tracing.spanIdMappings.getIfPresent(tracing.traceIdMappings.getIfPresent(EVENT_ID)));
        assertEquals(tracing.spanIdMappings.getIfPresent(TRACE_ID_STRING), Collections.singletonList(mockTracer.finishedSpans().get(0)));


        CompletableFuture future = new CompletableFuture();
        tracing.addToTraceAsync(() -> {
            assertEquals(tracing.spanIdMappings.getIfPresent(TRACE_ID_STRING).size(), 1);
            return future;
        }, "Do More Stuff", EVENT_ID);
        assertEquals(tracing.spanIdMappings.getIfPresent(TRACE_ID_STRING), Collections.singletonList(mockTracer.finishedSpans().get(0)));
        assertEquals(1, mockTracer.finishedSpans().size());

        future.complete(null);
        assertEquals(2, mockTracer.finishedSpans().size());

        CompletableFuture future2 = new CompletableFuture();
        CompletableFuture otherThread = CompletableFuture.supplyAsync(() -> tracing.addToTraceAsync(() -> {
            assertEquals(tracing.spanIdMappings.getIfPresent(TRACE_ID_STRING).size(), 2);
            return future2;
        }, "Do More Stuff Async", EVENT_ID));
        assertEquals(2, mockTracer.finishedSpans().size());

        future2.complete(null);
        otherThread.get();
        assertEquals(3, mockTracer.finishedSpans().size());


        assertEquals(tracing.spanIdMappings.getIfPresent(TRACE_ID_STRING), Collections.singletonList(mockTracer.finishedSpans().get(0)));

        MockSpan parent = mockTracer.finishedSpans().get(0);
        assertTrue(parent.references().isEmpty());

        MockSpan child = mockTracer.finishedSpans().get(1);
        assertEquals(1, child.references().size());
        assertEquals("child_of", child.references().get(0).getReferenceType());
        assertEquals(child.references().get(0).getContext().spanId(), parent.context().spanId());

        MockSpan otherChild = mockTracer.finishedSpans().get(2);
        assertEquals(1, otherChild.references().size());
        assertEquals("child_of", otherChild.references().get(0).getReferenceType());
        assertEquals(otherChild.references().get(0).getContext().spanId(), parent.context().spanId());
    }

    @Test
    public void testAddToTracePromisecWithId() throws ExecutionException, InterruptedException {
        MockTracer mockTracer = new MockTracer();
        TracingEngineWithId tracing = new TracingEngineWithId(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));


        tracing.newTrace(TestUtils::doStuffVoid, "Do Stuff", EVENT_ID);
        assertNotNull(tracing.traceIdMappings.getIfPresent(EVENT_ID));
        assertEquals(tracing.traceIdMappings.getIfPresent(EVENT_ID), TRACE_ID_STRING);
        Assert.assertNotNull(tracing.spanIdMappings.getIfPresent(tracing.traceIdMappings.getIfPresent(EVENT_ID)));
        assertEquals(tracing.spanIdMappings.getIfPresent(TRACE_ID_STRING), Collections.singletonList(mockTracer.finishedSpans().get(0)));


        MockPromise promise = new MockPromise();
        tracing.addToTracePromise(() -> {
            assertEquals(tracing.spanIdMappings.getIfPresent(TRACE_ID_STRING).size(), 1);
            return promise;
        }, "Do More Stuff", EVENT_ID);
        assertEquals(tracing.spanIdMappings.getIfPresent(TRACE_ID_STRING), Collections.singletonList(mockTracer.finishedSpans().get(0)));
        assertEquals(1, mockTracer.finishedSpans().size());

        promise.complete();
        assertEquals(2, mockTracer.finishedSpans().size());

        MockPromise promise2 = new MockPromise();
        CompletableFuture otherThread = CompletableFuture.supplyAsync(() -> tracing.addToTracePromise(() -> {
            assertEquals(tracing.spanIdMappings.getIfPresent(TRACE_ID_STRING).size(), 2);
            return promise2;
        }, "Do More Stuff Async", EVENT_ID));
        assertEquals(2, mockTracer.finishedSpans().size());

        otherThread.get();
        promise2.complete();
        assertEquals(3, mockTracer.finishedSpans().size());

        assertEquals(tracing.spanIdMappings.getIfPresent(TRACE_ID_STRING), Collections.singletonList(mockTracer.finishedSpans().get(0)));

        MockSpan parent = mockTracer.finishedSpans().get(0);
        assertTrue(parent.references().isEmpty());

        MockSpan child = mockTracer.finishedSpans().get(1);
        assertEquals(1, child.references().size());
        assertEquals("child_of", child.references().get(0).getReferenceType());
        assertEquals(child.references().get(0).getContext().spanId(), parent.context().spanId());

        MockSpan otherChild = mockTracer.finishedSpans().get(2);
        assertEquals(1, otherChild.references().size());
        assertEquals("child_of", otherChild.references().get(0).getReferenceType());
        assertEquals(otherChild.references().get(0).getContext().spanId(), parent.context().spanId());
    }

    @Test
    public void testAddToTraceOpenSupplier() throws ExecutionException, InterruptedException {
        MockTracer mockTracer = new MockTracer();
        TracingEngineWithId tracing = new TracingEngineWithId(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));


        tracing.newTrace(TestUtils::doStuffVoid, "Do Stuff", EVENT_ID);
        assertNotNull(tracing.traceIdMappings.getIfPresent(EVENT_ID));
        assertEquals(tracing.traceIdMappings.getIfPresent(EVENT_ID), TRACE_ID_STRING);
        Assert.assertNotNull(tracing.spanIdMappings.getIfPresent(tracing.traceIdMappings.getIfPresent(EVENT_ID)));
        assertEquals(tracing.spanIdMappings.getIfPresent(TRACE_ID_STRING), Collections.singletonList(mockTracer.finishedSpans().get(0)));

        MockSpan parent = mockTracer.finishedSpans().get(0);
        assertTrue(parent.references().isEmpty());

        CompletableFuture.runAsync(() -> {
            Object obj = new Object();
            tracing.addToTraceOpen(TestUtils::doStuffWithResult, obj, "Do More Stuff", EVENT_ID);
            assertEquals(1, mockTracer.finishedSpans().size());

            tracing.closeOpen(obj);
            assertEquals(2, mockTracer.finishedSpans().size());
        }).get();

        MockSpan child = mockTracer.finishedSpans().get(1);
        assertEquals(1, child.references().size());
        assertEquals("child_of", child.references().get(0).getReferenceType());
        assertEquals(child.references().get(0).getContext().spanId(), parent.context().spanId());
    }

    @Test
    public void testAddToTraceOpenRunnable() throws ExecutionException, InterruptedException {
        MockTracer mockTracer = new MockTracer();
        TracingEngineWithId tracing = new TracingEngineWithId(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));


        tracing.newTrace(TestUtils::doStuffVoid, "Do Stuff", EVENT_ID);
        assertNotNull(tracing.traceIdMappings.getIfPresent(EVENT_ID));
        assertEquals(tracing.traceIdMappings.getIfPresent(EVENT_ID), TRACE_ID_STRING);
        Assert.assertNotNull(tracing.spanIdMappings.getIfPresent(tracing.traceIdMappings.getIfPresent(EVENT_ID)));
        assertEquals(tracing.spanIdMappings.getIfPresent(TRACE_ID_STRING), Collections.singletonList(mockTracer.finishedSpans().get(0)));

        MockSpan parent = mockTracer.finishedSpans().get(0);
        assertTrue(parent.references().isEmpty());

        CompletableFuture.runAsync(() -> {
            Object obj = new Object();
            tracing.addToTraceOpen(TestUtils::doStuffVoid, obj, "Do More Stuff", EVENT_ID);
            assertEquals(1, mockTracer.finishedSpans().size());

            tracing.closeOpen(obj);
            assertEquals(2, mockTracer.finishedSpans().size());
        }).get();

        MockSpan child = mockTracer.finishedSpans().get(1);
        assertEquals(1, child.references().size());
        assertEquals("child_of", child.references().get(0).getReferenceType());
        assertEquals(child.references().get(0).getContext().spanId(), parent.context().spanId());
    }

    @Test
    public void testCurrentContextForId() throws ExecutionException, InterruptedException {
        MockTracer mockTracer = new MockTracer();
        TracingEngineWithId tracing = new TracingEngineWithId(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));

        tracing.newTrace(TestUtils::doStuffVoid, "Do Stuff", EVENT_ID);

        assertEquals(tracing.currentContextforId(EVENT_ID).get(), mockTracer.finishedSpans().get(0).context());

        CompletableFuture.runAsync(() -> {
                CompletableFuture fut = CompletableFuture.runAsync(() -> {
                    tracing.addToTrace(() -> {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }, "Do More Stuff", EVENT_ID);
                });
                TraceContext span = tracing.currentContext();
                assertEquals(tracing.currentContextforId(EVENT_ID).get(), mockTracer.finishedSpans().get(0).context());
            try {
                fut.get();
                assertEquals(mockTracer.finishedSpans().get(1).context(), span.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void testTraceHasStarted() {
        MockTracer mockTracer = new MockTracer();
        TracingEngineWithId tracing = new TracingEngineWithId(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));
        assertFalse(tracing.traceHasStarted(EVENT_ID));
        tracing.newTrace(TestUtils::doStuffVoid, "Do Stuff", EVENT_ID);
        assertTrue(tracing.traceHasStarted(EVENT_ID));
    }


}
