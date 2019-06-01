package io.openweather.domain.entities;

public class Weather {

    private String city;
    private String description;
    private int temp;
    private int pressure;
    private int humidity;

    public Weather(String city, String description, int temp, int pressure, int humidity) {
        this.city = city;
        this.description = description;
        this.temp = temp;
        this.pressure = pressure;
        this.humidity = humidity;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "city='" + city + '\'' +
                ", description='" + description + '\'' +
                ", temp=" + temp +
                ", pressure=" + pressure +
                ", humidity=" + humidity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Weather weather = (Weather) o;

        if (temp != weather.temp) return false;
        if (pressure != weather.pressure) return false;
        if (humidity != weather.humidity) return false;
        if (city != null ? !city.equals(weather.city) : weather.city != null) return false;
        return description != null ? description.equals(weather.description) : weather.description == null;

    }

    @Override
    public int hashCode() {
        int result = city != null ? city.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + temp;
        result = 31 * result + pressure;
        result = 31 * result + humidity;
        return result;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }
}
