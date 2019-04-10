# Tracing API Description

In this page we will describe the different API methods and how they interact with each other.

### When to use this API

This is the base API for systems that don't require tracing across thread context switches. If your system requires that please check:

* [Tracing API With Id Description](API-DESC-ID.md) for systems that have an application level eventID and don't trace across process boundaries.
* [Tracing API With Context Description](API-DESC-CTX.md) for systems that have no such ID and require explicit propagation of the tracing context, or trace across process boundaries.


### Main Types of Methods

The API is comprised of two main classes of methods `Tracing#newTrace` and `Tracing#addToTrace`. As the name suggests the first method will create a new trace by wrapping the traced operation as a Span with no parent, the latter will wrap the operation in a Span that is a child of the one of the ongoing traces. 

Most tracing tools ignore this distinction and simply make the first call to the tracing API  the root. However, this relies on the assumption that the each request has its own thread, or at least that the entry point of the system does not reuse threads. Since we cannot always make that assumption we decided to provide a clear way to begin a new trace.

These two types have two things in common: both take a lambda (the traced method) and a String (the span name/description) as parameters in **all** methods.



#### New Trace

These methods should be used to trace the first method executed by a new request.


#### Add To Trace

Whenever you want to continue a pre-existing trace, you should use methods called `Tracing#addToTrace(..)`.



### Examples

#### Starting a Trace

Let's say you have a method called `onMessage(Message msg)` in which you start processing a new request. In this situation all you have to do is:

**Example 1** - Starting a Trace

```java
 void onMessage(Message msg) {
    Tracing.newTrace(() -> processMessage(msg), "Process Request");    
 }
```

In the examples, the methods have no return values, but the API provides overloads for tracing functions that return any type of value.


#### Continuing a trace

Continuing a trace is a bit trickier than starting it, since it requires accessing the current trace context in order to add the new Span as a child. In cases with no multithreading it's very simple, as the Tracing library stores the trace context in thread local variables, as such all you have to do is use the base method.


**Example 2** - Parent and child Spans in the same thread.


```java
 void processMessage(Message msg) {
    Tracing.addToTrace(() -> processFirstPart(msg), "Process First Part")
}
```

The library will magically attach the new Span to the current trace.

 


















   




