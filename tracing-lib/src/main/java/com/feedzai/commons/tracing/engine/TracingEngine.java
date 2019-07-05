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

import com.feedzai.commons.tracing.api.TracingOpen;
import com.feedzai.commons.tracing.api.TracingOpenWithContext;
import com.feedzai.commons.tracing.api.TracingOpenWithId;
import io.opentracing.Span;
import io.opentracing.Tracer;

/**
 * Helper interface that allows the engine implementations to depend on a single interface instead of three so that we
 * can leverage polimorphism to make Engine Decorators more generic.
 *
 * @author Gonçalo Garcia (goncalo.garcia@feedzai.com)
 */
public interface TracingEngine extends TracingOpenWithContext, TracingOpen, TracingOpenWithId {

    /**
     * Returns the tracer object of the underlying OpenTracing implementation
     *
     * @return the tracer.
     */
    Tracer getTracer();

    /**
     * Returns the span that represents the current thread-local context.
     *
     * @return current span.
     */
    Span currentSpan();

}
