package com.feedzai.commons.tracing.engine;


import com.feedzai.commons.tracing.api.TraceContext;
import io.opentracing.SpanContext;

/**
 * Implements {@link TraceContext} parametrized with {@link SpanContext} in order to be used with the OpenTracing @see
 * <a href="https://opentracing.io/">https://opentracing.io/</a> implementation of {@link
 * com.feedzai.commons.tracing.api.Tracing}.
 *
 * @author Gonçalo Garcia (goncalo.garcia@feedzai.com)
 */
public class SpanTraceContext implements TraceContext<SpanContext> {

    /**
     * The context encapsulated by this class.
     */
    private final SpanContext span;

    /**
     * The constructor for this class.
     * @param span The context encapsulated by this class
     */
    public SpanTraceContext(final SpanContext span) {
        this.span = span;
    }

    @Override
    public SpanContext get() {
        return span;
    }

}
