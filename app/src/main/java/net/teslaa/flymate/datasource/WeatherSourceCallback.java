package net.teslaa.flymate.datasource;

import net.teslaa.flymate.weather.CurrentWeatherIcons;

/**
 * By teslaa on 12/7/17 at Flymate.
 */

public interface WeatherSourceCallback {

    /**
     * Called when the forecast is successfully retrieved and parsed.
     * @param forecast The forecast that was retrieved by the WeatherSource in the background.
     */
    void onSuccess(CurrentWeatherIcons forecast);

    /**
     *  Called if we weren't able to retrieve the weather forecast for whatever reason.
     */
    void onFailure(Exception e);
}
