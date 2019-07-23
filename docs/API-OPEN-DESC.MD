# Tracing Unfinished API Description

In this page we will describe the different API methods and how they interact with each other in situations where the start and finish of Spans must happen in different methods.

### When to use this API

This API allows you to associate a Span object to any object that is tightly coupled with it, and could be used to retrieve it at a later time.
It should be used, for instance, when tracing the time spent in a queue, or when tracing asynchronous processing that is not encapsulated by a CompletableFuture or a Promise.

### How it differs from the base Tracing API

This API exposes the same functionality as [Tracing API](API-DESC.md), with slight differences:

* Adds `addToTraceOpen` methods, that take in an additional parameter `object` allowing you to map the span to an object that is present in both its starting and finish points.

### Examples

#### Continuing a trace

In some cases we might want to trace executions that cannot be enclosed in a single lambda, or method. To do that, our API provides methods that allow you to open a Span and close it later, by associating it to an object.

 **Example 1** - Opening a Span
 
In this case all we have to do is associate the beginning of the traced execution to an object.
 
```java
 void sendToQueue(Message msg) {
    Tracing.addToTraceOpen(() -> queue.add(msg), msg, "Waiting in Queue");
 }
```

This will begin a span, and associate it to the object to be retrieved later.


 **Example 2** - Finishing opened Span.
 
```java
 void getFromQueue(Message msg) {
    Message msg = queue.take();
    Tracing.closeOpen(msg);
 }
```

This method will retrieve the span associated to the object passed as argument and finish it.
