package io.openweather.presentation.features;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.api.ResolvableApiException;

import io.openweather.domain.entities.LatLon;
import io.openweather.domain.entities.Result;
import io.openweather.domain.entities.Weather;
import io.openweather.domain.features.location.CheckSettingsLocationUseCase;
import io.openweather.domain.features.location.LocationRepository;
import io.openweather.domain.features.location.SubscribeChangingLocationUseCase;
import io.openweather.domain.misc.observer.Disposable;
import io.openweather.domain.misc.observer.ObserverSubscriber;

public class WeatherPresenter implements WeatherContract.Presenter {

    private static final String TAG = "WeatherPresenter";

    private final CheckSettingsLocationUseCase settingsLocationUseCase;
    private final SubscribeChangingLocationUseCase subscribeChangingLocationUseCase;
    private final LocationRepository locationRepository;

    @Nullable private WeatherContract.View view;

    @Nullable private Disposable locationSettingsDisposable;
    @Nullable private Disposable subscribeLocationDisposable;
    @Nullable private Disposable loadWeatherDisposable;

    public WeatherPresenter(CheckSettingsLocationUseCase settingsLocationUseCase,
                            SubscribeChangingLocationUseCase subscribeChangingLocationUseCase,
                            LocationRepository locationRepository) {
        this.settingsLocationUseCase = settingsLocationUseCase;
        this.subscribeChangingLocationUseCase = subscribeChangingLocationUseCase;
        this.locationRepository = locationRepository;
    }

    @Override
    public void attachView(@NonNull WeatherContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
        disposeSettings();
        disposeLocation();
        disposeLoadingWeather();
    }

    @Override
    public void loadWeather() {
        requestLocationSettings();
    }

    @Override
    public void requestLocationSettings() {
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
        switchProgress(true);
        subscribeLocationDisposable = subscribeChangingLocationUseCase.execute()
                .subscribe(new ObserverSubscriber<LatLon>() {
                    @Override
                    public void onNext(@NonNull LatLon next) {
                        Log.d(TAG, "onNext() called with: next = [" + next + "]");
                        loadWeatherByPos(next);
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        switchProgress(false);
                        showError(throwable);
                    }
                });
    }

    @Override
    public void onEditCityClick() {
        if (view != null) {
            view.onStateChanged(InputState.EDITABLE);
        }
    }

    @Override
    public void onSaveCityClick(@Nullable CharSequence city) {
        if (view != null) {
            view.onStateChanged(InputState.DISABLE);
            //todo
        }
    }

    @Override
    public void onLocationClick() {
        if (view != null) {
            view.requestLocationWithPermission();
            view.onStateChanged(InputState.DISABLE);
        }
    }

    private void loadWeatherByPos(LatLon latLon) {
        disposeLoadingWeather();
        loadWeatherDisposable = locationRepository.getWeatherByPos(latLon)
                .subscribe(new ObserverSubscriber<Result<Weather>>() {
                    @Override
                    public void onNext(@NonNull Result<Weather> next) {
                        Log.d(TAG, "onNext() called with: next = [" + next + "]");
                        switchProgress(false);
                        handleResult(next);
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Log.e(TAG, "onError: ", throwable);
                        switchProgress(false);
                        showError(throwable);
                    }
                });
    }

    private void handleResult(@NonNull Result<Weather> next) {
        if (view == null) {
            return;
        }
        Weather result = next.getResult();
        if (result == null) {
            Throwable throwable = next.getThrowable();
            view.onShowError(throwable != null ? throwable.getMessage() : null);
        } else {
            view.onWeatherLoaded(result);
        }
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

    private void disposeLoadingWeather() {
        if (loadWeatherDisposable != null) {
            loadWeatherDisposable.dispose();
        }
    }

    private void switchProgress(boolean isLoading) {
        if (view != null) {
            view.onShowProgress(isLoading);
        }
    }

    private void showError(@NonNull Throwable throwable) {
        if (view != null) {
            view.onShowError(throwable.getMessage());
        }
    }

}
