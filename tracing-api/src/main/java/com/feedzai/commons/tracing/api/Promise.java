/*
 * Copyright 2018 Feedzai
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    <P extends Promise<R>, R> P onCompletePromise(Consumer<T> callOnCompletion);

    /**
     * Registers a method to be called by the {@link Promise} when the result of the computation terminates with an
     * exception.
     *
     * @param callOnError Lambda that represents a method that will handle the exception thrown by the class
     *                    implementing {@link Promise}
     * @return This {@link Promise}
     */
    <P extends Promise<R>, R> P onErrorPromise(Consumer<Throwable> callOnError);



}
