package io.openweather;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import io.openweather.domain.misc.observer.Disposable;
import io.openweather.domain.misc.observer.ObserverConsumer;
import io.openweather.domain.misc.observer.ObserverSubscriber;

@SuppressWarnings({"unused", "WeakerAccess"})
public class ObserverTestConsumer<T> implements ObserverConsumer<T> {

    private final List<T> items = new ArrayList<>();
    private Throwable throwable;
    private boolean disposed;
    private ObserverSubscriber<T> subscriber;

    public ObserverTestConsumer() {
    }


    public ObserverTestConsumer(List<T> items) {
        this.items.addAll(items);
    }

    public ObserverTestConsumer(T item) {
        this.items.add(item);
    }

    public ObserverTestConsumer(Throwable throwable) {
        this.throwable = throwable;
    }

    public void pushData(T item) {
        items.add(item);
        subscriber.onNext(item);
    }

    @SafeVarargs
    public final void pushData(T... items) {
        pushData(Arrays.asList(items));
    }

    public void pushData(List<T> items) {
        this.items.addAll(items);
        for (T item : items) {
            subscriber.onNext(item);
        }
    }

    public void pushError(Throwable throwable) {
        this.throwable = throwable;
        subscriber.onError(throwable);
    }

    public boolean isDisposed() {
        return disposed;
    }

    @Override
    public Disposable subscribe(@NonNull ObserverSubscriber<T> subscriber) {
        this.subscriber = subscriber;
        pushError(throwable);
        pushData(items);
        return this;
    }

    @Override
    public void dispose() {
        disposed = true;
    }

}
