package com.feedzai.commons.tracing.engine.opentracing.noop;

import io.opentracing.Scope;
import io.opentracing.Span;

/**
 * A noop (i.e., cheap-as-possible) implementation of an ScopeManager.
 */
public class NoopScopeManagerImpl implements NoopScopeManager {

    @Override
    public Scope activate(Span span, boolean b) {
        return NoopScope.INSTANCE;
    }

    public Span activeSpan() {
        return NoopSpan.INSTANCE;
    }


    @Override
    public Scope active() {
        return NoopScope.INSTANCE;
    }

    static class NoopScopeImpl implements NoopScope {
        @Override
        public void close() {}

        @Override
        public Span span() {
            return NoopSpan.INSTANCE;
        }
    }
}
