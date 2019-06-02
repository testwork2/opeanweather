package io.openweather;

import io.openweather.domain.entities.Weather;

public class WeatherBuilder {
    private String place;
    private String description;
    private long updatedDate;
    private int groupId;
    private int temp;
    private int pressure;
    private int humidity;

    public WeatherBuilder setPlace(String place) {
        this.place = place;
        return this;
    }

    public WeatherBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public WeatherBuilder setUpdatedDate(long updatedDate) {
        this.updatedDate = updatedDate;
        return this;
    }

    public WeatherBuilder setGroupId(int groupId) {
        this.groupId = groupId;
        return this;
    }

    public WeatherBuilder setTemp(int temp) {
        this.temp = temp;
        return this;
    }

    public WeatherBuilder setPressure(int pressure) {
        this.pressure = pressure;
        return this;
    }

    public WeatherBuilder setHumidity(int humidity) {
        this.humidity = humidity;
        return this;
    }

    public Weather build() {
        return new Weather(place, description, updatedDate, groupId, temp, pressure, humidity);
    }
}