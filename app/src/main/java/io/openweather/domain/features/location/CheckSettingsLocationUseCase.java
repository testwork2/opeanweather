package io.openweather.domain.features.location;

import io.openweather.domain.misc.UseCase;
import io.openweather.domain.misc.observer.ObserverEmitter;
import io.openweather.domain.misc.observer.dispatchers.SingleDispatcher;

public class CheckSettingsLocationUseCase implements UseCase<ObserverEmitter<Object>> {

    private final LocationProvider locationProvider;

    public CheckSettingsLocationUseCase(LocationProvider locationProvider) {
        this.locationProvider = locationProvider;
    }

    @Override
    public ObserverEmitter<Object> execute() {
        return locationProvider.checkSettings().connect(SingleDispatcher.create());
    }


}
