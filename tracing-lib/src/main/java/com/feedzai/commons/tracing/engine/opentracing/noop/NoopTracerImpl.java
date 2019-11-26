package com.feedzai.commons.tracing.engine.opentracing.noop;

import io.opentracing.Scope;
import io.opentracing.ScopeManager;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;

public final class NoopTracerImpl implements NoopTracer {
    final static NoopTracer INSTANCE = new NoopTracerImpl();

    @Override
    public ScopeManager scopeManager() {
        return NoopScopeManager.INSTANCE;
    }

    @Override
    public Span activeSpan() {
        return NoopSpanImpl.INSTANCE;
    }

    public Scope activateSpan(Span span) {
        return NoopScopeManager.NoopScope.INSTANCE;
    }

    @Override
    public SpanBuilder buildSpan(String operationName) { return NoopSpanBuilderImpl.INSTANCE; }

    @Override
    public <C> void inject(SpanContext spanContext, Format<C> format, C carrier) {
        if(format == Format.Builtin.TEXT_MAP){
            spanContext.baggageItems().forEach(x -> ((TextMap) carrier).put(x.getKey(), x.getValue()));
        }
    }

    @Override
    public <C> SpanContext extract(Format<C> format, C carrier) { return NoopSpanContextImpl.INSTANCE; }

    public void close() {}

    @Override
    public String toString() { return NoopTracer.class.getSimpleName(); }
}

