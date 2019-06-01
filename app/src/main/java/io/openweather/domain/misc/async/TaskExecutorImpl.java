package io.openweather.domain.misc.async;


import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskExecutorImpl implements TaskExecutor {

    private static volatile TaskExecutorImpl sInstance;
    @NonNull
    private static final Executor sMainThreadExecutor = command -> getInstance().postToMainThread(command);
    @NonNull
    private static final Executor sIOThreadExecutor = command -> getInstance().executeOnIO(command);

    private final Object lock = new Object();

    private final ExecutorService io = Executors.newFixedThreadPool(4, new ThreadFactory() {
        private static final String THREAD_NAME_STEM = "io_%d";

        private final AtomicInteger threadId = new AtomicInteger(0);

        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread t = new Thread(r);
            t.setName(String.format(Locale.US, THREAD_NAME_STEM, threadId.getAndIncrement()));
            return t;
        }
    });

    @Nullable
    private volatile Handler mainHandler;

    @NonNull
    public static TaskExecutorImpl getInstance() {
        if (sInstance != null) {
            return sInstance;
        }
        synchronized (TaskExecutorImpl.class) {
            if (sInstance == null) {
                sInstance = new TaskExecutorImpl();
            }
        }
        return sInstance;
    }

    private static Handler createAsync(@NonNull Looper looper) {
        if (Build.VERSION.SDK_INT >= 28) {
            return Handler.createAsync(looper);
        }
        return new Handler(looper);
    }

    @Override
    @NonNull
    public Executor mainExecutor() {
        return sMainThreadExecutor;
    }

    @Override
    @NonNull
    public Executor ioExecutor() {
        return sIOThreadExecutor;
    }


    private void executeOnIO(Runnable runnable) {
        io.execute(runnable);
    }

    private void postToMainThread(Runnable runnable) {
        if (mainHandler == null) {
            synchronized (lock) {
                if (mainHandler == null) {
                    mainHandler = createAsync(Looper.getMainLooper());
                }
            }
        }
        //noinspection ConstantConditions
        mainHandler.post(runnable);
    }


}
