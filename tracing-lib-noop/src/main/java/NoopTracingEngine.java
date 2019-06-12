/*
 *
 *  * Copyright 2018 Feedzai
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 *
 */

import com.feedzai.commons.tracing.api.Promise;
import com.feedzai.commons.tracing.api.TraceContext;
import com.feedzai.commons.tracing.api.TracingOpen;
import com.feedzai.commons.tracing.api.TracingOpenWithContext;
import com.feedzai.commons.tracing.api.TracingOpenWithId;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class NoopTracingEngine implements TracingOpenWithContext, TracingOpen, TracingOpenWithId {


    @Override
    public <R> Promise addToTraceOpenPromise(Supplier<Promise<R>> toTraceAsync, Object object,
                                             String description) {
        return toTraceAsync.get();
    }

    @Override
    public <R> CompletableFuture<R> addToTraceOpenFuture(Supplier<CompletableFuture<R>> toTraceAsync,
                                                         Object object, String description) {
        return toTraceAsync.get();
    }

    @Override
    public void addToTraceOpen(Runnable toTraceAsync, Object object, String description) {

    }

    @Override
    public <R> R addToTraceOpen(Supplier<R> toTraceAsync, Object value, String description) {
        return toTraceAsync.get();
    }

    @Override
    public <R> Promise addToTraceOpenPromise(Supplier<Promise<R>> toTraceAsync, Object object,
                                             String description, String eventId) {
        return toTraceAsync.get();
    }

    @Override
    public <R> CompletableFuture<R> addToTraceOpenFuture(Supplier<CompletableFuture<R>> toTraceAsync,
                                                         Object object, String description,
                                                         String eventId) {
        return toTraceAsync.get();
    }

    @Override
    public void addToTraceOpen(Runnable toTraceAsync, Object object, String description,
                               String eventId) {

    }

    @Override
    public <R> R addToTraceOpen(Supplier<R> toTraceAsync, Object value, String description,
                                String eventId) {
        return toTraceAsync.get();
    }

    @Override
    public void closeOpen(Object object) {

    }

    @Override
    public <R> Promise addToTraceOpenPromise(Supplier<Promise<R>> toTraceAsync, Object object,
                                             String description, TraceContext context) {
        return toTraceAsync.get();
    }

    @Override
    public <R> CompletableFuture<R> addToTraceOpenFuture(Supplier<CompletableFuture<R>> toTraceAsync,
                                                         Object object, String description,
                                                         TraceContext context) {
        return toTraceAsync.get();
    }

    @Override
    public void addToTraceOpen(Runnable toTraceAsync, Object object, String description,
                               TraceContext context) {

    }

    @Override
    public <R> R addToTraceOpen(Supplier<R> toTraceAsync, Object value, String description,
                                TraceContext context) {
        return toTraceAsync.get();
    }

    @Override
    public <R> R newProcess(Supplier<R> toTrace, String description, TraceContext context) {
        return toTrace.get();
    }

    @Override
    public void newProcess(Runnable toTrace, String description, TraceContext context) {

    }

    @Override
    public Promise newProcessPromise(Supplier<Promise> toTrace, String description,
                                     TraceContext context) {
        return toTrace.get();
    }

    @Override
    public <R> CompletableFuture<R> newProcessFuture(Supplier<CompletableFuture<R>> toTrace,
                                                     String description, TraceContext context) {
        return toTrace.get();
    }

    @Override
    public <R> R addToTrace(Supplier<R> toTrace, String description, TraceContext context) {
        return toTrace.get();
    }

    @Override
    public void addToTrace(Runnable toTrace, String description, TraceContext context) {

    }

    @Override
    public <R> CompletableFuture<R> addToTraceAsync(Supplier<CompletableFuture<R>> toTraceAsync, String description,
                                                    TraceContext context) {
        return toTraceAsync.get();
    }

    @Override
    public Promise addToTracePromise(Supplier<Promise> toTraceAsync, String description, TraceContext context) {
        return toTraceAsync.get();
    }

    @Override
    public Serializable serializeContext() {
        return "";
    }

    @Override
    public TraceContext deserializeContext(Serializable headers) {
        return new TraceContext() {
            @Override
            public Object get() {
                return null;
            }
        };
    }

    @Override
    public TraceContext currentContext() {
        return null;
    }

    @Override
    public TraceContext currentContextforObject(Object obj) {
        return null;
    }

    @Override
    public <R> R newTrace(Supplier<R> toTrace, String description) {
        return toTrace.get();
    }

    @Override
    public void newTrace(Runnable toTrace, String description) {

    }

    @Override
    public <R> CompletableFuture<R> newTraceAsync(Supplier<CompletableFuture<R>> toTraceAsync, String description) {
        return toTraceAsync.get();
    }

    @Override
    public Promise newTracePromise(Supplier<Promise> toTraceAsync, String description) {
        return toTraceAsync.get();
    }

    @Override
    public <R> R addToTrace(Supplier<R> toTrace, String description) {
        return toTrace.get();
    }

    @Override
    public void addToTrace(Runnable toTrace, String description) {

    }

    @Override
    public <R> CompletableFuture<R> addToTraceAsync(Supplier<CompletableFuture<R>> toTraceAsync, String description) {
        return toTraceAsync.get();
    }

    @Override
    public Promise addToTracePromise(Supplier<Promise> toTraceAsync, String description) {
        return toTraceAsync.get();
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public <R> R newTrace(Supplier<R> toTrace, String description, String eventId) {
        return toTrace.get();
    }

    @Override
    public void newTrace(Runnable toTrace, String description, String eventId) {

    }

    @Override
    public <R> CompletableFuture<R> newTraceAsync(Supplier<CompletableFuture<R>> toTraceAsync, String description,
                                                  String eventId) {
        return toTraceAsync.get();
    }

    @Override
    public Promise newTracePromise(Supplier<Promise> toTraceAsync, String description, String eventId) {
        return toTraceAsync.get();
    }

    @Override
    public <R> R newProcess(Supplier<R> toTrace, String description, String eventId) {
        return toTrace.get();
    }

    @Override
    public void newProcess(Runnable toTrace, String description, String eventId) {

    }

    @Override
    public CompletableFuture newProcessFuture(Supplier<CompletableFuture> toTrace, String description,
                                              String eventId) {
        return toTrace.get();
    }

    @Override
    public Promise newProcessPromise(Supplier<Promise> toTrace, String description, String eventId) {
        return toTrace.get();
    }

    @Override
    public <R> R addToTrace(Supplier<R> toTrace, String description, String eventId) {
        return toTrace.get();
    }

    @Override
    public void addToTrace(Runnable toTrace, String description, String eventId) {

    }

    @Override
    public <R> CompletableFuture<R> addToTraceAsync(Supplier<CompletableFuture<R>> toTraceAsync, String description,
                                                    String eventId) {
        return toTraceAsync.get();
    }

    @Override
    public Promise addToTracePromise(Supplier<Promise> toTraceAsync, String description, String eventId) {
        return toTraceAsync.get();
    }

    @Override
    public TraceContext currentContextforId(String eventId) {
        return new TraceContext() {
            @Override
            public Object get() {
                return null;
            }
        };
    }

    @Override
    public boolean traceHasStarted(String eventId) {
        return false;
    }
}
