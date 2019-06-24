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

/**
 * Singleton that holds an engine of the tracing engine to be shared.
 *
 * @author Gonçalo Garcia (goncalo.garcia@feedzai.com)
 */
public class TraceUtil {


    /**
     * The tracer engine.
     */
    private static JaegerTracingEngine engine;

    private TraceUtil(){}

    /**
     * Initiates the tracing engine.
     *
     * @param engine The engine.
     */
    public static void init(final JaegerTracingEngine engine) {
        TraceUtil.engine = engine;
    }

    /**
     * Obtains the engine.
     *
     * @return the engine.
     */
    public static JaegerTracingEngine instance() {
        return engine;
    }

}
