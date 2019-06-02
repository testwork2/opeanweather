package io.openweather;

import android.app.Application;

import com.google.gson.Gson;

import io.openweather.data.features.ResourcesImpl;
import io.openweather.data.features.location.LocationProviderImpl;
import io.openweather.data.features.weather.WeatherDaoImpl;
import io.openweather.data.features.weather.WeatherRepositoryImpl;
import io.openweather.data.mappers.WeatherFromResponseMapper;
import io.openweather.data.network.DefaultRestClient;
import io.openweather.data.network.GsonConverterImpl;
import io.openweather.data.network.ServerConfigImpl;
import io.openweather.domain.features.Resources;
import io.openweather.domain.features.location.LocationProvider;
import io.openweather.domain.features.weather.CheckSettingsLocationUseCase;
import io.openweather.domain.features.weather.SubscribeChangingLocationUseCase;
import io.openweather.domain.features.weather.ValidatePlaceNameUseCase;
import io.openweather.domain.features.weather.WeatherDao;
import io.openweather.domain.features.weather.WeatherRepository;
import io.openweather.domain.misc.async.TaskExecutor;
import io.openweather.domain.misc.async.TaskExecutorImpl;
import io.openweather.domain.network.Converter;
import io.openweather.domain.network.RestClient;
import io.openweather.domain.network.ServerConfig;
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

    //region Data

    public static TaskExecutor provideTaskExecutor() {
        return TaskExecutorImpl.getInstance();
    }

    public static Converter provideConverter() {
        return new GsonConverterImpl(new Gson());
    }

    public static ServerConfig provideServerConfig() {
        return new ServerConfigImpl();
    }

    public static RestClient provideRestClient() {
        return new DefaultRestClient();
    }


    public static Resources provideResources() {
        return new ResourcesImpl(INSTANCE.application);
    }

    public static LocationProvider provideLocationProvider() {
        return new LocationProviderImpl(INSTANCE.application, provideResources());
    }

    public static WeatherFromResponseMapper provideWeatherMapper() {
        return new WeatherFromResponseMapper();
    }

    public static WeatherDao provideWeatherDao() {
        return new WeatherDaoImpl(INSTANCE.application);
    }

    public static WeatherRepository provideLocationRepository() {
        return new WeatherRepositoryImpl(
                provideTaskExecutor(),
                provideServerConfig(),
                provideRestClient(),
                provideWeatherDao(),
                provideWeatherMapper(),
                provideConverter()
        );
    }

    //endregion


    //region Domain

    public static CheckSettingsLocationUseCase provideCheckSettingsLocationUseCase() {
        return new CheckSettingsLocationUseCase(provideLocationProvider());
    }

    public static SubscribeChangingLocationUseCase provideSubscribeLocationUseCase() {
        return new SubscribeChangingLocationUseCase(provideLocationProvider());
    }

    public static ValidatePlaceNameUseCase provideValidatePlaceNameUseCase() {
        return new ValidatePlaceNameUseCase(provideResources());
    }

    //endregion


    //region Presentation

    public static WeatherContract.Presenter providePresenter() {
        return new WeatherPresenter(
                provideCheckSettingsLocationUseCase(),
                provideSubscribeLocationUseCase(),
                provideLocationRepository(),
                provideValidatePlaceNameUseCase(),
                provideResources());
    }

    //endregion

}
