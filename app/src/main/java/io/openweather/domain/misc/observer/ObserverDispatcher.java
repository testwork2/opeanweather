package io.openweather.domain.misc.observer;

import androidx.annotation.NonNull;

public interface ObserverDispatcher<T> extends ObserverEmitter<T>, ObserverSubscriber<T> {

    void source(@NonNull ObserverDispatcher<T> source);

    default ObserverDispatcher<T> connect(ObserverDispatcher<T> other) {
        ObserverDispatcher<T> dispatcher = new ObserverDispatcher<T>() {
            @Override
            public void source(@NonNull ObserverDispatcher<T> source) {
                other.source(source);
            }

            @Override
            public Disposable subscribe(@NonNull ObserverSubscriber<T> subscriber) {
                return other.subscribe(subscriber);
            }

            @Override
            public void dispose() {
                other.dispose();
            }

            @Override
            public void onNext(@NonNull T next) {
                other.onNext(next);
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                other.onError(throwable);
            }
        };
        dispatcher.source(this);
        subscribe(dispatcher);
        return dispatcher;
    }

}