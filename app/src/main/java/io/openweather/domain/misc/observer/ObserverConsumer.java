package io.openweather.domain.misc.observer;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;

public interface ObserverConsumer<T> extends Disposable {

    @CheckResult(suggest = "#dispose()")
    Disposable subscribe(@NonNull ObserverSubscriber<T> subscriber);

}
