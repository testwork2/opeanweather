package io.openweather.domain.entities;

import androidx.annotation.Nullable;

public class Result<T> {

    @Nullable
    private final T remote;
    @Nullable
    private final T local;
    @Nullable
    private final Throwable throwable;

    private Result(@Nullable T remote, @Nullable T local, @Nullable Throwable throwable) {
        this.remote = remote;
        this.local = local;
        this.throwable = throwable;
    }

    @Nullable
    public T getRemote() {
        return remote;
    }

    @Nullable
    public T getLocal() {
        return local;
    }

    @Nullable
    public Throwable getThrowable() {
        return throwable;
    }

    @Nullable
    public T getResult() {
        return remote != null ? remote : local;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Result<?> result = (Result<?>) o;

        if (remote != null ? !remote.equals(result.remote) : result.remote != null) return false;
        if (local != null ? !local.equals(result.local) : result.local != null) return false;
        return throwable != null ? throwable.equals(result.throwable) : result.throwable == null;

    }

    @Override
    public int hashCode() {
        int result = remote != null ? remote.hashCode() : 0;
        result = 31 * result + (local != null ? local.hashCode() : 0);
        result = 31 * result + (throwable != null ? throwable.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Result{" +
                "remote=" + remote +
                ", local=" + local +
                ", throwable=" + throwable +
                '}';
    }

    public static class Builder<T> {

        private T remote;
        private T local;
        private Throwable throwable;

        public Builder<T> setRemote(T remote) {
            this.remote = remote;
            return this;
        }

        public Builder<T> setLocal(T local) {
            this.local = local;
            return this;
        }

        public Builder<T> setThrowable(Throwable throwable) {
            this.throwable = throwable;
            return this;
        }

        public Result<T> build() {
            return new Result<>(remote, local, throwable);
        }
    }
}
