package io.openweather.data.network.response;

import com.google.gson.annotations.SerializedName;

public class MainResponse {

    @SerializedName("temp")
    private double temp;

    @SerializedName("temp_min")
    private int tempMin;

    @SerializedName("humidity")
    private int humidity;

    @SerializedName("pressure")
    private int pressure;

    @SerializedName("temp_max")
    private double tempMax;

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public int getTempMin() {
        return tempMin;
    }

    public void setTempMin(int tempMin) {
        this.tempMin = tempMin;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public double getTempMax() {
        return tempMax;
    }

    public void setTempMax(double tempMax) {
        this.tempMax = tempMax;
    }

    @Override
    public String toString() {
        return
                "MainResponse{" +
                        "temp = '" + temp + '\'' +
                        ",temp_min = '" + tempMin + '\'' +
                        ",humidity = '" + humidity + '\'' +
                        ",pressure = '" + pressure + '\'' +
                        ",temp_max = '" + tempMax + '\'' +
                        "}";
    }
}