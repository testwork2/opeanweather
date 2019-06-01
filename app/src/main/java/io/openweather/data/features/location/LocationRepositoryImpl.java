package io.openweather.data.features.location;

import android.net.Uri;

import androidx.annotation.WorkerThread;

import io.openweather.data.mappers.WeatherFromResponseMapper;
import io.openweather.data.network.response.WeatherResponse;
import io.openweather.domain.entities.LatLon;
import io.openweather.domain.entities.Result;
import io.openweather.domain.entities.Weather;
import io.openweather.domain.features.location.LocationRepository;
import io.openweather.domain.misc.async.TaskExecutor;
import io.openweather.domain.misc.observer.ObserverConsumer;
import io.openweather.domain.misc.observer.dispatchers.RestObserverDispatcher;
import io.openweather.domain.network.Converter;
import io.openweather.domain.network.RestClient;
import io.openweather.domain.network.ServerConfig;

public class LocationRepositoryImpl implements LocationRepository {

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
    private final WeatherFromResponseMapper weatherMapper;
    private final Converter converter;

    public LocationRepositoryImpl(TaskExecutor taskExecutor, ServerConfig serverConfig, RestClient restClient, WeatherFromResponseMapper weatherMapper, Converter converter) {
        this.taskExecutor = taskExecutor;
        this.serverConfig = serverConfig;
        this.restClient = restClient;
        this.weatherMapper = weatherMapper;
        this.converter = converter;
    }

    @Override
    public ObserverConsumer<Result<Weather>> getWeatherByCityName(String city) {
        Uri uri = getWeatherUriBuilder()
                .appendQueryParameter(PARAM_QUERY, city)
                .build();

        return getWeatherByUri(uri);
    }


    @Override
    public ObserverConsumer<Result<Weather>> getWeatherByPos(LatLon latLon) {
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

    private ObserverConsumer<Result<Weather>> getWeatherByUri(Uri uri) {
        return new RestObserverDispatcher<>(
                restClient.call(uri.toString()),
                taskExecutor.ioExecutor(),
                taskExecutor.mainExecutor(),
                this::convertJson
        );
    }

    @WorkerThread
    private Result<Weather> convertJson(String json) {
        WeatherResponse response = converter.deserialize(json, WeatherResponse.class);
        Weather weather = weatherMapper.map(response);
        return new Result.Builder<Weather>()
                .setRemote(weather)
                .build();
    }

}
