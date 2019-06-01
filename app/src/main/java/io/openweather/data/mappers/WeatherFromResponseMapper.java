package io.openweather.data.mappers;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.openweather.data.network.response.WeatherItemResponse;
import io.openweather.data.network.response.WeatherResponse;
import io.openweather.domain.entities.Weather;
import io.openweather.domain.misc.Mapper;

public class WeatherFromResponseMapper extends Mapper<WeatherResponse, Weather> {

    @Override
    public Weather map(@NonNull WeatherResponse value) {
        String description;
        int groupId;

        List<WeatherItemResponse> items = value.getWeather();

        if (items.isEmpty()) {
            description = "";
            groupId = -1;
        } else {
            WeatherItemResponse item = items.get(0);
            description = item.getDescription();
            groupId = item.getId();
        }

        return new Weather(
                value.getName(),
                description,
                TimeUnit.SECONDS.toMillis(value.getDt()),
                groupId,
                (int) value.getMain().getTemp(),
                (int) value.getMain().getPressure(),
                (int) value.getMain().getHumidity()
        );
    }
}
