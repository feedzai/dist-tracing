package com.feedzai.commons.tracing.exception;


/**
 * Exception that should be thrown when a new span is added to a new trace before {@link
 * com.feedzai.commons.tracing.api.Tracing#newTraceAsync(Supplier, String)} or {@link
 * com.feedzai.commons.tracing.api.Tracing#newTrace(Runnable, String)} or {@link com.feedzai.commons.tracing.api.Tracing#newTrace(Runnable,
 * String)} have been called.
 *
 * @author Gonçalo Garcia (goncalo.garcia@feedzai.com)
 */
public class TraceNotStartedException extends RuntimeException {
}
