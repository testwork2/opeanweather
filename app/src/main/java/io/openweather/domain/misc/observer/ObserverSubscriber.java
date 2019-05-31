package io.openweather.domain.misc.observer;

import androidx.annotation.NonNull;

public interface ObserverSubscriber<T> {

    void onNext(@NonNull T next);

    void onError(@NonNull Throwable throwable);

}
