package io.openweather.domain.features.location;

import androidx.annotation.NonNull;

import io.openweather.domain.entities.LatLon;
import io.openweather.domain.misc.observer.ObserverDispatcher;

public interface LocationProvider {

    ObserverDispatcher<Object> checkSettings();

    ObserverDispatcher<LatLon> observeLocation();

    float distanceBetween(@NonNull LatLon l1, @NonNull LatLon l2);

}
