package io.openweather.domain.features.weather;

import io.openweather.domain.features.location.LocationProvider;
import io.openweather.domain.misc.UseCase;
import io.openweather.domain.misc.observer.ObserverConsumer;
import io.openweather.domain.misc.observer.dispatchers.SingleDispatcher;

public class CheckSettingsLocationUseCase implements UseCase<ObserverConsumer<Object>> {

    private final LocationProvider locationProvider;

    public CheckSettingsLocationUseCase(LocationProvider locationProvider) {
        this.locationProvider = locationProvider;
    }

    @Override
    public ObserverConsumer<Object> execute() {
        return locationProvider.checkSettings().connect(SingleDispatcher.create());
    }


}
