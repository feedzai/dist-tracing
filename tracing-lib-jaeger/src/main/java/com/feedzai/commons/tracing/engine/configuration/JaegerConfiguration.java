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

package com.feedzai.commons.tracing.engine.configuration;

/**
 * Class that holds the configuration for {@link com.feedzai.commons.tracing.engine.JaegerTracingEngine}
 *
 * @author Gonçalo Garcia (goncalo.garcia@feedzai.com)
 */
public class JaegerConfiguration {

    /**
     * The duration of each cache record prior to expiration.
     */
    public long cacheDurationInMinutes;

    /**
     * The maximum size of the caches.
     */
    public long cacheMaxSize;

    /**
     * The rate at which requests will be sampled for tracing.
     */
    public double sampleRate;

    /**
     * The name of the this process.
     */
    public String processName;

    /**
     * The ip address of the jaeger agent.
     */
    public String ip;

}
