package com.feedzai.commons.tracing.engine;

import com.feedzai.commons.tracing.engine.configuration.CacheConfiguration;
import com.google.common.base.Preconditions;
import io.jaegertracing.Configuration;
import io.jaegertracing.internal.reporters.RemoteReporter;
import io.jaegertracing.internal.samplers.ProbabilisticSampler;
import io.jaegertracing.spi.Reporter;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Random;

/**
 * Concrete extension of the OpenTracing based abstract implementation of the {@link
 * com.feedzai.commons.tracing.api.TracingWithId} and {@link com.feedzai.commons.tracing.api.TracingWithContext} APIs.
 *
 * @author Gonçalo Garcia (goncalo.garcia@feedzai.com)
 */
public class JaegerTracingEngine extends AbstractTracingEngineWithId {


    /**
     * Constructor for this abstract class to be called by the extension classes to supply the implementation specific
     * parameters.
     *
     * @param tracer        The Tracer implementation of the underlying tracing Engine.
     * @param configuration The configuration parameters for the caches.
     */
    JaegerTracingEngine(final Tracer tracer,
                        final CacheConfiguration configuration) {
        super(tracer, configuration);
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
            final Tracer trace = config.getTracerBuilder().withReporter(reporter).withSampler(new ProbabilisticSampler(sampleRate)).build();
            if (!GlobalTracer.isRegistered()) {
                GlobalTracer.register(trace);
            }
            return trace;
        }


    }


}
