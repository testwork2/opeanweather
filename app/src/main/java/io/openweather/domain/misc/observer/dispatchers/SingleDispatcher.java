package io.openweather.domain.misc.observer.dispatchers;

import androidx.annotation.NonNull;

public class SingleDispatcher<T> extends ObserverDispatcherAdapter<T> {

    private SingleDispatcher() {
    }

    public static <T> SingleDispatcher<T> create() {
        return new SingleDispatcher<>();
    }

    @Override
    public void onNext(@NonNull T next) {
        super.onNext(next);
        dispose();
    }


    @Override
    public void onError(@NonNull Throwable throwable) {
        super.onError(throwable);
        dispose();
    }

}
