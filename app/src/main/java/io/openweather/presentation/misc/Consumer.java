package io.openweather.presentation.misc;

public interface Consumer<T> {

    void accept(T target);

}
