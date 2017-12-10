package net.teslaa.flymate.datasource;

import net.teslaa.flymate.weather.Current;
import net.teslaa.flymate.weather.CurrentWeatherIcons;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * By teslaa on 12/7/17 at Flymate.
 */

public class OpenWeather extends WeatherSource {

    private static final String API_KEY = "3c1fc1665bcfc46d841ebde49c835fd2";

    public OpenWeather(WeatherSourceCallback callback) {
        super(callback);
    }


    @Override
    protected String getForecastUrl(double latitude, double longitude) {
        return "https://api.openweathermap.org/data/2.5/weather?units=metric&"
                + "lat=" + latitude +
                "&lon=" + longitude +
                "&appid=" + API_KEY;
    }

    /**
     * Builds the CurrentWeatherIcons from the data retrieved from API.
     * @param forecastData The forecast data as retrieved from the source in String form.
     * @return The complete complete current, hourly and daily forecast.
     * @throws WeatherSourceException
     */
    @Override
    protected CurrentWeatherIcons parseForecastDetails(String forecastData) throws WeatherSourceException {
        CurrentWeatherIcons forecast = new CurrentWeatherIcons();

        try {
            forecast.setCurrent(getCurrentDetails(forecastData));
        } catch (JSONException e) {
            throw new WeatherSourceException(e);
        }

        return forecast;
    }


    /**
     * Parses forecast data for the current weather conditions.
     * @param jsonData The forecast data as retrieved from the source in String form.
     * @return The Current weather conditions.
     * @throws JSONException
     */
    protected Current getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        Current current = new Current();

        String timezone = forecast.getString("name");

        JSONArray weather = forecast.getJSONArray("weather");
        JSONObject weatherObject = weather.getJSONObject(0);
        current.setSummary(weatherObject.getString("description"));
        current.setIcon(weatherObject.getString("icon"));

        JSONObject main = forecast.getJSONObject("main");
        current.setHumidity(main.getDouble("humidity"));
        current.setTemperature(main.getDouble("temp"));

        JSONObject sys = forecast.getJSONObject("sys");
        current.setTime(sys.getLong("sunrise"));

        JSONObject wind = forecast.getJSONObject("wind");
        current.setPrecipChance(wind.getDouble("speed"));

        current.setTimeZone(timezone);

        return current;
    }
}
