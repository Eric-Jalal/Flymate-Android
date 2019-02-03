package net.teslaa.flymate.ui;

/**
 * By teslaa on 12/7/17 at Flymate.
 */

import java.io.IOException;

public abstract class WeatherSource {

  public static final String TAG = WeatherSource.class.getSimpleName();

  private final WeatherSourceCallback mCallback;

  WeatherSource(WeatherSourceCallback callback) {
    mCallback = callback;
  }

  public void getForecast(double latitude, double longitude) {
    String forecastUrl = getForecastUrl(latitude, longitude);

    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder().url(forecastUrl).build();
    Call call = client.newCall(request);
    call.enqueue(new Callback() {
      @Override
      public void onFailure(Request request, IOException e) {
        mCallback.onFailure(e);
      }

      @Override
      public void onResponse(Response response) throws IOException {
        try {
          String forecastData = response.body().string();
          if (response.isSuccessful()) {
            CurrentWeatherIcons forecast = parseForecastDetails(forecastData);
            mCallback.onSuccess(forecast);
          } else {
            mCallback.onFailure(
                new WeatherSourceException("Request from forecast service was not successful."));
          }
        } catch (IOException | WeatherSourceException e) {
          mCallback.onFailure(e);
        }
      }
    });
  }

  protected abstract CurrentWeatherIcons parseForecastDetails(String forecastData)
      throws WeatherSourceException;

  protected abstract String getForecastUrl(double latitude, double longitude);

}
