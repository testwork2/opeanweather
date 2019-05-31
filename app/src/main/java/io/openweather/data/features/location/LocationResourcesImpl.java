package io.openweather.data.features.location;


import android.content.Context;

import androidx.annotation.NonNull;

import io.openweather.R;
import io.openweather.domain.features.location.LocationResources;

public class LocationResourcesImpl implements LocationResources {

    @NonNull
    private final Context context;

    public LocationResourcesImpl(@NonNull Context context) {
        this.context = context;
    }

    @Override
    public String getSettingsChangeUnavailableMessage() {
        return context.getString(R.string.settingsChangeUnavailableMessage);
    }
}
