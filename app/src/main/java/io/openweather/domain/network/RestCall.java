package io.openweather.domain.network;

import androidx.annotation.WorkerThread;

import java.io.IOException;

public interface RestCall {

    @WorkerThread
    String execute() throws IOException;

    void cancel();


}
