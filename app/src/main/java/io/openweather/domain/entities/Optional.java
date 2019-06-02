package io.openweather.domain.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@SuppressWarnings("unchecked")
public final class Optional<T> {

    private static final Optional EMPTY = new Optional(null);

    @Nullable
    private final T data;

    private Optional(@Nullable T data) {
        this.data = data;
    }

    public static <T> Optional<T> of(@Nullable T target) {
        return target == null ? empty() : new Optional<>(target);
    }

    public static <T> Optional<T> empty() {
        return EMPTY;
    }

    public boolean isEmpty() {
        return data == null;
    }

    @Nullable
    public T getData() {
        return data;
    }

    public T getDataOrThrow() {
        if (data == null) {
            throw new IllegalArgumentException("the data is null reference");
        }
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Optional<?> optional = (Optional<?>) o;

        return data != null ? data.equals(optional.data) : optional.data == null;

    }

    @Override
    public int hashCode() {
        return data != null ? data.hashCode() : 0;
    }

    @NonNull
    @Override
    public String toString() {
        return "Optional{" +
                "data=" + data +
                '}';
    }
}
