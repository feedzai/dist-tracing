# Tracing API With Context Description

In this page we will describe the different API methods and how they interact with each other in situations where passing context explicitly is necessary.


### When to use this API

This API allows you to explicitly pass the tracing context between methods in the form of a [TraceContext](TRACECTX.md) object. Use this API if you:
* Trace operations across multiple threads and don't have an application level eventID assigned to each request.
* Trace operations that are processed by multiple threads at the same time.
* Trace operations across process boundaries.




### How it differs from the base Tracing API
This API exposes the same functionality as [Tracing API](API-DESC.md) with slight differences: 

* The `addToTrace` methods receive a [TraceContext](TRACECTX.md) that represents the context that will be the parent of this Span.



### Examples

#### Starting a Trace

Let's say you have a method called `onMessage(Message msg)` in which you start processing a new request. In this situation, since there is no parent, you do not have to propagate context.

**Example 1** - Starting a Trace.

```java
 void onMessage(Message msg) {
    Tracing.newTrace(() -> processMessage(msg), "Process Request");    
 }
```

In the examples, the methods have no return values, but the API provides overloads for tracing functions that return any type of value.


#### Continuing a trace

Continuing a trace is a bit trickier than starting it, since it requires accessing the current trace context in order to add the new Span as a child. 


**Example 1** - Parent and child Spans in the different thread pass context explicitly
 
 This case requires you to explicitly pass the [TraceContext](TRACECTX.md) to the new span.
 
```java
 void processMessage(Message msg) {
    TraceContext context = Tracing.getTraceContext();
    threadPool.submit(() -> Tracing.addToTrace(() -> processFirstPart(msg), "Process Request", context));     
 }
```

Here the library will grab the current thread's tracing context and store it in a [TraceContext](TRACECTX.md) object that you can pass when tracing the next part of the execution. Note that **obtaining the current context must be done before switching context**, i.e., you cannot do it inside the lambda.

