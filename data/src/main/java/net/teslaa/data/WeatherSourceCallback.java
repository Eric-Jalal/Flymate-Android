package net.teslaa.data;

import net.teslaa.flymate.weather.CurrentWeatherIcons;

/**
 * By teslaa on 12/7/17 at Flymate.
 */

public interface WeatherSourceCallback {

    void onSuccess(CurrentWeatherIcons forecast);
    void onFailure(Exception e);
}
