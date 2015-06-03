/**
 * Copyright 2014 Netflix, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rx.observable;

import static org.junit.Assert.*;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.UncheckedExecutionException;

import org.junit.Test;

import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ListenableFutureObservableTest {
    @Test
    public void testListenableFutureToObservableScheduler() {
        Object value = new Object();
        final Thread currThread = Thread.currentThread();
        assertSame(value, ListenableFutureObservable.from(Futures.immediateFuture(value), Schedulers.io()).doOnNext(new Action1<Object>() {
            @Override
            public void call(Object v) {
                assertNotSame(currThread, Thread.currentThread());
            }
        }).toBlocking().first());
    }

    @Test
    public void testListenableFutureToObservableSuccess() {
        Object value = new Object();
        assertSame(value, ListenableFutureObservable.from(Futures.immediateFuture(value), Executors.newSingleThreadExecutor()).toBlocking().first());
    }

    @Test(expected = RuntimeException.class)
    public void testListenableFutureToObservableError() {
        ListenableFutureObservable.from(Futures.immediateFailedCheckedFuture(new IllegalArgumentException()), Executors.newSingleThreadExecutor()).toBlocking().first();
    }

    @Test
    public void testObservableToListenableFutureSuccess() {
        Object value = new Object();
        assertSame(value, Futures.getUnchecked(ListenableFutureObservable.to(Observable.just(value))));
    }

    @Test(expected = UncheckedExecutionException.class)
    public void testObservableToListenableFutureError() {
        Futures.getUnchecked(ListenableFutureObservable.to(Observable.error(new IllegalArgumentException())));
    }

    @Test(expected = Exception.class)
    public void testObservableToListenableFutureInterrupt() throws Exception {
        Futures.get(ListenableFutureObservable.to(Observable.never()), 10, TimeUnit.MILLISECONDS, Exception.class);
    }
}
