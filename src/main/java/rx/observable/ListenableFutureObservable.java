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

import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.ListenableFuture;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.functions.Action0;
import rx.internal.producers.SingleDelayedProducer;
import rx.Observer;
import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.Subscriber;

import java.util.concurrent.Executor;

public class ListenableFutureObservable {

    /**
     * Converts from {@link ListenableFuture} to {@link rx.Observable}.
     * 
     * @param future  the {@link ListenableFuture} to register a listener on.
     * @param scheduler  the {@link Scheduler} where the callback will be executed.  The will be where the {@link Observer#onNext(Object)} call from.
     * @return an {@link Observable} that emits the one value when the future completes.
     */
    public static <T> Observable<T> from(final ListenableFuture<T> future, final Scheduler scheduler) {
        final Worker worker = scheduler.createWorker();
        return from(future, new Executor() {
            @Override
            public void execute(final Runnable command) {
                worker.schedule(new Action0() {
                    @Override
                    public void call() {
                        try {
                            command.run();
                        } finally {
                            worker.unsubscribe();
                        }
                    }
                });
            }
        });
    }

    /**
     * Converts from {@link ListenableFuture} to {@link rx.Observable}.
     * 
     * @param future  the {@link ListenableFuture} to register a listener on.
     * @param executor  the {@link Executor} where the callback will be executed.  The will be where the {@link Observer#onNext(Object)} call from.
     * @return an {@link Observable} that emits the one value when the future completes.
     */
    public static <T> Observable<T> from(final ListenableFuture<T> future, final Executor executor) {
        return Observable.create(new OnSubscribe<T>() {
            @Override
            public void call(final Subscriber<? super T> subscriber) {
                final SingleDelayedProducer<T> sdp = new SingleDelayedProducer<T>(subscriber);
                subscriber.setProducer(sdp);
                
                future.addListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            T t = future.get();
                            sdp.setValue(t);
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                }, executor);
                
            }
        });
    }

    /**
     * Immediately subscribes to the {@link Observable} and returns a future that will contain the only one value T passed in to the
     * {@link Observer#onNext(Object)}.  If more than one value is received then an {@link Observer#onError(Throwable)} is invoked.
     * <p>
     * If the source {@link Observable} emits more than one item or no items, notify of an IllegalArgumentException or NoSuchElementException respectively.
     * 
     * @param observable  The source {@link Observable} for the value.
     * @return a {@link ListenableFuture} that sets the value on completion.
     */
    public static <T> ListenableFuture<T> to(final Observable<T> observable) {
        class ListenFutureSubscriberAdaptor extends AbstractFuture<T> {
            final Subscriber<? super T> subscriber;

            private ListenFutureSubscriberAdaptor() {
                subscriber = new Subscriber<T>() {
                    private T value;

                    @Override
                    public void onCompleted() {
                        set(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        setException(e);
                    }

                    @Override
                    public void onNext(T t) {
                        // wait for the onCompleted to make sure the observable on emits one value.
                        value = t;
                    }
                };
            }

            @Override
            protected void interruptTask() {
                subscriber.unsubscribe();
            }
        }

        ListenFutureSubscriberAdaptor future = new ListenFutureSubscriberAdaptor();

        // Futures are hot so subscribe immediately
        observable.single().subscribe(future.subscriber);

        return future;
    }
}
