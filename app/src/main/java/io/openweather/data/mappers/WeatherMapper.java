package io.openweather.data.mappers;

import androidx.annotation.NonNull;

import java.util.List;

import io.openweather.data.network.response.WeatherItemReponse;
import io.openweather.data.network.response.WeatherResponse;
import io.openweather.domain.entities.Weather;
import io.openweather.domain.misc.Mapper;

public class WeatherMapper extends Mapper<WeatherResponse, Weather> {

    @Override
    public Weather map(@NonNull WeatherResponse value) {
        String description;

        List<WeatherItemReponse> items = value.getWeather();

        if (items.isEmpty()) {
            description = "";
        } else {
            WeatherItemReponse item = items.get(0);
            description = item.getDescription();
        }

        return new Weather(
                value.getName(),
                description,
                (int) value.getMain().getTemp(),
                value.getMain().getPressure(),
                value.getMain().getHumidity()
        );
    }
}
