package io.openweather.presentation.features;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.api.ResolvableApiException;

import io.openweather.domain.entities.LatLon;
import io.openweather.domain.features.location.CheckSettingsLocationUseCase;
import io.openweather.domain.features.location.SubscribeChangingLocationUseCase;
import io.openweather.domain.misc.observer.Disposable;
import io.openweather.domain.misc.observer.ObserverSubscriber;

public class WeatherPresenter implements WeatherContract.Presenter {

    private final CheckSettingsLocationUseCase settingsLocationUseCase;
    private final SubscribeChangingLocationUseCase subscribeChangingLocationUseCase;

    @Nullable
    private Disposable locationSettingsDisposable;
    @Nullable
    private Disposable subscribeLocationDisposable;

    @Nullable
    private WeatherContract.View view;

    public WeatherPresenter(CheckSettingsLocationUseCase settingsLocationUseCase,
                            SubscribeChangingLocationUseCase subscribeChangingLocationUseCase) {
        this.settingsLocationUseCase = settingsLocationUseCase;
        this.subscribeChangingLocationUseCase = subscribeChangingLocationUseCase;
    }


    @Override
    public void attachView(WeatherContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
        disposeSettings();
        disposeLocation();
    }

    @Override
    public void requestLocation() {
        disposeSettings();
        locationSettingsDisposable = settingsLocationUseCase.execute()
                .subscribe(new ObserverSubscriber<Object>() {
                    @Override
                    public void onNext(@NonNull Object next) {
                        observeLocationUpdates();
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        WeatherContract.View view = WeatherPresenter.this.view;
                        if (view == null) {
                            return;
                        }
                        if (throwable instanceof ResolvableApiException) {
                            ResolvableApiException rae = (ResolvableApiException) throwable;
                            view.onShowLocationSettings(rae);
                        } else {
                            view.onShowError(throwable.getMessage());
                        }
                    }
                });
    }

    @Override
    public void observeLocationUpdates() {
        disposeLocation();
        subscribeLocationDisposable = subscribeChangingLocationUseCase.execute()
                .subscribe(new ObserverSubscriber<LatLon>() {
                    @Override
                    public void onNext(@NonNull LatLon next) {
                        Log.i("test", next.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        if (view != null) {
                            view.onShowError(throwable.getMessage());
                        }
                    }
                });
    }

    private void disposeSettings() {
        if (locationSettingsDisposable != null) {
            locationSettingsDisposable.dispose();
        }
    }

    private void disposeLocation() {
        if (subscribeLocationDisposable != null) {
            subscribeLocationDisposable.dispose();
        }
    }

}
