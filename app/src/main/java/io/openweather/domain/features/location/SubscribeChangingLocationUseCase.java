package io.openweather.domain.features.location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.openweather.domain.entities.LatLon;
import io.openweather.domain.misc.UseCase;
import io.openweather.domain.misc.observer.ObserverConsumer;
import io.openweather.domain.misc.observer.dispatchers.ObserverDispatcherAdapter;

public class SubscribeChangingLocationUseCase implements UseCase<ObserverConsumer<LatLon>> {

    private final LocationProvider locationProvider;

    public SubscribeChangingLocationUseCase(LocationProvider locationProvider) {
        this.locationProvider = locationProvider;
    }

    @Override
    public ObserverConsumer<LatLon> execute() {
        return locationProvider.observeLocation().connect(new FilterLocationDispatcher());
    }


    private class FilterLocationDispatcher extends ObserverDispatcherAdapter<LatLon> {

        private final static int ACCURACY = 100; //meters

        @Nullable
        private LatLon previousPos;

        @Override
        public void onNext(@NonNull LatLon next) {
            if (subscriber == null) {
                return;
            }

            if (previousPos == null || (!next.equals(previousPos) && isOutOfRadius(previousPos, next))) {
                subscriber.onNext(next);
            }
            previousPos = next;
        }

        private boolean isOutOfRadius(@NonNull LatLon l1, @NonNull LatLon l2) {
            return locationProvider.distanceBetween(l1, l2) > ACCURACY;
        }
    }
}
