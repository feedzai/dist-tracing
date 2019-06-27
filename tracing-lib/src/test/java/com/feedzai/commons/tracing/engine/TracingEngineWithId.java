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
import com.feedzai.commons.tracing.engine.configuration.CacheConfiguration;
import io.opentracing.Span;
import io.opentracing.Tracer;

import java.io.Serializable;

public class TracingEngineWithId extends AbstractOpenTracingEngineWithId {
    /**
     * Constructor for this abstract class to be called by the extension classes to supply the implementation specific
     * parameters.
     *
     * @param tracer        The Tracer implementation of the underlying tracing Engine.
     * @param configuration The configuration parameters for the caches.
     */
    protected TracingEngineWithId(Tracer tracer,
                                  CacheConfiguration configuration) {
        super(tracer, configuration);
    }

    @Override
    protected String getTraceIdFromSpan(Span span) {
        return "1:1:1";
    }

    @Override
    public Serializable serializeContext() {
        return null;
    }

    @Override
    public TraceContext deserializeContext(Serializable headers) {
        return null;
    }
}
