package io.openweather.domain.network;

import android.os.NetworkOnMainThreadException;

import androidx.annotation.WorkerThread;

import java.io.IOException;

public interface RestCall {

    @WorkerThread
    String execute() throws IOException, NetworkOnMainThreadException;

    void cancel();


}
