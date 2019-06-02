package io.openweather.domain.entities;

import androidx.annotation.Nullable;

public final class ValidationResult<T> {

    @Nullable private T result;
    @Nullable private Throwable throwable;

    public ValidationResult(@Nullable T result) {
        this.result = result;
    }

    public ValidationResult(@Nullable Throwable throwable) {
        this.throwable = throwable;
    }

    @Nullable
    public T getResult() {
        return result;
    }

    @Nullable
    public Throwable getThrowable() {
        return throwable;
    }

    public boolean isSuccess() {
        return result != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValidationResult<?> that = (ValidationResult<?>) o;

        if (result != null ? !result.equals(that.result) : that.result != null) return false;
        return throwable != null ? throwable.equals(that.throwable) : that.throwable == null;

    }

    @Override
    public int hashCode() {
        int result1 = result != null ? result.hashCode() : 0;
        result1 = 31 * result1 + (throwable != null ? throwable.hashCode() : 0);
        return result1;
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "result=" + result +
                ", throwable=" + throwable +
                '}';
    }
}
