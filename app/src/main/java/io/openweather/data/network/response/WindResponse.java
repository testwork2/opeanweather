package io.openweather.data.network.response;

import com.google.gson.annotations.SerializedName;

public class WindResponse {

    @SerializedName("deg")
    private double deg;

    @SerializedName("speed")
    private double speed;

    public double getDeg() {
        return deg;
    }

    public double getSpeed() {
        return speed;
    }
}