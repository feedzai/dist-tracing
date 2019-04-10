# Promises

In most applications it's faster to do certain things asynchronously. Promises allow us to create an object that represents an asynchronous computation and may finish successfully or with an exception. 

Tracing calls to Promises does not allow us to capture the full duration of the asynchronous execution, so we have to attach a method to the promises that close the Span whenever it finishes.

Since different systems can implement their own promise classes we provide a simple interface that can be implemented by the traced systems in order to make their Promise implementations compatible with our tracing API.

The interface has two simple methods:
* `Promise#OnComplete(Function<Promise, Promise>)` that executes if the promise completes successfully.
* `Promise#OnError(Function<Promise, Promise>)` that executes if the promise completes exceptionally.

These methods allow us to pass a function that takes a promise as argument and returns it, which closes the span whenever it should be completed.

### How to use this in your projects
Make the classes that represent asynchronous computations implement the Promise API.

### Can I use this with CompletableFuture?
No, since the tracing API for Promises requires that the objects using it implement the Promise interface, which can't be done with Completable Future. However, all of the tracing APIs have overloads that provide the same functionality for methods returning CompletableFuture


### Example

Let's say we have a long running computation that we want to trace and takes 500ms. The following code would fail to trace the full execution 

**Example 1** - What not to do.

```java
 void onMessage(Message msg) {
    Promise promise = Tracing.newTrace(() -> longRunningComputation(msg), "Process Request", msg.getId());    
 }
```
This will close the span when the call finishes and not when the computation finishes!

**Example 1** - Doing it right.


```java
 void onMessage(Message msg) {
    Promise promise = Tracing.newTracePromise(() -> longRunningComputation(msg), "Process First Part", msg.getId());    
 }
```

This will close the span when `Promise#OnComplete` or `Promise#OnError` finishes.