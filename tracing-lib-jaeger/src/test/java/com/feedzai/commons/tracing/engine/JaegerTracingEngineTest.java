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

package com.feedzai.commons.tracing.engine;

import com.feedzai.commons.tracing.api.TraceContext;
import io.opentracing.Span;
import org.junit.Test;

import static org.junit.Assert.*;

import java.time.Duration;
import java.util.HashMap;

public class JaegerTracingEngineTest {

    private static final String UBER_TRACE_ID = "uber-trace-id";
    public static final String EVENT_ID = "498c9cf0-295e-44b1-bed8-73e1e0c3e389";


    @Test
    public void testSerializeContext() {
        JaegerTracingEngine engine = new JaegerTracingEngine.Builder().build();

        engine.newTrace(() -> {
            TestUtils.doStuffVoid();
            HashMap<String, String> ctx = (HashMap<String, String>) engine.serializeContext();
            assertNotNull(ctx.get(UBER_TRACE_ID));
        }, "Do Stuff");
    }

    @Test
    public void testSerializeContextForId() {
        JaegerTracingEngine engine = new JaegerTracingEngine.Builder().build();
        engine.newTrace(TestUtils::doStuffVoid, "Do Stuff", EVENT_ID);
        HashMap<String, String> ctx = (HashMap<String, String>) engine.serializeContextForId(EVENT_ID);
        assertNotNull(ctx.get(UBER_TRACE_ID));
    }

    @Test
    public void testDeserializeContext() {
        JaegerTracingEngine engine = new JaegerTracingEngine.Builder().build();
        engine.newTrace(TestUtils::doStuffVoid, "Do Stuff", EVENT_ID);
        TestUtils.doStuffVoid();
        TraceContext originalCtx = engine.currentContext();
        HashMap<String, String> ctx = (HashMap<String, String>) engine.serializeContext();
        assertNotNull(ctx.get(UBER_TRACE_ID));
        TraceContext deserializedCtx = engine.deserializeContext(ctx);
        assertEquals(deserializedCtx.get().toString(), originalCtx.get().toString());


    }

    @Test
    public void testGetTraceId() {
        JaegerTracingEngine engine = new JaegerTracingEngine.Builder().build();
        engine.newTrace(TestUtils::doStuffVoid, "Do Stuff", EVENT_ID);
        Span ctx = engine.currentSpan();
        String traceId = engine.getTraceIdFromSpan(ctx);
        assertTrue(ctx.context().toString().contains(traceId));
    }

}
