package com.feedzai.commons.tracing.engine.opentracing.noop;

import io.opentracing.SpanContext;

import java.util.Map;

public final class NoopSpanImpl implements NoopSpan {

    @Override
    public SpanContext context() { return NoopSpanContextImpl.INSTANCE; }

    @Override
    public void finish() {}

    @Override
    public void finish(long finishMicros) {}

    @Override
    public NoopSpan setTag(String key, String value) { return this; }

    @Override
    public NoopSpan setTag(String key, boolean value) { return this; }

    @Override
    public NoopSpan setTag(String key, Number value) { return this; }

    @Override
    public NoopSpan log(Map<String, ?> fields) { return this; }

    @Override
    public NoopSpan log(long timestampMicroseconds, Map<String, ?> fields) { return this; }

    @Override
    public NoopSpan log(String event) { return this; }

    @Override
    public NoopSpan log(long timestampMicroseconds, String event) { return this; }

    @Override
    public NoopSpan setBaggageItem(String key, String value) { return this; }

    @Override
    public String getBaggageItem(String key) { return ""; }

    @Override
    public NoopSpan setOperationName(String operationName) { return this; }

    @Override
    public String toString() { return NoopSpan.class.getSimpleName(); }
}
