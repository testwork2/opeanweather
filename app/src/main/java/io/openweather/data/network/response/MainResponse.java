package io.openweather.data.network.response;

import com.google.gson.annotations.SerializedName;

public class MainResponse {

    @SerializedName("temp")
    private double temp;

    @SerializedName("humidity")
    private double humidity;

    @SerializedName("pressure")
    private double pressure;

    public double getTemp() {
        return temp;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getPressure() {
        return pressure;
    }
}