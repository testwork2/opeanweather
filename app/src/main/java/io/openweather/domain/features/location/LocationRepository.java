package io.openweather.domain.features.location;

import io.openweather.domain.entities.LatLon;
import io.openweather.domain.entities.Result;
import io.openweather.domain.entities.Weather;
import io.openweather.domain.misc.observer.ObserverConsumer;

public interface LocationRepository {

    ObserverConsumer<Result<Weather>> getWeatherByCityName(String city);

    ObserverConsumer<Result<Weather>> getWeatherByPos(LatLon latLon);

}
