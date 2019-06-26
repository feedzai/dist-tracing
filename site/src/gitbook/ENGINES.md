# Engines

This library provides the following implementations of our API:

* JaegerTracingEngine - This implementation performs distributed tracing and uses Jaeger as its tracing backend. 

* NoopTracingEngine - This engine should be used when users wish to completely ignore all tracing instrumentation. Using this engine is the closest possible thing to removing all instrumentation code.

* LoggingTracingEngine - This engine logs each tracepoint to a file in the working environment. Keep in mind that this does not preserve the causal relation between tracepoints. The format of each log entry is the following: `description, optional(eventId), timestamp, latency(ns)`

# Writing Custom Engines

Writing a custom engine is as simple as implementing any of our APIs. Keep in mind that if you wish to add another distributed tracing backend (such as Zipkin) we already provide an OpenTracing based abstract implementation in [Tracing-Lib](/tracing-lib), which might make the process easier.