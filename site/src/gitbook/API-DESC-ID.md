# Tracing API With ID Description

In this page we will describe the different API methods and how they interact with each other when the traced software has an application-level eventID. 

### When to use this API

This API allows you to map your own application-level eventID to the traceID and avoid having to explicitly propagate the context object. Use this API if you:
* Trace operations across thread boundaries and have an an application level ID assigned to each request.
* Don't trace across process boundaries.
* Don't have multiple threads working on the same request at the same time.

### How it differs from the base Tracing API

This API exposes the same functionality as [Tracing API](API-DESC.md), with slight differences:

* All methods take in an additional parameter `eventID` allowing you to map the tracing engine generated `traceID` with your application specific ID.


### Examples

#### Starting a Trace

Let's say you have a method called `onMessage(Message msg)` in which you start processing a new request. In this situation you have one option.

**Example 1** - Starting a Trace

```java
 void onMessage(Message msg) {
    Tracing.newTrace(() -> processMessage(msg), "Process Request", msg.getId());    
 }
```

In the examples, the methods have no return values, but the API provides overloads for tracing functions that return any type of value.


#### Continuing a trace

Continuing a trace is a bit trickier than starting it, since it requires accessing the current trace context in order to add the new Span as a child.

 **Example 2** - Parent and child Spans in the different thread but there is an application specific ID.
 
 In this case we have to explicitly tell the Tracing library which trace this span belongs to. If the traced application marks each new request with an ID, it becomes easy.
 
```java
 void processMessage(Message msg) {
    threadPool.submit(() -> Tracing.addToTrace(() -> processFirstPart(msg), "Process First Part", msg.getId()));    
 }
```

The library will find the trace associated to this ID and add the new span as a child. Keep in mind that to use this method you must have mapped the application specific ID to the traceID by calling `newTrace(.., String eventID)`



















   




