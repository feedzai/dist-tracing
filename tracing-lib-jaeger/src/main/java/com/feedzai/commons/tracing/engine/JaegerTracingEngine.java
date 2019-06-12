/*
 *
 *  * Copyright 2018 Feedzai
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
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import io.jaegertracing.Configuration;
import io.jaegertracing.internal.JaegerSpanContext;
import io.jaegertracing.internal.reporters.RemoteReporter;
import io.jaegertracing.internal.samplers.ProbabilisticSampler;
import io.jaegertracing.spi.Reporter;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;
import io.opentracing.util.GlobalTracer;

import java.io.Serializable;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Concrete extension of the OpenTracing based abstract implementation of the {@link
 * com.feedzai.commons.tracing.api.TracingWithId} and {@link com.feedzai.commons.tracing.api.TracingWithContext} APIs.
 *
 * @author Gonçalo Garcia (goncalo.garcia@feedzai.com)
 */
public class JaegerTracingEngine extends AbstractTracingEngineWithId {


    /**
     * Jaeger's TextMap key for a baggage item called "name".
     */
    private static final String UBERCTX_NAME = "uberctx-name";

    /**
     * Jaeger's TextMap key for the context.
     */
    private static final String UBER_TRACE_ID = "uber-trace-id";

    /**
     * Jaeger's TextMap key for a baggage item called "id".
     */
    private static final String UBERCTX_ID = "uberctx-id";


    /**
     * Constructor for this abstract class to be called by the extension classes to supply the implementation specific
     * parameters.
     *
     * @param tracer        The Tracer implementation of the underlying tracing Engine.
     * @param configuration The configuration parameters for the caches.
     */
    @VisibleForTesting
    JaegerTracingEngine(final Tracer tracer,
                        final CacheConfiguration configuration) {
        super(tracer, configuration);
    }

    /**
     * Tracer.
     *
     * @return Tracer.
     */
    public Tracer getTracer() {
        return super.tracer;
    }

    /**
     * Tracer.
     *
     * @return Tracer.
     */
    public Span currentSpan() {
        return tracer.activeSpan();
    }

    /**
     * Tracer.
     *
     * @param eventId A.
     * @param span    B.
     */
    public void mapEventId(final String eventId, final Span span) {
        traceIdMappings.put(eventId, getTraceIdFromSpan(span));
    }


    @Override
    public Serializable serializeContext() {
        final HashMap<String, String> map = new HashMap<>();
        if (GlobalTracer.isRegistered() && GlobalTracer.get().activeSpan() != null) {
            GlobalTracer.get().inject(GlobalTracer.get().activeSpan().context(), Format.Builtin.TEXT_MAP, implementTextMap(map));
        }
        return map;
    }

    /**
     * Tracer.
     *
     * @param id A.
     * @return Result.
     */
    public Serializable serializeContextForId(final String id) {
        final HashMap<String, String> map = new HashMap<>();
        if (GlobalTracer.isRegistered() && traceIdMappings.getIfPresent(id) != null) {
            GlobalTracer.get().inject(
                    spanIdMappings.getIfPresent(traceIdMappings.getIfPresent(id)).peek().context(),
                    Format.Builtin.TEXT_MAP, implementTextMap(map));
        }
        return map;
    }

    /**
     * Returns an implementation of Jaeger's TextMap.
     *
     * @param map The map that should be turned into a {@link TextMap}
     * @return the TextMap object
     */
    private TextMap implementTextMap(final Map<String, String> map) {
        return new TextMap() {
            @Override
            public Iterator<Map.Entry<String, String>> iterator() {
                return map.entrySet().iterator();
            }

            @Override
            public void put(final String key, final String value) {
                map.put(key, value);
            }
        };
    }

    @Override
    public TraceContext deserializeContext(final Serializable headers) {
        final Map<String, String> textMap = ((Map<String, Object>) headers).entrySet().stream()
                .filter(entry -> entry.getKey().equals(UBERCTX_NAME) || entry.getKey().equals(UBER_TRACE_ID)
                        || entry.getKey().equals(UBERCTX_ID))
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().toString()));

        final SpanContext context = GlobalTracer.get().extract(Format.Builtin.TEXT_MAP, new TextMap() {
            @Override
            public Iterator<Map.Entry<String, String>> iterator() {
                return textMap.entrySet().iterator();
            }

            @Override
            public void put(final String key, final String value) {
            }
        });
        final String traceId = (textMap.get(UBER_TRACE_ID) != null ? textMap.get(UBER_TRACE_ID) : "").split(":")[0];
        if (context == null) {
            //This is okay because creating a span as child of null creates an orphan span and does not throw an NPE.
            return new SpanTraceContext(null);
        } else if (((JaegerSpanContext) context).getBaggageItem(EVENT_ID) != null) {
            traceIdMappings.put(((JaegerSpanContext) context).getBaggageItem(EVENT_ID), traceId);
            spanIdMappings.put(traceId, new LinkedList<>());
        }
        return new SpanTraceContext(context);
    }


    @Override
    protected String getTraceIdFromSpan(final Span span) {
        final HashMap<String, String> map = new HashMap<>();
        tracer.inject(span.context(), Format.Builtin.TEXT_MAP, implementTextMap(map));
        return map.get(UBER_TRACE_ID).split(":")[0];
    }

    /**
     * Builder for instances of {@link JaegerTracingEngine}.
     */
    public static class Builder {

        /**
         * The address of the Jaeger Agent that will receive the completed Spans. Default value is localhost.
         */
        private String ip = "localhost";

        /**
         * The name of this process, for displaying in the UI. Default value is "UnknownProcess" + a random identifier.
         */
        private String processName = "UnknownProcess" + RANDOM.nextInt();

        /**
         * The rate at which requests should be sampled for tracing. Default value is 1.
         */
        private double sampleRate = 1;

        /**
         * The maximum duration of records in the context caches. Default value is 10 seconds.
         */
        private Duration cacheDuration = Duration.of(10, ChronoUnit.SECONDS);

        /**
         * The maximum number of elements in the cache. Default value is 10.000
         */
        private long cacheMaxSize = 10000;

        /**
         * Random object for generating random IDs.
         */
        private static final Random RANDOM = new Random();


        /**
         * Sets the value of the {@code ip} parameter for this {@link JaegerTracingEngine} instance.
         *
         * @param ip The Jaeger agent address.
         * @return this Builder.
         */
        public Builder withIp(final String ip) {
            Preconditions.checkArgument(!ip.isEmpty());
            Preconditions.checkNotNull(ip);
            this.ip = ip;
            return this;
        }

        /**
         * Sets the value of the {@code processName} parameter for this {@link JaegerTracingEngine} instance.
         *
         * @param processName The name of the process
         * @return this Builder.
         */
        public Builder withProcessName(final String processName) {
            Preconditions.checkArgument(!processName.isEmpty());
            Preconditions.checkNotNull(processName);
            this.processName = processName;
            return this;
        }

        /**
         * Sets the value of the {@code sampleRate} parameter for this {@link JaegerTracingEngine} instance.
         *
         * @param sampleRate The sample rate.
         * @return this Builder.
         */
        public Builder withSampleRate(final double sampleRate) {
            Preconditions.checkArgument(sampleRate >= 0);
            this.sampleRate = sampleRate;
            return this;
        }

        /**
         * Sets the value of the {@code cacheDuration} parameter for this {@link JaegerTracingEngine} instance.
         *
         * @param cacheDuration The duration of the cache.
         * @return this Builder.
         */
        public Builder withCacheDuration(final Duration cacheDuration) {
            Preconditions.checkNotNull(cacheDuration);
            this.cacheDuration = cacheDuration;
            return this;
        }


        /**
         * Sets the value of the {@code cacheMaxSize} parameter for this {@link JaegerTracingEngine} instance.
         *
         * @param cacheMaxSize Maximum size of the cache.
         * @return this Builder.
         */
        public Builder withCacheMaxSize(final long cacheMaxSize) {
            Preconditions.checkArgument(cacheMaxSize >= 0);
            this.cacheMaxSize = cacheMaxSize;
            return this;
        }

        /**
         * Builds an instance of {@link JaegerTracingEngine} based on the parameters supplied to the builder.
         *
         * @return an instance of {@link JaegerTracingEngine} configured with the parameters supplied to the Builder.
         */
        public JaegerTracingEngine build() {
            final Tracer tracer = initTracer(ip, processName, sampleRate);
            final CacheConfiguration configuration = new CacheConfiguration(cacheDuration, cacheMaxSize);
            return new JaegerTracingEngine(tracer, configuration);
        }


        /**
         * Configures an instance of {@link io.jaegertracing.internal.JaegerTracer} with the supplied agent address,
         * process name and sampling rate.
         *
         * @param ip          The agent's ip address.
         * @param processName This process's name.
         * @param sampleRate  The sampling rate
         * @return instance of {@link io.jaegertracing.internal.JaegerTracer}
         */
        private Tracer initTracer(final String ip, final String processName, final double sampleRate) {
            final Configuration.SenderConfiguration senderConfig = Configuration.SenderConfiguration.fromEnv().withAgentHost(ip);
            final Reporter reporter = new RemoteReporter.Builder().withSender(senderConfig.getSender()).build();
            final Configuration config = new Configuration(processName);
            final Tracer trace = config.getTracerBuilder()
                    .withClock(new MicroClock())
                    .withReporter(reporter)
                    .withSampler(new ProbabilisticSampler(sampleRate)).build();
            if (!GlobalTracer.isRegistered()) {
                GlobalTracer.register(trace);
            }
            return trace;
        }


    }


}
