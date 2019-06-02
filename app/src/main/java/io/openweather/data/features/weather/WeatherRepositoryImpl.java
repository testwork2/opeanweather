package io.openweather.data.features.weather;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import io.openweather.data.mappers.WeatherFromResponseMapper;
import io.openweather.data.network.response.WeatherResponse;
import io.openweather.domain.entities.LatLon;
import io.openweather.domain.entities.Optional;
import io.openweather.domain.entities.Weather;
import io.openweather.domain.features.weather.WeatherDao;
import io.openweather.domain.features.weather.WeatherRepository;
import io.openweather.domain.misc.async.TaskExecutor;
import io.openweather.domain.misc.observer.ObserverConsumer;
import io.openweather.domain.misc.observer.ObserverSubscriber;
import io.openweather.domain.misc.observer.dispatchers.ObserverDispatcherAdapter;
import io.openweather.domain.misc.observer.dispatchers.RestObserverDispatcher;
import io.openweather.domain.network.Converter;
import io.openweather.domain.network.RestClient;
import io.openweather.domain.network.ServerConfig;

public class WeatherRepositoryImpl implements WeatherRepository {

    private static final String PATH_WEATHER = "weather";
    private static final String PARAM_QUERY = "q";
    private static final String PARAM_UNITS = "units";
    private static final String PARAM_APP_ID = "APPID";
    private static final String PARAM_LAT = "lat";
    private static final String PARAM_LON = "lon";
    private static final String METRIC = "metric";

    private final TaskExecutor taskExecutor;
    private final ServerConfig serverConfig;
    private final RestClient restClient;
    private final WeatherDao weatherDao;
    private final WeatherFromResponseMapper weatherMapper;
    private final Converter converter;

    public WeatherRepositoryImpl(TaskExecutor taskExecutor, ServerConfig serverConfig,
                                 RestClient restClient, WeatherDao weatherDao,
                                 WeatherFromResponseMapper weatherMapper, Converter converter) {
        this.taskExecutor = taskExecutor;
        this.serverConfig = serverConfig;
        this.restClient = restClient;
        this.weatherDao = weatherDao;
        this.weatherMapper = weatherMapper;
        this.converter = converter;
    }

    @Override
    public ObserverConsumer<Optional<Weather>> getLastWeatherData() {
        return new ObserverDispatcherAdapter<Optional<Weather>>() {
            @Override
            protected void subscribeActual(@NonNull ObserverSubscriber<Optional<Weather>> subscriber) {
                taskExecutor.ioExecutor().execute(() -> {
                    Optional<Weather> weatherOptional = weatherDao.get();
                    taskExecutor.mainExecutor().execute(() -> onNext(weatherOptional));
                });
            }
        };
    }

    @Override
    public ObserverConsumer<Weather> getWeatherByPlaceName(String place) {
        Uri uri = getWeatherUriBuilder()
                .appendQueryParameter(PARAM_QUERY, place)
                .build();
        return getWeatherByUri(uri);
    }


    @Override
    public ObserverConsumer<Weather> getWeatherByPos(LatLon latLon) {
        Uri uri = getWeatherUriBuilder()
                .appendQueryParameter(PARAM_LAT, String.valueOf(latLon.getLat()))
                .appendQueryParameter(PARAM_LON, String.valueOf(latLon.getLon()))
                .build();
        return getWeatherByUri(uri);
    }

    private Uri.Builder getWeatherUriBuilder() {
        return serverConfig.getApiUri()
                .buildUpon()
                .appendPath(PATH_WEATHER)
                .appendQueryParameter(PARAM_UNITS, METRIC)
                .appendQueryParameter(PARAM_APP_ID, serverConfig.getApiKey());
    }

    private ObserverConsumer<Weather> getWeatherByUri(Uri uri) {
        return new RestObserverDispatcher<Weather>(
                restClient.call(uri.toString()),
                taskExecutor.ioExecutor(),
                taskExecutor.mainExecutor(),
                this::convertJson
        ) {
            @Override
            protected void handleResult(Weather result) {
                weatherDao.insert(result);
                super.handleResult(result);
            }
        };
    }

    @WorkerThread
    private Weather convertJson(String json) {
        WeatherResponse response = converter.deserialize(json, WeatherResponse.class);
        return weatherMapper.map(response);
    }

}
