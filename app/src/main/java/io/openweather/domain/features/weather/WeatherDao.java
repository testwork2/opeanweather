package io.openweather.domain.features.weather;

import io.openweather.domain.entities.Optional;
import io.openweather.domain.entities.Weather;

public interface WeatherDao {

    void insert(Weather weather);

    Optional<Weather> get();
}
