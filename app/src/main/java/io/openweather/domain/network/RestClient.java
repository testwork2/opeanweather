package io.openweather.domain.network;

public interface RestClient {

    RestCall call(String url);

}
