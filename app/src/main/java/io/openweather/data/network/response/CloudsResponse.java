package io.openweather.data.network.response;

import com.google.gson.annotations.SerializedName;

public class CloudsResponse {

    @SerializedName("all")
    private int all;

    public int getAll() {
        return all;
    }

    public void setAll(int all) {
        this.all = all;
    }

    @Override
    public String toString() {
        return
                "CloudsResponse{" +
                        "all = '" + all + '\'' +
                        "}";
    }
}