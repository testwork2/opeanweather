package io.openweather.domain.network;

import android.net.Uri;

public interface ServerConfig {

    Uri getApiUri();

    String getApiKey();

}
