/*
 * Copyright 2018 The OpenTracing Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.opentracing.contrib.hazelcast;

import static io.opentracing.contrib.hazelcast.TracingHelper.decorateExceptionally2;
import static io.opentracing.contrib.hazelcast.TracingHelper.extract;
import static io.opentracing.contrib.hazelcast.TracingHelper.nullableClass;

import io.opentracing.Span;
import io.opentracing.SpanContext;
import java.util.Map;
import java.util.concurrent.Callable;

public class TracingCallable<V> implements Callable<V> {

  private final Callable<V> callable;
  private final boolean traceWithActiveSpanOnly;
  private final Map<String, String> spanContextMap;

  public TracingCallable(Callable<V> callable, boolean traceWithActiveSpanOnly,
      Map<String, String> spanContextMap) {
    this.callable = callable;
    this.traceWithActiveSpanOnly = traceWithActiveSpanOnly;
    this.spanContextMap = spanContextMap;
  }

  @Override
  public V call() throws Exception {
    SpanContext parent = extract(spanContextMap);
    Span span = TracingHelper.buildSpan("call", parent, traceWithActiveSpanOnly);
    span.setTag("callable", nullableClass(callable));
    return decorateExceptionally2(callable::call, span);
  }
}
