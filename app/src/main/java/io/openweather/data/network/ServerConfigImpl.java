package io.openweather.data.network;

import android.net.Uri;

import io.openweather.domain.network.ServerConfig;

public class ServerConfigImpl implements ServerConfig {

    @Override
    public Uri getApiUri() {
        return Uri.parse("http://api.openweathermap.org/data/2.5");
    }

    @Override
    public String getApiKey() {
        return "2e4a1ffb0996836bad10f75b10bb5db5";
    }
}
