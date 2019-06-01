package io.openweather.domain.misc.observer;

import androidx.annotation.NonNull;

public interface ObserverConsumer<T> extends Disposable {

    Disposable subscribe(@NonNull ObserverSubscriber<T> subscriber);

}
