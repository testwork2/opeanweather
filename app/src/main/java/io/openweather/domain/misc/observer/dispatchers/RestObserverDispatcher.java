package io.openweather.domain.misc.observer.dispatchers;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;

import io.openweather.domain.misc.observer.Function;
import io.openweather.domain.misc.observer.ObserverSubscriber;
import io.openweather.domain.network.RequestCancelledException;
import io.openweather.domain.network.RestCall;

@SuppressWarnings("WeakerAccess")
public class RestObserverDispatcher<T> extends ObserverDispatcherAdapter<T> {

    private final RestCall restCall;
    private final Executor fetchExecutor;
    private final Executor notifyExecutor;
    private final Function<String, T> mapperFunction;

    public RestObserverDispatcher(RestCall restCall,
                                  Executor fetchExecutor,
                                  Executor notifyExecutor,
                                  Function<String, T> mapperFunction) {
        this.restCall = restCall;
        this.fetchExecutor = fetchExecutor;
        this.notifyExecutor = notifyExecutor;
        this.mapperFunction = mapperFunction;

    }

    @Override
    protected void subscribeActual(@NonNull ObserverSubscriber<T> subscriber) {
        fetchExecutor.execute(() -> {
            try {
                T result = mapperFunction.apply(restCall.execute());
                handleResult(result);
            } catch (RequestCancelledException cancelled) {
                //ignore
            } catch (Throwable throwable) {
                handleError(throwable);
            }
        });
    }


    @Override
    public void dispose() {
        super.dispose();
        if (restCall != null) {
            restCall.cancel();
        }
    }

    protected void handleResult(T result) {
        notifyExecutor.execute(() -> onNext(result));
    }

    protected void handleError(Throwable throwable) {
        notifyExecutor.execute(() -> onError(throwable));
    }
}
