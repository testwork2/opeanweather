package io.openweather.domain.misc.async;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;

public interface TaskExecutor {

    @NonNull
    Executor mainExecutor();

    @NonNull
    Executor ioExecutor();

}
