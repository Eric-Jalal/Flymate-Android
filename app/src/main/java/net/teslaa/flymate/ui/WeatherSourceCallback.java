package net.teslaa.flymate.ui;

/**
 * By teslaa on 12/7/17 at Flymate.
 */

public interface WeatherSourceCallback {

    void onSuccess(CurrentWeatherIcons forecast);
    void onFailure(Exception e);
}
