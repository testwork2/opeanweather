package io.openweather;

import android.app.Application;

import io.openweather.data.features.location.LocationProviderImpl;
import io.openweather.data.features.location.LocationResourcesImpl;
import io.openweather.domain.features.location.CheckSettingsLocationUseCase;
import io.openweather.domain.features.location.LocationProvider;
import io.openweather.domain.features.location.LocationResources;
import io.openweather.domain.features.location.SubscribeChangingLocationUseCase;
import io.openweather.presentation.features.WeatherContract;
import io.openweather.presentation.features.WeatherPresenter;

@SuppressWarnings("WeakerAccess")
public class ServiceLocator {

    private static ServiceLocator INSTANCE;
    private Application application;

    private ServiceLocator(Application application) {
        this.application = application;
    }

    static void init(Application application) {
        INSTANCE = new ServiceLocator(application);
        INSTANCE.application = application;
    }

    public static LocationResources resolveLocationResources() {
        return new LocationResourcesImpl(INSTANCE.application);
    }

    public static LocationProvider resolveLocationProvider() {
        return new LocationProviderImpl(INSTANCE.application, resolveLocationResources());
    }

    public static CheckSettingsLocationUseCase resolveCheckSettingsLocationUseCase() {
        return new CheckSettingsLocationUseCase(resolveLocationProvider());
    }

    public static SubscribeChangingLocationUseCase resolveSubscribeLocationUseCase() {
        return new SubscribeChangingLocationUseCase(resolveLocationProvider());
    }

    public static WeatherContract.Presenter providePresenter() {
        return new WeatherPresenter(
                resolveCheckSettingsLocationUseCase(),
                resolveSubscribeLocationUseCase()
        );
    }

}
