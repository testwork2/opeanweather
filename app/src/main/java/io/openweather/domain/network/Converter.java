package io.openweather.domain.network;

import androidx.annotation.WorkerThread;

public interface Converter {

    @WorkerThread
    <T> T deserialize(String source, Class<T> clazz);

}
