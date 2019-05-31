package io.openweather;

import android.app.Application;

public class WeatherApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ServiceLocator.init(this);
    }
}
