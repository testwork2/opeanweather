package io.openweather.data.network;

import androidx.annotation.WorkerThread;

import com.google.gson.Gson;

import io.openweather.domain.network.Converter;

public class GsonConverterImpl implements Converter {

    private final Gson gson;

    public GsonConverterImpl(Gson gson) {
        this.gson = gson;
    }

    @Override
    @WorkerThread
    public <T> T deserialize(String source, Class<T> clazz) {
        return gson.fromJson(source, clazz);
    }
}
