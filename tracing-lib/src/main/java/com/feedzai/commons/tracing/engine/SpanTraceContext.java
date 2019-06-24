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
