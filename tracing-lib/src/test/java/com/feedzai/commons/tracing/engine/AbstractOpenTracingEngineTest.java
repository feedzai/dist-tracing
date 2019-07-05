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

import com.feedzai.commons.tracing.engine.configuration.CacheConfiguration;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import org.junit.Test;

import static org.junit.Assert.*;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class AbstractOpenTracingEngineTest {

    @Test
    public void testNewTraceSupplier() {
        MockTracer mockTracer = new MockTracer();
        MockTracingEngine tracing = new MockTracingEngine(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));

        tracing.newTrace(TestUtils::doStuffWithResult, "Do Stuff");
        assertEquals(1, mockTracer.finishedSpans().size());


        MockSpan span = mockTracer.finishedSpans().get(0);
        assertTrue(span.references().isEmpty());
    }

    @Test
    public void testNewTraceRunnable() {
        MockTracer mockTracer = new MockTracer();
        MockTracingEngine tracing = new MockTracingEngine(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));

        tracing.newTrace(TestUtils::doStuffVoid, "Do Stuff");
        assertEquals(1, mockTracer.finishedSpans().size());


        MockSpan span = mockTracer.finishedSpans().get(0);
        assertTrue(span.references().isEmpty());
    }

    @Test
    public void testNewTraceAsync() {
        MockTracer mockTracer = new MockTracer();
        MockTracingEngine tracing = new MockTracingEngine(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));

        CompletableFuture future = new CompletableFuture();
        tracing.newTraceAsync(() -> future, "Do Long Running Stuff");
        assertEquals(0, mockTracer.finishedSpans().size());

        future.complete(null);
        assertEquals(1, mockTracer.finishedSpans().size());

        MockSpan span = mockTracer.finishedSpans().get(0);
        assertTrue(span.references().isEmpty());
    }

    @Test
    public void testNewTracePromise() {
        MockTracer mockTracer = new MockTracer();
        MockTracingEngine tracing = new MockTracingEngine(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));

        MockPromise promise = new MockPromise();
        tracing.newTracePromise(() -> promise, "Do Long Running Stuff");
        assertEquals(0, mockTracer.finishedSpans().size());

        promise.complete();
        assertEquals(1, mockTracer.finishedSpans().size());

        MockSpan span = mockTracer.finishedSpans().get(0);
        assertTrue(span.references().isEmpty());
    }

    @Test
    public void testAddToTraceSupplier() {
        MockTracer mockTracer = new MockTracer();
        MockTracingEngine tracing = new MockTracingEngine(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));

        tracing.newTrace(() -> {
            tracing.addToTrace(TestUtils::doStuffWithResult, "Do More Stuff");
        }, "Do Stuff");

        assertEquals(2, mockTracer.finishedSpans().size());

        MockSpan parent = mockTracer.finishedSpans().get(1);
        assertTrue(parent.references().isEmpty());

        MockSpan child = mockTracer.finishedSpans().get(0);
        assertEquals(1, child.references().size());
        assertEquals("child_of", child.references().get(0).getReferenceType());
        assertEquals(child.references().get(0).getContext().spanId(), parent.context().spanId());
    }

    @Test
    public void testAddToTraceSupplierWithContext() {
        MockTracer mockTracer = new MockTracer();
        MockTracingEngine tracing = new MockTracingEngine(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));

        tracing.newTrace(TestUtils::doStuffWithResult, "Do Stuff");
        SpanTraceContext ctx = new SpanTraceContext(mockTracer.finishedSpans().get(0).context());

        tracing.addToTrace(TestUtils::doStuffWithResult, "Do Stuff in new Process", ctx);

        assertEquals(2, mockTracer.finishedSpans().size());

        MockSpan parent = mockTracer.finishedSpans().get(0);
        assertTrue(parent.references().isEmpty());

        MockSpan child = mockTracer.finishedSpans().get(1);
        assertEquals(1, child.references().size());
        assertEquals("child_of", child.references().get(0).getReferenceType());
        assertEquals(child.references().get(0).getContext().spanId(), parent.context().spanId());
    }

    @Test
    public void testAddToTraceRunnable() {
        MockTracer mockTracer = new MockTracer();
        MockTracingEngine tracing = new MockTracingEngine(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));

        tracing.newTrace(() -> {
            tracing.addToTrace(TestUtils::doStuffVoid, "Do More Stuff");
        }, "Do Stuff");

        assertEquals(2, mockTracer.finishedSpans().size());

        MockSpan parent = mockTracer.finishedSpans().get(1);
        assertTrue(parent.references().isEmpty());

        MockSpan child = mockTracer.finishedSpans().get(0);
        assertEquals(1, child.references().size());
        assertEquals("child_of", child.references().get(0).getReferenceType());
        assertEquals(child.references().get(0).getContext().spanId(), parent.context().spanId());
    }

    @Test
    public void testAddToTraceRunnableWithContext() {
        MockTracer mockTracer = new MockTracer();
        MockTracingEngine tracing = new MockTracingEngine(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));

        tracing.newTrace(TestUtils::doStuffVoid, "Do Stuff");
        SpanTraceContext ctx = new SpanTraceContext(mockTracer.finishedSpans().get(0).context());

        tracing.addToTrace(TestUtils::doStuffVoid, "Do Stuff in new Process", ctx);

        assertEquals(2, mockTracer.finishedSpans().size());

        MockSpan parent = mockTracer.finishedSpans().get(0);
        assertTrue(parent.references().isEmpty());

        MockSpan child = mockTracer.finishedSpans().get(1);
        assertEquals(1, child.references().size());
        assertEquals("child_of", child.references().get(0).getReferenceType());
        assertEquals(child.references().get(0).getContext().spanId(), parent.context().spanId());
    }

    @Test
    public void testAddToTraceAsync() {
        MockTracer mockTracer = new MockTracer();
        MockTracingEngine tracing = new MockTracingEngine(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));

        CompletableFuture future = new CompletableFuture();
        tracing.newTrace(() -> {
            tracing.addToTraceAsync(() -> future, "Do Long Running Stuff");
        }, "Do Stuff");

        assertEquals(1, mockTracer.finishedSpans().size());


        MockSpan parent = mockTracer.finishedSpans().get(0);
        assertTrue(parent.references().isEmpty());

        future.complete(null);
        assertEquals(2, mockTracer.finishedSpans().size());

        MockSpan child = mockTracer.finishedSpans().get(1);
        assertEquals(1, child.references().size());
        assertEquals("child_of", child.references().get(0).getReferenceType());
        assertEquals(child.references().get(0).getContext().spanId(), parent.context().spanId());
    }

    @Test
    public void testAddToTraceAsyncWithContext() {
        MockTracer mockTracer = new MockTracer();
        MockTracingEngine tracing = new MockTracingEngine(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));

        CompletableFuture future = new CompletableFuture();

        tracing.newTrace(TestUtils::doStuffVoid, "Do Stuff");
        SpanTraceContext ctx = new SpanTraceContext(mockTracer.finishedSpans().get(0).context());

        tracing.addToTraceAsync(() -> future, "Do Long Running Stuff", ctx);

        assertEquals(1, mockTracer.finishedSpans().size());


        MockSpan parent = mockTracer.finishedSpans().get(0);
        assertTrue(parent.references().isEmpty());

        future.complete(null);
        assertEquals(2, mockTracer.finishedSpans().size());

        MockSpan child = mockTracer.finishedSpans().get(1);
        assertEquals(1, child.references().size());
        assertEquals("child_of", child.references().get(0).getReferenceType());
        assertEquals(child.references().get(0).getContext().spanId(), parent.context().spanId());
    }


    @Test
    public void testAddToTracePromise() {
        MockTracer mockTracer = new MockTracer();
        MockTracingEngine tracing = new MockTracingEngine(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));

        MockPromise promise = new MockPromise();


        tracing.newTrace(() -> {
            tracing.addToTracePromise(() -> promise, "Do Long Running Stuff");
        }, "Do Stuff");

        assertEquals(1, mockTracer.finishedSpans().size());


        MockSpan parent = mockTracer.finishedSpans().get(0);
        assertTrue(parent.references().isEmpty());

        promise.complete();
        assertEquals(2, mockTracer.finishedSpans().size());

        MockSpan child = mockTracer.finishedSpans().get(1);
        assertEquals(1, child.references().size());
        assertEquals("child_of", child.references().get(0).getReferenceType());
        assertEquals(child.references().get(0).getContext().spanId(), parent.context().spanId());
    }

    @Test
    public void testAddToTracePromiseWithContext() {
        MockTracer mockTracer = new MockTracer();
        MockTracingEngine tracing = new MockTracingEngine(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));

        MockPromise promise = new MockPromise();
        tracing.newTrace(TestUtils::doStuffVoid, "Do Stuff");
        SpanTraceContext ctx = new SpanTraceContext(mockTracer.finishedSpans().get(0).context());

        tracing.addToTracePromise(() -> promise, "Do Long Running Stuff", ctx);

        assertEquals(1, mockTracer.finishedSpans().size());


        MockSpan parent = mockTracer.finishedSpans().get(0);
        assertTrue(parent.references().isEmpty());

        promise.complete();
        assertEquals(2, mockTracer.finishedSpans().size());

        MockSpan child = mockTracer.finishedSpans().get(1);
        assertEquals(1, child.references().size());
        assertEquals("child_of", child.references().get(0).getReferenceType());
        assertEquals(child.references().get(0).getContext().spanId(), parent.context().spanId());
    }

    @Test
    public void testNewProcessSupplier() {
        MockTracer mockTracer = new MockTracer();
        MockTracingEngine tracing = new MockTracingEngine(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));

        tracing.newTrace(TestUtils::doStuffWithResult, "Do Stuff");
        SpanTraceContext ctx = new SpanTraceContext(mockTracer.finishedSpans().get(0).context());

        tracing.newProcess(TestUtils::doStuffWithResult, "Do Stuff in new Process", ctx);

        MockSpan parent = mockTracer.finishedSpans().get(0);
        assertTrue(parent.references().isEmpty());

        MockSpan child = mockTracer.finishedSpans().get(1);
        assertEquals(1, child.references().size());
        assertEquals("child_of", child.references().get(0).getReferenceType());
        assertEquals(child.references().get(0).getContext().spanId(), parent.context().spanId());
    }

    @Test
    public void testNewProcessRunnable() {
        MockTracer mockTracer = new MockTracer();
        MockTracingEngine tracing = new MockTracingEngine(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));

        tracing.newTrace(TestUtils::doStuffVoid, "Do Stuff");
        SpanTraceContext ctx = new SpanTraceContext(mockTracer.finishedSpans().get(0).context());

        tracing.newProcess(TestUtils::doStuffVoid, "Do Stuff in new Process", ctx);

        MockSpan parent = mockTracer.finishedSpans().get(0);
        assertTrue(parent.references().isEmpty());

        MockSpan child = mockTracer.finishedSpans().get(1);
        assertEquals(1, child.references().size());
        assertEquals("child_of", child.references().get(0).getReferenceType());
        assertEquals(child.references().get(0).getContext().spanId(), parent.context().spanId());
    }


    @Test
    public void testNewProcessFuture() {
        MockTracer mockTracer = new MockTracer();
        MockTracingEngine tracing = new MockTracingEngine(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));

        tracing.newTrace(TestUtils::doStuffWithResult, "Do Stuff");
        SpanTraceContext ctx = new SpanTraceContext(mockTracer.finishedSpans().get(0).context());

        CompletableFuture future = new CompletableFuture();
        tracing.newProcessFuture(() -> future, "Do Long Running Stuff in new Process", ctx);

        assertEquals(1, mockTracer.finishedSpans().size());

        future.complete(null);
        assertEquals(2, mockTracer.finishedSpans().size());

        MockSpan parent = mockTracer.finishedSpans().get(0);
        assertTrue(parent.references().isEmpty());

        MockSpan child = mockTracer.finishedSpans().get(1);
        assertEquals(1, child.references().size());
        assertEquals("child_of", child.references().get(0).getReferenceType());
        assertEquals(child.references().get(0).getContext().spanId(), parent.context().spanId());
    }

    @Test
    public void testNewProcessPromise() {
        MockTracer mockTracer = new MockTracer();
        MockTracingEngine tracing = new MockTracingEngine(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));

        tracing.newTrace(TestUtils::doStuffWithResult, "Do Stuff");
        SpanTraceContext ctx = new SpanTraceContext(mockTracer.finishedSpans().get(0).context());

        MockPromise promise = new MockPromise();
        tracing.newProcessPromise(() -> promise, "Do Long Running Stuff in new Process", ctx);

        assertEquals(1, mockTracer.finishedSpans().size());

        promise.complete();
        assertEquals(2, mockTracer.finishedSpans().size());

        MockSpan parent = mockTracer.finishedSpans().get(0);
        assertTrue(parent.references().isEmpty());

        MockSpan child = mockTracer.finishedSpans().get(1);
        assertEquals(1, child.references().size());
        assertEquals("child_of", child.references().get(0).getReferenceType());
        assertEquals(child.references().get(0).getContext().spanId(), parent.context().spanId());
    }

    @Test
    public void testAddToTraceOpenSupplier() {
        MockTracer mockTracer = new MockTracer();
        MockTracingEngine tracing = new MockTracingEngine(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));

        Object obj = new Object();
        tracing.newTrace(() -> {
            tracing.addToTraceOpen(TestUtils::doStuffWithResult, obj, "Do More Stuff");
        }, "Do Stuff");
        assertEquals(1, mockTracer.finishedSpans().size());

        MockSpan parent = mockTracer.finishedSpans().get(0);
        assertTrue(parent.references().isEmpty());

        tracing.closeOpen(obj);
        assertEquals(2, mockTracer.finishedSpans().size());

        MockSpan child = mockTracer.finishedSpans().get(1);
        assertEquals(1, child.references().size());
        assertEquals("child_of", child.references().get(0).getReferenceType());
        assertEquals(child.references().get(0).getContext().spanId(), parent.context().spanId());
    }

    @Test
    public void testAddToTraceOpenSupplierWithContext() {
        MockTracer mockTracer = new MockTracer();
        MockTracingEngine tracing = new MockTracingEngine(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));

        Object obj = new Object();

        tracing.newTrace(TestUtils::doStuffWithResult, "Do Stuff");
        SpanTraceContext ctx = new SpanTraceContext(mockTracer.finishedSpans().get(0).context());

        tracing.addToTraceOpen(TestUtils::doStuffWithResult, obj, "Do More Stuff", ctx);
        assertEquals(1, mockTracer.finishedSpans().size());

        MockSpan parent = mockTracer.finishedSpans().get(0);
        assertTrue(parent.references().isEmpty());

        tracing.closeOpen(obj);
        assertEquals(2, mockTracer.finishedSpans().size());

        MockSpan child = mockTracer.finishedSpans().get(1);
        assertEquals(1, child.references().size());
        assertEquals("child_of", child.references().get(0).getReferenceType());
        assertEquals(child.references().get(0).getContext().spanId(), parent.context().spanId());
    }

    @Test
    public void testAddToTraceOpenRunnable() {
        MockTracer mockTracer = new MockTracer();
        MockTracingEngine tracing = new MockTracingEngine(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));

        Object obj = new Object();
        tracing.newTrace(() -> {
            tracing.addToTraceOpen(TestUtils::doStuffVoid, obj, "Do More Stuff");
        }, "Do Stuff");
        assertEquals(1, mockTracer.finishedSpans().size());

        MockSpan parent = mockTracer.finishedSpans().get(0);
        assertTrue(parent.references().isEmpty());

        tracing.closeOpen(obj);
        assertEquals(2, mockTracer.finishedSpans().size());

        MockSpan child = mockTracer.finishedSpans().get(1);
        assertEquals(1, child.references().size());
        assertEquals("child_of", child.references().get(0).getReferenceType());
        assertEquals(child.references().get(0).getContext().spanId(), parent.context().spanId());
    }

    @Test
    public void testAddToTraceOpenRunnableWithContext() {
        MockTracer mockTracer = new MockTracer();
        MockTracingEngine tracing = new MockTracingEngine(mockTracer, new CacheConfiguration(Duration.ofDays(1), 10000));

        Object obj = new Object();
        tracing.newTrace(TestUtils::doStuffVoid, "Do Stuff");
        SpanTraceContext ctx = new SpanTraceContext(mockTracer.finishedSpans().get(0).context());

        tracing.addToTraceOpen(TestUtils::doStuffVoid, obj, "Do More Stuff", ctx);
        assertEquals(1, mockTracer.finishedSpans().size());

        MockSpan parent = mockTracer.finishedSpans().get(0);
        assertTrue(parent.references().isEmpty());

        tracing.closeOpen(obj);
        assertEquals(2, mockTracer.finishedSpans().size());

        MockSpan child = mockTracer.finishedSpans().get(1);
        assertEquals(1, child.references().size());
        assertEquals("child_of", child.references().get(0).getReferenceType());
        assertEquals(child.references().get(0).getContext().spanId(), parent.context().spanId());
    }






}

