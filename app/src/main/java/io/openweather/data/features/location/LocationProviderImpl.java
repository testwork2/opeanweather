package io.openweather.data.features.location;

import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;

import java.util.concurrent.TimeUnit;

import io.openweather.domain.entities.LatLon;
import io.openweather.domain.features.location.LocationException;
import io.openweather.domain.features.location.LocationProvider;
import io.openweather.domain.features.location.LocationResources;
import io.openweather.domain.misc.observer.ObserverDispatcher;
import io.openweather.domain.misc.observer.ObserverSubscriber;
import io.openweather.domain.misc.observer.dispatchers.ObserverDispatcherAdapter;

public class LocationProviderImpl implements LocationProvider {

    private static final String TAG = "LocationProviderImpl";
    private static final int UPDATE_INTERVAL_IN_MINUTES = 1;
    private static final int FASTEST_UPDATE_INTERVAL_IN_MINUTES = UPDATE_INTERVAL_IN_MINUTES / 2;

    @NonNull
    private final Context context;
    @NonNull
    private final LocationResources resources;

    public LocationProviderImpl(@NonNull Context context, @NonNull LocationResources resources) {
        this.context = context.getApplicationContext();
        this.resources = resources;
    }

    @Override
    public ObserverDispatcher<Object> checkSettings() {
        return new LocationSettingsDispatcher();
    }

    public ObserverDispatcher<LatLon> observeLocation() {
        return new LocationDispatcher();
    }

    @Override
    public float distanceBetween(@NonNull LatLon l1, @NonNull LatLon l2) {
        float[] dist = new float[1];
        Location.distanceBetween(l1.getLat(), l1.getLon(), l2.getLat(), l2.getLon(), dist);
        return dist[0];
    }

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(TimeUnit.MINUTES.toMillis(UPDATE_INTERVAL_IN_MINUTES));
        locationRequest.setFastestInterval(TimeUnit.MINUTES.toMillis(FASTEST_UPDATE_INTERVAL_IN_MINUTES));
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    private class LocationDispatcher extends ObserverDispatcherAdapter<LatLon> {
        private final FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(context);
        private final LocationCallback locationCallback = createLocationCallback();

        @Override
        protected void subscribeActual(@NonNull ObserverSubscriber<LatLon> subscriber) {
            LocationRequest locationRequest = createLocationRequest();
            client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
            .addOnFailureListener(this::onError);
        }

        @Override
        public void dispose() {
            client.removeLocationUpdates(locationCallback);
        }

        private LocationCallback createLocationCallback() {
            return new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Location lastLocation = locationResult.getLastLocation();
                    onNext(new LatLon(lastLocation.getLatitude(), lastLocation.getLongitude()));
                }
            };
        }

    }

    private class LocationSettingsDispatcher extends ObserverDispatcherAdapter<Object> {
        private final SettingsClient settingsClient = LocationServices.getSettingsClient(context);

        @Override
        protected void subscribeActual(@NonNull ObserverSubscriber<Object> subscriber) {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(createLocationRequest());
            LocationSettingsRequest settingsRequest = builder.build();

            settingsClient
                    .checkLocationSettings(settingsRequest)
                    .addOnSuccessListener(locationSettingsResponse -> {
                        Log.d(TAG, "All location settings are satisfied");
                        onNext(true);
                    })
                    .addOnFailureListener(this::handleError);
        }

        private void handleError(Exception e) {
            int statusCode = ((ApiException) e).getStatusCode();
            switch (statusCode) {
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                            "location settings ");
                    ResolvableApiException rae = (ResolvableApiException) e;
                    onError(rae);
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    onError(new LocationException(resources.getSettingsChangeUnavailableMessage()));
                    break;
                default:
                    onError(e);
            }
        }
    }

}
