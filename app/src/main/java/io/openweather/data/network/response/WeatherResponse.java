package io.openweather.data.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherResponse {

    @SerializedName("main")
    private MainResponse main;

    @SerializedName("dt")
    private long dt;

    @SerializedName("name")
    private String name;

    @SerializedName("weather")
    private List<WeatherItemResponse> weather;

    @SerializedName("wind")
    private WindResponse wind;

    public String getName() {
        return name;
    }

    public MainResponse getMain() {
        return main;
    }

    public long getDt() {
        return dt;
    }

    public List<WeatherItemResponse> getWeather() {
        return weather;
    }

    public WindResponse getWind() {
        return wind;
    }
}