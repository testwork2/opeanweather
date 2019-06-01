package io.openweather.domain.network;

import java.io.IOException;

public class RestClientException extends IOException {
    public RestClientException(String message) {
        super(message);
    }
}
