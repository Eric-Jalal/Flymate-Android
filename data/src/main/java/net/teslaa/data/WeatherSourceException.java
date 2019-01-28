package net.teslaa.data;

/**
 * By teslaa on 12/7/17 at Flymate.
 */

class WeatherSourceException extends Exception {

    WeatherSourceException(String detailMessage) {
        super(detailMessage);
    }
    WeatherSourceException(Throwable throwable) {
        super(throwable);
    }
}
