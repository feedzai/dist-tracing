package com.feedzai.commons.tracing.api;

import java.util.function.Consumer;

/**
 * Represents any class that supports setting callbacks to be executed upon completion.
 *
 * @param <T> The type consumed by the onComplete method.
 * @author Gonçalo Garcia (goncalo.garcia@feedzai.com)
 */
public interface Promise<T> {


    /**
     * Registers a method to be called by the {@link Promise} when the result of the computation is ready.
     *
     * @param callOnCompletion Lambda that represents the method that should be executed upon completion
     * @return This {@link Promise}
     */
    Promise onCompletePromise(Consumer<T> callOnCompletion);

    /**
     * Registers a method to be called by the {@link Promise} when the result of the computation terminates with an
     * exception.
     *
     * @param callOnError Lambda that represents a method that will handle the exception thrown by the class
     *                    implementing {@link Promise}
     * @return This {@link Promise}
     */
    Promise onErrorPromise(Consumer<Throwable> callOnError);



}
