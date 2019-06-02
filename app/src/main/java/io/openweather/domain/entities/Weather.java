package io.openweather.domain.entities;

public final class Weather {

    private String place;
    private String description;
    private long updatedDate;
    private int groupId;
    private int temp;
    private int pressure;
    private int humidity;

    public Weather(String place, String description, long updatedDate, int groupId, int temp, int pressure, int humidity) {
        this.place = place;
        this.description = description;
        this.updatedDate = updatedDate;
        this.groupId = groupId;
        this.temp = temp;
        this.pressure = pressure;
        this.humidity = humidity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Weather weather = (Weather) o;

        if (updatedDate != weather.updatedDate) return false;
        if (groupId != weather.groupId) return false;
        if (temp != weather.temp) return false;
        if (pressure != weather.pressure) return false;
        if (humidity != weather.humidity) return false;
        if (place != null ? !place.equals(weather.place) : weather.place != null) return false;
        return description != null ? description.equals(weather.description) : weather.description == null;

    }

    @Override
    public int hashCode() {
        int result = place != null ? place.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (int) (updatedDate ^ (updatedDate >>> 32));
        result = 31 * result + groupId;
        result = 31 * result + temp;
        result = 31 * result + pressure;
        result = 31 * result + humidity;
        return result;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "place='" + place + '\'' +
                ", description='" + description + '\'' +
                ", updatedDate=" + updatedDate +
                ", groupId=" + groupId +
                ", temp=" + temp +
                ", pressure=" + pressure +
                ", humidity=" + humidity +
                '}';
    }

    public String getPlace() {
        return place;
    }

    public String getDescription() {
        return description;
    }

    public long getUpdatedDate() {
        return updatedDate;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getTemp() {
        return temp;
    }

    public String getTempWithSign() {
        return temp > 0 ? "+" + temp : String.valueOf(temp);
    }

    public int getPressure() {
        return pressure;
    }

    public int getHumidity() {
        return humidity;
    }
}
