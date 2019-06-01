package io.openweather.data.features.location;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import io.openweather.data.mappers.WeatherMapper;
import io.openweather.data.network.response.WeatherResponse;
import io.openweather.domain.entities.LatLon;
import io.openweather.domain.entities.Result;
import io.openweather.domain.entities.Weather;
import io.openweather.domain.features.location.LocationRepository;
import io.openweather.domain.misc.async.TaskExecutor;
import io.openweather.domain.misc.observer.ObserverConsumer;
import io.openweather.domain.misc.observer.ObserverSubscriber;
import io.openweather.domain.misc.observer.dispatchers.ObserverDispatcherAdapter;
import io.openweather.domain.network.Converter;
import io.openweather.domain.network.RestCall;
import io.openweather.domain.network.RestClient;
import io.openweather.domain.network.ServerConfig;

public class LocationRepositoryImpl implements LocationRepository {

    private static final String PATH_WEATHER = "weather";
    private static final String PARAM_QUERY = "q";
    private static final String PARAM_UNITS = "units";
    private static final String PARAM_APP_ID = "APPID";
    private static final String METRIC = "metric";

    private final TaskExecutor taskExecutor;
    private final ServerConfig serverConfig;
    private final RestClient restClient;
    private final WeatherMapper weatherMapper;
    private final Converter converter;

    public LocationRepositoryImpl(TaskExecutor taskExecutor, ServerConfig serverConfig, RestClient restClient, WeatherMapper weatherMapper, Converter converter) {
        this.taskExecutor = taskExecutor;
        this.serverConfig = serverConfig;
        this.restClient = restClient;
        this.weatherMapper = weatherMapper;
        this.converter = converter;
    }

    @Override
    public ObserverConsumer<Result<Weather>> getWeatherByCityName(String city) {
        return new ObserverDispatcherAdapter<Result<Weather>>() {

            private RestCall<String> restCall;

            @Override
            protected void subscribeActual(@NonNull ObserverSubscriber<Result<Weather>> subscriber) {
                Uri uri = serverConfig.getApiUri()
                        .buildUpon()
                        .appendPath(PATH_WEATHER)
                        .appendQueryParameter(PARAM_QUERY, city)
                        .appendQueryParameter(PARAM_UNITS, METRIC)
                        .appendQueryParameter(PARAM_APP_ID, serverConfig.getApiKey())
                        .build();

                restCall = restClient.call(uri.toString());

                taskExecutor.ioExecutor().execute(() -> {
                    try {
                        onNext(convertJson(restCall.execute()));
                    } catch (Throwable throwable) {
                        onError(throwable);
                    }
                });
            }

            @Override
            public void dispose() {
                super.dispose();
                if (restCall != null) {
                    restCall.cancel();
                }
            }
        };
    }

    @Override
    public void getWeatherByPos(LatLon latLon) {

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
