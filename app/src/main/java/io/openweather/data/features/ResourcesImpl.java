package io.openweather.data.features;


import android.content.Context;

import androidx.annotation.NonNull;

import io.openweather.R;
import io.openweather.domain.features.Resources;

public class ResourcesImpl implements Resources {

    @NonNull
    private final Context context;

    public ResourcesImpl(@NonNull Context context) {
        this.context = context;
    }

    @Override
    public String getSettingsChangeUnavailableMessage() {
        return context.getString(R.string.settings_change_unavailable_message);
    }

    @Override
    public String getInvalidPlaceMessage() {
        return context.getString(R.string.invalid_place_name);
    }

    @Override
    public String getUnknownErrorMessage() {
        return context.getString(R.string.unknown_error);
    }

    @Override
    public String getUnknownPlaceMessage() {
        return context.getString(R.string.unknown_place);
    }

    @Override
    public String getUnknownHostException() {
        return context.getString(R.string.unknown_host_exception);
    }
}
