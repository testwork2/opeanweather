package io.openweather.domain.network;

import java.io.IOException;

public class HttpResponseException extends IOException {

    private final int code;

    public HttpResponseException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
