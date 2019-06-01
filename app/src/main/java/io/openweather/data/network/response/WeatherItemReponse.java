package io.openweather.data.network.response;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class WeatherItemReponse {

    @SerializedName("description")
    private String description;

    @SerializedName("id")
    private int id;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
        return
                "WeatherItemReponse{" +
                        ",description = '" + description + '\'' +
                        ",id = '" + id + '\'' +
                        "}";
    }
}