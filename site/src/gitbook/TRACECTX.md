# TraceContext

Since our API is implementation agnostic, we can't use a vendor specific Span object whenever it's useful to refer to the trace context directly. To counter that we designed an immutable interface that is parametrized with the implementation's context object. This means that one TraceContext object holds one and only one implementation specific 
context objec.

For our OpenTracing implementation we used this interface to wrap the Span object by creating a class `SpanTracingContext` that extends `TracingContext<Span>`

User's can implement their own versions of the TracingContext to fit whatever their needs might be.