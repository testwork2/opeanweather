package io.openweather.domain.features.weather;

import io.openweather.domain.entities.LatLon;
import io.openweather.domain.entities.Optional;
import io.openweather.domain.entities.Weather;
import io.openweather.domain.misc.observer.ObserverConsumer;

public interface WeatherRepository {

    ObserverConsumer<Optional<Weather>> getLastWeatherData();

    ObserverConsumer<Weather> getWeatherByPlaceName(String place);

    ObserverConsumer<Weather> getWeatherByPos(LatLon latLon);

}
