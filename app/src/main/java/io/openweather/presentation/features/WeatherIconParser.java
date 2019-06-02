package io.openweather.presentation.features;

import androidx.annotation.DrawableRes;

import io.openweather.R;

class WeatherIconParser {

    /**
     * Find a icon by group id ("https://openweathermap.org/weather-conditions#")
     *
     * @return drawable res id
     */
    @DrawableRes
    int parseGroupId(int groupId) {
        if (inRange(groupId, 200, 300)) {
            return R.drawable.ic_lightning;
        } else if (inRange(groupId, 300, 400)) {
            return R.drawable.ic_drizzle;
        } else if (inRange(groupId, 500, 600)) {
            return R.drawable.ic_rainy;
        } else if (inRange(groupId, 600, 700)) {
            return R.drawable.ic_snowing;
        } else if (inRange(groupId, 700, 800)) {
            return R.drawable.ic_cloud;
        } else if (groupId == 800) {
            return R.drawable.ic_sun;
        } else if (inRange(groupId, 800, 900)) {
            return R.drawable.ic_clouds;
        } else {
            return R.drawable.ic_sun;
        }
    }

    private boolean inRange(int groupId, int start, int end) {
        return groupId >= start && groupId < end;
    }

}
