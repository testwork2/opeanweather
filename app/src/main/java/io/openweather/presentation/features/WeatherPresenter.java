package io.openweather.presentation.features;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.api.ResolvableApiException;

import java.net.UnknownHostException;

import androidx.annotation.VisibleForTesting;
import io.openweather.domain.entities.LatLon;
import io.openweather.domain.entities.Optional;
import io.openweather.domain.entities.ValidationResult;
import io.openweather.domain.entities.Weather;
import io.openweather.domain.features.Resources;
import io.openweather.domain.features.weather.CheckSettingsLocationUseCase;
import io.openweather.domain.features.weather.SubscribeChangingLocationUseCase;
import io.openweather.domain.features.weather.ValidatePlaceNameUseCase;
import io.openweather.domain.features.weather.WeatherRepository;
import io.openweather.domain.misc.observer.Disposable;
import io.openweather.domain.misc.observer.ObserverSubscriber;
import io.openweather.domain.network.HttpCodes;
import io.openweather.domain.network.HttpResponseException;
import io.openweather.presentation.misc.Consumer;

public class WeatherPresenter implements WeatherContract.Presenter {

    private static final String TAG = "WeatherPresenter";

    private final CheckSettingsLocationUseCase settingsLocationUseCase;
    private final SubscribeChangingLocationUseCase subscribeChangingLocationUseCase;
    private final WeatherRepository weatherRepository;
    private final ValidatePlaceNameUseCase validatePlaceNameUseCase;
    private final Resources resources;

    @Nullable private WeatherContract.View view;

    @Nullable private Disposable locationSettingsDisposable;
    @Nullable private Disposable subscribeLocationDisposable;
    @Nullable private Disposable loadWeatherDisposable;
    @Nullable private Disposable lastWeatherDisposable;

    public WeatherPresenter(CheckSettingsLocationUseCase settingsLocationUseCase,
                            SubscribeChangingLocationUseCase subscribeChangingLocationUseCase,
                            WeatherRepository weatherRepository,
                            ValidatePlaceNameUseCase validatePlaceNameUseCase, Resources resources) {
        this.settingsLocationUseCase = settingsLocationUseCase;
        this.subscribeChangingLocationUseCase = subscribeChangingLocationUseCase;
        this.weatherRepository = weatherRepository;
        this.validatePlaceNameUseCase = validatePlaceNameUseCase;
        this.resources = resources;
    }

    @Override
    public void attachView(@NonNull WeatherContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
        dispose(locationSettingsDisposable);
        dispose(subscribeLocationDisposable);
        dispose(loadWeatherDisposable);
        dispose(lastWeatherDisposable);
    }

    @Override
    public void loadWeather() {
        dispose(lastWeatherDisposable);
        lastWeatherDisposable = weatherRepository.getLastWeatherData()
                .subscribe(new ObserverSubscriber<Optional<Weather>>() {
                    @Override
                    public void onNext(@NonNull Optional<Weather> next) {
                        if (next.isEmpty()) {
                            invokeView(WeatherContract.View::requestLocationWithPermission);
                        } else {
                            Weather weather = next.getDataOrThrow();
                            invokeView(v -> v.onWeatherLoaded(weather));
                            //update the weather for the saved place
                            loadWeatherByPlace(weather.getPlace());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Log.e(TAG, "Error while loading the last weather data", throwable);
                        invokeView(WeatherContract.View::requestLocationWithPermission);
                    }
                });
    }

    @Override
    public void requestLocationSettings() {
        dispose(locationSettingsDisposable);
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
        dispose(subscribeLocationDisposable);
        switchProgress(true);
        switchInputState(InputState.DISABLED);
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
                        switchInputState(InputState.DEFAULT);
                    }
                });
    }

    @Override
    public void onEditPlaceClick() {
        switchInputState(InputState.EDITABLE);
    }

    @Override
    public void onSavePlaceClick(@Nullable CharSequence place) {
        ValidationResult<String> result = validatePlaceNameUseCase.execute(place);
        if (result.isSuccess()) {
            switchProgress(true);
            switchInputState(InputState.DISABLED);
            loadWeatherByPlace(result.getResult());
        } else {
            //NPE is not possible in this case
            //noinspection ConstantConditions
            showError(result.getThrowable());
        }
    }

    @Override
    public void onLocationClick() {
        invokeView(v -> {
            v.requestLocationWithPermission();
            v.onStateChanged(InputState.DEFAULT);
        });
    }

    private void loadWeatherByPos(LatLon latLon) {
        dispose(loadWeatherDisposable);
        loadWeatherDisposable = weatherRepository.getWeatherByPos(latLon)
                .subscribe(getWeatherSubscriber());
    }

    private void loadWeatherByPlace(String place) {
        dispose(loadWeatherDisposable);
        loadWeatherDisposable = weatherRepository.getWeatherByPlaceName(place)
                .subscribe(getWeatherSubscriber());
    }

    private ObserverSubscriber<Weather> getWeatherSubscriber() {
        return new ObserverSubscriber<Weather>() {
            @Override
            public void onNext(@NonNull Weather next) {
                Log.d(TAG, "onNext() called with: next = [" + next + "]");
                switchProgress(false);
                invokeView(v -> v.onWeatherLoaded(next));
                switchInputState(InputState.DEFAULT);
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                Log.e(TAG, "onError: ", throwable);
                switchProgress(false);
                if (isNotFoundException(throwable)) {
                    invokeView((v) -> v.onShowError(resources.getUnknownPlaceMessage()));
                    switchInputState(InputState.EDITABLE);
                } else {
                    showError(throwable);
                    switchInputState(InputState.DEFAULT);
                }
            }
        };
    }

    @VisibleForTesting
    void dispose(@Nullable Disposable disposable) {
        if (disposable != null) {
            disposable.dispose();
        }
    }

    private void switchProgress(boolean isLoading) {
        invokeView((v) -> v.onShowProgress(isLoading));
    }

    private void showError(@NonNull Throwable throwable) {
        if (view == null) {
            return;
        }
        String message;
        if (throwable instanceof UnknownHostException) {
            message = resources.getUnknownHostException();
        } else {
            message = throwable.getMessage();
        }
        view.onShowError(message == null ? resources.getUnknownErrorMessage() : message);
    }

    private void switchInputState(InputState editable) {
        if (view != null) {
            view.onStateChanged(editable);
        }
    }

    private boolean isNotFoundException(Throwable throwable) {
        return throwable instanceof HttpResponseException &&
                HttpCodes.HTTP_NOT_FOUND.equal(((HttpResponseException) throwable).getCode());
    }

    private void invokeView(Consumer<WeatherContract.View> viewConsumer) {
        if (view != null) {
            viewConsumer.accept(view);
        }
    }

}
