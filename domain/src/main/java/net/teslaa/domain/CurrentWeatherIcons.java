package net.teslaa.domain;

import net.teslaa.flymate.R;

/**
 * By teslaa on 12/7/17 at Flymate.
 */
public class CurrentWeatherIcons {
    private Current mCurrent;

    public Current getCurrent() {
        return mCurrent;
    }

    public void setCurrent(Current current) {
        mCurrent = current;
    }

    public static int getIconId(String iconString) {
        // clear-day, clear-night, rain, snow, sleet, wind, fog, cloudy, partly-cloudy-day, or partly-cloudy-night
        int iconId = R.drawable.clear_day;

        if (iconString.equals("01d")) {
            iconId = R.drawable.clear_day;
        }
        else if (iconString.equals("01n")) {
            iconId = R.drawable.clear_night;
        }
        else if (iconString.equals("09d") || iconString.equals("09n") || iconString.equals("10d") || iconString.equals("10n")) {
            iconId = R.drawable.rain;
        }
        else if (iconString.equals("13d") || iconString.equals("13n")) {
            iconId = R.drawable.snow;
        }
        else if (iconString.equals("11d") || iconString.equals("11n")) {
            iconId = R.drawable.wind;
        }
        else if (iconString.equals("50d") || iconString.equals("50n")) {
            iconId = R.drawable.fog;
        }
        else if (iconString.equals("03d") || iconString.equals("03n")) {
            iconId = R.drawable.cloudy;
        }
        else if (iconString.equals("02d") || iconString.equals("04d")) {
            iconId = R.drawable.partly_cloudy;
        }
        else if (iconString.equals("04n") || iconString.equals("02n")) {
            iconId = R.drawable.cloudy_night;
        }
        return iconId;
    }
}