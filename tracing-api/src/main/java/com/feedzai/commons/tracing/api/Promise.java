package com.feedzai.commons.tracing.api;

import java.util.function.Consumer;

/**
 * Represents any class that supports setting callbacks to be executed upon completion.
 *
 * @author Gonçalo Garcia (goncalo.garcia@feedzai.com)
 */

public interface Promise {


    /**
     * Registers a method to be called by the {@link Promise} when the result of the computation is ready.
     *
     * @param callbackMethod Lambda that represents the method that should be executed upon completion
     * @return This {@link Promise}
     */
    Promise onComplete(Consumer<Promise> callbackMethod);


}
