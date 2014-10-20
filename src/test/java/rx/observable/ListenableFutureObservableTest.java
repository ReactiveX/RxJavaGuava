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
