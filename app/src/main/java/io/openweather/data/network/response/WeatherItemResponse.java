package io.openweather.data.network.response;

import com.google.gson.annotations.SerializedName;

public class WeatherItemResponse {

    @SerializedName("description")
    private String description;

    @SerializedName("id")
    private int id;

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }
}