package io.openweather.presentation.features;

import com.google.android.gms.common.api.ResolvableApiException;

public interface WeatherContract {

    interface View {

        void onShowError(String message);

        void onShowProgress(boolean isLoading);

        void onShowLocationSettings(ResolvableApiException e);

    }

    interface Presenter {

        void attachView(View view);

        void detachView();

        void requestLocation();

        void observeLocationUpdates();
    }


}
