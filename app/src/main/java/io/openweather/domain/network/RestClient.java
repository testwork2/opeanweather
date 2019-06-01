package io.openweather.domain.network;

import androidx.annotation.Nullable;

public interface RestClient {

    @Nullable
    RestCall<String> call(String url);

}
