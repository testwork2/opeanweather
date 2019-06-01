package io.openweather.domain.misc.observer.dispatchers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.openweather.domain.misc.observer.Disposable;
import io.openweather.domain.misc.observer.ObserverDispatcher;
import io.openweather.domain.misc.observer.ObserverSubscriber;

public abstract class ObserverDispatcherAdapter<T> implements ObserverDispatcher<T> {

    @Nullable
    protected ObserverSubscriber<T> subscriber;

    @Nullable
    protected ObserverDispatcher<T> source;

    @Override
    public void source(@NonNull ObserverDispatcher<T> source) {
        this.source = source;
    }

    @Override
    public Disposable subscribe(@NonNull ObserverSubscriber<T> subscriber) {
        this.subscriber = subscriber;
        try {
            subscribeActual(subscriber);
        } catch (Throwable throwable) {
            onError(throwable);
            dispose();
        }
        return this;
    }

    @Override
    public void dispose() {
        this.subscriber = null;
        if (source != null) {
            source.dispose();
        }
    }

    @Override
    public void onNext(@NonNull T next) {
        if (subscriber != null) {
            subscriber.onNext(next);
        }
    }

    @Override
    public void onError(@NonNull Throwable throwable) {
        if (subscriber != null) {
            subscriber.onError(throwable);
        }
    }

    protected void subscribeActual(@NonNull ObserverSubscriber<T> subscriber) {
        //no-op
    }
}
