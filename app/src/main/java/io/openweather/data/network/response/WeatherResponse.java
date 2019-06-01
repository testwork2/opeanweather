package io.openweather.data.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherResponse {

    @SerializedName("visibility")
    private int visibility;

    @SerializedName("timezone")
    private int timezone;

    @SerializedName("main")
    private MainResponse main;

    @SerializedName("clouds")
    private CloudsResponse clouds;

    @SerializedName("dt")
    private int dt;

    @SerializedName("weather")
    private List<WeatherItemReponse> weather;

    @SerializedName("name")
    private String name;

    @SerializedName("cod")
    private int cod;

    @SerializedName("id")
    private int id;

    @SerializedName("base")
    private String base;

    @SerializedName("wind")
    private WindResponse wind;

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public int getTimezone() {
        return timezone;
    }

    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }

    public MainResponse getMain() {
        return main;
    }

    public void setMain(MainResponse main) {
        this.main = main;
    }

    public CloudsResponse getClouds() {
        return clouds;
    }

    public void setClouds(CloudsResponse clouds) {
        this.clouds = clouds;
    }

    public int getDt() {
        return dt;
    }

    public void setDt(int dt) {
        this.dt = dt;
    }

    public List<WeatherItemReponse> getWeather() {
        return weather;
    }

    public void setWeather(List<WeatherItemReponse> weather) {
        this.weather = weather;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCod() {
        return cod;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public WindResponse getWind() {
        return wind;
    }

    public void setWind(WindResponse wind) {
        this.wind = wind;
    }

    @Override
    public String toString() {
        return
                "WeatherResponse{" +
                        "visibility = '" + visibility + '\'' +
                        ",timezone = '" + timezone + '\'' +
                        ",main = '" + main + '\'' +
                        ",clouds = '" + clouds + '\'' +
                        ",dt = '" + dt + '\'' +
                        ",weather = '" + weather + '\'' +
                        ",name = '" + name + '\'' +
                        ",cod = '" + cod + '\'' +
                        ",id = '" + id + '\'' +
                        ",base = '" + base + '\'' +
                        ",wind = '" + wind + '\'' +
                        "}";
    }
}