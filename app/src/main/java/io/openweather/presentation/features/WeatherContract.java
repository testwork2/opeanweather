package io.openweather.presentation.features;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.api.ResolvableApiException;

import io.openweather.domain.entities.Weather;

public interface WeatherContract {

    interface View {

        void onShowError(@NonNull String message);

        void onShowProgress(boolean isLoading);

        void onShowLocationSettings(@NonNull ResolvableApiException e);

        void onWeatherLoaded(@NonNull Weather weather);

        void onStateChanged(InputState state);

        void requestLocationWithPermission();

    }

    interface Presenter {

        void attachView(@NonNull View view);

        void detachView();

        void loadWeather();

        void requestLocationSettings();

        void observeLocationUpdates();

        void onEditPlaceClick();

        void onSavePlaceClick(@Nullable CharSequence place);

        void onLocationClick();

    }


}
