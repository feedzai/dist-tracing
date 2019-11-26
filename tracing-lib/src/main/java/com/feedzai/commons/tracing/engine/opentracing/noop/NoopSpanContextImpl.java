package com.feedzai.commons.tracing.engine.opentracing.noop;

import java.util.Collections;
import java.util.Map;

public final class NoopSpanContextImpl implements NoopSpanContext {
    static final NoopSpanContextImpl INSTANCE = new NoopSpanContextImpl();
    Map<String, String> map = Collections.singletonMap("uber-trace-id", "1:2:3");
    public String toTraceId() {
        return "";
    }

    public String toSpanId() {
        return "";
    }

    @Override
    public Iterable<Map.Entry<String, String>> baggageItems() {
        return map.entrySet();
    }

    @Override
    public String toString() { return NoopSpanContext.class.getSimpleName(); }

}
