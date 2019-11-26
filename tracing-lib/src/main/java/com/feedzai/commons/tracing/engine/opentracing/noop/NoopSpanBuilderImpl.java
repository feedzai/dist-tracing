package com.feedzai.commons.tracing.engine.opentracing.noop;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;

public final class NoopSpanBuilderImpl implements NoopSpanBuilder {

    @Override
    public Tracer.SpanBuilder addReference(String refType, SpanContext referenced) {
        return this;
    }

    @Override
    public Tracer.SpanBuilder asChildOf(SpanContext parent) {
        return this;
    }

    @Override
    public Tracer.SpanBuilder ignoreActiveSpan() { return this; }

    @Override
    public Tracer.SpanBuilder asChildOf(Span parent) {
        return this;
    }

    @Override
    public Tracer.SpanBuilder withTag(String key, String value) {
        return this;
    }

    @Override
    public Tracer.SpanBuilder withTag(String key, boolean value) {
        return this;
    }

    @Override
    public Tracer.SpanBuilder withTag(String key, Number value) {
        return this;
    }

    @Override
    public Tracer.SpanBuilder withStartTimestamp(long microseconds) {
        return this;
    }

    @Override
    public Scope startActive(boolean b) {
        return NoopScopeManager.NoopScope.INSTANCE;
    }

    @Override
    public Span startManual() {
        return NoopSpan.INSTANCE;
    }

    @Override
    public Span start() {
        return NoopSpanImpl.INSTANCE;
    }

    @Override
    public String toString() { return NoopSpanBuilder.class.getSimpleName(); }
}
