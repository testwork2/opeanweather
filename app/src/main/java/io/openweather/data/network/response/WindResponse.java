package io.openweather.data.network.response;

import com.google.gson.annotations.SerializedName;

public class WindResponse {

    @SerializedName("deg")
    private int deg;

    @SerializedName("speed")
    private int speed;

    public int getDeg() {
        return deg;
    }

    public void setDeg(int deg) {
        this.deg = deg;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @Override
    public String toString() {
        return
                "WindResponse{" +
                        "deg = '" + deg + '\'' +
                        ",speed = '" + speed + '\'' +
                        "}";
    }
}