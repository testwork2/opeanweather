package io.openweather.domain.network;

public enum HttpCodes {

    /* 4XX: client error */

    /**
     * HTTP Status-Code 404: Not Found.
     */
    HTTP_NOT_FOUND(404);

    private final int code;

    HttpCodes(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public boolean equal(int code) {
        return this.getCode() == code;
    }

}
