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

import com.feedzai.commons.tracing.engine.configuration.BaseConfiguration;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class AbstractOpenTracingEngineTest {

    private MockTracer mockTracer;
    private MockTracingEngine tracing;

    @Before
    public void initializeTracer(){
        mockTracer = new MockTracer();
        tracing = new MockTracingEngine(mockTracer, new BaseConfiguration(Duration.ofDays(1), 10000, 1));
    }

    @Test
    public void testNewTraceSupplier() {
        tracing.newTrace(TestUtils::doStuffWithResult, "Do Stuff");
        assertHasNoChildren(mockTracer);
    }

    private MockSpan assertHasNoChildren(MockTracer mockTracer) {
        assertEquals(1, mockTracer.finishedSpans().size());


        final MockSpan span = mockTracer.finishedSpans().get(0);
        assertTrue(span.references().isEmpty());
        return span;
    }

    @Test
    public void testNewTraceRunnable() {
        tracing.newTrace(TestUtils::doStuffVoid, "Do Stuff");
        assertHasNoChildren(mockTracer);
    }

    @Test
    public void testNewTraceAsync() {
        final CompletableFuture future = new CompletableFuture();
        tracing.newTraceAsync(() -> future, "Do Long Running Stuff");
        assertEquals(0, mockTracer.finishedSpans().size());

        future.complete(null);
        assertHasNoChildren(mockTracer);
    }

    @Test
    public void testNewTracePromise() {
        final MockPromise promise = new MockPromise();
        tracing.newTracePromise(() -> promise, "Do Long Running Stuff");
        assertEquals(0, mockTracer.finishedSpans().size());

        promise.complete();
        assertHasNoChildren(mockTracer);
    }

    @Test
    public void testAddToTraceSupplier() {
        tracing.newTrace(() -> {
            tracing.addToTrace(TestUtils::doStuffWithResult, "Do More Stuff");
        }, "Do Stuff");

        assertParentRelationship(mockTracer, 1, 0);
    }

    @Test
    public void testAddToTraceSupplierWithContext() {
        final SpanTraceContext ctx = buildNewTraceRunnableContext();

        tracing.addToTrace(TestUtils::doStuffWithResult, "Do Stuff in new Process", ctx);

        assertParentRelationship(mockTracer, 0, 1);
    }

    private SpanTraceContext buildNewTraceRunnableContext() {
        tracing.newTrace(TestUtils::doStuffWithResult, "Do Stuff");
        return new SpanTraceContext(mockTracer.finishedSpans().get(0).context());
    }

    private void assertParentRelationship(final MockTracer mockTracer, final int firstFinishedSpan, final int lastFinishedSpan) {
        assertEquals(2, mockTracer.finishedSpans().size());

        final MockSpan parent = mockTracer.finishedSpans().get(firstFinishedSpan);
        assertTrue(parent.references().isEmpty());

        final MockSpan child = mockTracer.finishedSpans().get(lastFinishedSpan);
        assertEquals(1, child.references().size());
        assertEquals("child_of", child.references().get(0).getReferenceType());
        assertEquals(child.references().get(0).getContext().spanId(), parent.context().spanId());
    }

    @Test
    public void testAddToTraceRunnable() {
        tracing.newTrace(() -> {
            tracing.addToTrace(TestUtils::doStuffVoid, "Do More Stuff");
        }, "Do Stuff");

        assertParentRelationship(mockTracer, 1, 0);
    }

    @Test
    public void testAddToTraceRunnableWithContext() {
        final SpanTraceContext ctx = buildNewTraceSupplierContext();

        tracing.addToTrace(TestUtils::doStuffVoid, "Do Stuff in new Process", ctx);

        assertParentRelationship(mockTracer, 0, 1);
    }

    @Test
    public void testAddToTraceAsync() {
        final CompletableFuture future = new CompletableFuture();
        tracing.newTrace(() -> {
            tracing.addToTraceAsync(() -> future, "Do Long Running Stuff");
        }, "Do Stuff");

        assertParentRelationshipFuture(mockTracer, future);
    }

    @Test
    public void testAddToTraceAsyncWithContext() {
        final CompletableFuture future = new CompletableFuture();

        final SpanTraceContext ctx = buildNewTraceSupplierContext();

        tracing.addToTraceAsync(() -> future, "Do Long Running Stuff", ctx);

        assertParentRelationshipFuture(mockTracer, future);
    }

    private void assertParentRelationshipFuture(final MockTracer mockTracer, final CompletableFuture future) {
        MockSpan parent = assertHasNoChildren(mockTracer);

        future.complete(null);
       assertParentRelationship(mockTracer, parent);
    }


    @Test
    public void testAddToTracePromise() {
        final MockPromise promise = new MockPromise();

        tracing.newTrace(() -> {
            tracing.addToTracePromise(() -> promise, "Do Long Running Stuff");
        }, "Do Stuff");

        assertParentRelationshipPromise(mockTracer, promise);
    }

    private void assertParentRelationshipPromise(final MockTracer mockTracer, final MockPromise promise) {
        MockSpan parent = assertHasNoChildren(mockTracer);

        promise.complete();
        assertParentRelationship(mockTracer, parent);
    }

    @Test
    public void testAddToTracePromiseWithContext() {
        final MockPromise promise = new MockPromise();
        final SpanTraceContext ctx = buildNewTraceSupplierContext();

        tracing.addToTracePromise(() -> promise, "Do Long Running Stuff", ctx);

        assertParentRelationshipPromise(mockTracer, promise);
    }

    @Test
    public void testNewProcessSupplier() {
        final SpanTraceContext ctx = buildNewTraceRunnableContext();

        tracing.newProcess(TestUtils::doStuffWithResult, "Do Stuff in new Process", ctx);

        final MockSpan parent = mockTracer.finishedSpans().get(0);
        assertTrue(parent.references().isEmpty());

        assertParentRelationship(mockTracer, parent);

    }

    @Test
    public void testNewProcessRunnable() {
        final SpanTraceContext ctx = buildNewTraceSupplierContext();

        tracing.newProcess(TestUtils::doStuffVoid, "Do Stuff in new Process", ctx);

        final MockSpan parent = mockTracer.finishedSpans().get(0);
        assertTrue(parent.references().isEmpty());

        assertParentRelationship(mockTracer, parent);
    }


    @Test
    public void testNewProcessFuture() {
        final SpanTraceContext ctx = buildNewTraceRunnableContext();

        final CompletableFuture future = new CompletableFuture();
        tracing.newProcessFuture(() -> future, "Do Long Running Stuff in new Process", ctx);

        assertEquals(1, mockTracer.finishedSpans().size());

        future.complete(null);
        assertParentRelationship(mockTracer, 0, 1);
    }

    @Test
    public void testNewProcessPromise() {
        final SpanTraceContext ctx = buildNewTraceRunnableContext();

        final MockPromise promise = new MockPromise();
        tracing.newProcessPromise(() -> promise, "Do Long Running Stuff in new Process", ctx);

        assertEquals(1, mockTracer.finishedSpans().size());

        promise.complete();
        assertParentRelationship(mockTracer, 0, 1);
    }

    @Test
    public void testAddToTraceOpenSupplier() {
        final Object obj = new Object();
        tracing.newTrace(() -> {
            tracing.addToTraceOpen(TestUtils::doStuffWithResult, obj, "Do More Stuff");
        }, "Do Stuff");
        assertParentRelationshipOpen(mockTracer, tracing, obj);
    }

    private void assertParentRelationshipOpen(final MockTracer mockTracer, final MockTracingEngine tracing, final Object obj) {
        MockSpan parent = assertHasNoChildren(mockTracer);

        tracing.closeOpen(obj);
        assertParentRelationship(mockTracer, parent);
    }

    private void assertParentRelationship(MockTracer mockTracer, MockSpan parent) {
        assertEquals(2, mockTracer.finishedSpans().size());

        final MockSpan child = mockTracer.finishedSpans().get(1);
        assertEquals(1, child.references().size());
        assertEquals("child_of", child.references().get(0).getReferenceType());
        assertEquals(child.references().get(0).getContext().spanId(), parent.context().spanId());
    }

    @Test
    public void testAddToTraceOpenSupplierWithContext() {
        final Object obj = new Object();

        final SpanTraceContext ctx = buildNewTraceRunnableContext();

        tracing.addToTraceOpen(TestUtils::doStuffWithResult, obj, "Do More Stuff", ctx);
        assertParentRelationshipOpen(mockTracer, tracing, obj);
    }

    @Test
    public void testAddToTraceOpenRunnable() {
        final Object obj = new Object();
        tracing.newTrace(() -> {
            tracing.addToTraceOpen(TestUtils::doStuffVoid, obj, "Do More Stuff");
        }, "Do Stuff");
        assertParentRelationshipOpen(mockTracer, tracing, obj);
    }

    @Test
    public void testAddToTraceOpenRunnableWithContext() {
        final Object obj = new Object();
        final SpanTraceContext ctx = buildNewTraceSupplierContext();

        tracing.addToTraceOpen(TestUtils::doStuffVoid, obj, "Do More Stuff", ctx);
        assertParentRelationshipOpen(mockTracer, tracing, obj);
    }

    private SpanTraceContext buildNewTraceSupplierContext() {
        tracing.newTrace(TestUtils::doStuffVoid, "Do Stuff");
        return new SpanTraceContext(mockTracer.finishedSpans().get(0).context());
    }


}

