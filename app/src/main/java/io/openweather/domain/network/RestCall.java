package io.openweather.domain.network;

import androidx.annotation.WorkerThread;

import java.io.IOException;

public interface RestCall<T> {

    @WorkerThread
    T execute() throws IOException;

    void cancel();


}
