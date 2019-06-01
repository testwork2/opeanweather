package io.openweather.presentation.features;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.ResolvableApiException;

import io.openweather.ServiceLocator;
import io.openweather.domain.entities.Result;
import io.openweather.domain.entities.Weather;
import io.openweather.domain.misc.observer.ObserverSubscriber;
import io.openweather.presentation.misc.PermissionHelper;

public class WeatherActivity extends AppCompatActivity implements WeatherContract.View {

    public static final String PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;

    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final String TAG = "WeatherActivity";

    private final PermissionHelper permissionHelper = new PermissionHelper();
    private final WeatherContract.Presenter presenter = ServiceLocator.providePresenter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ServiceLocator.provideLocationRepository()
                .getWeatherByCityName("Kiev")
                .subscribe(new ObserverSubscriber<Result<Weather>>() {
                    @Override
                    public void onNext(@NonNull Result<Weather> next) {
                        Log.d(TAG, "onNext() called with: next = [" + next + "]");
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Log.e(TAG, "onError() called with: throwable = [" + throwable + "]");
                    }
                });

    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.attachView(this);
        if (!permissionHelper.hasSelfPermissions(this, PERMISSION)) {
            permissionHelper.requestPermissions(this, PERMISSION);
        } else {
            presenter.requestLocation();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.detachView();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionHelper.verifyPermissions(requestCode, grantResults)) {
            presenter.requestLocation();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check for the integer request code originally supplied to startResolutionForResult().
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.i(TAG, "User agreed to make required location settings changes.");
                    presenter.observeLocationUpdates();
                    break;
                case Activity.RESULT_CANCELED:
                    Log.i(TAG, "User chose not to make required location settings changes.");
                    break;
            }
        }
    }


    @Override
    public void onShowError(String message) {

    }

    @Override
    public void onShowProgress(boolean isLoading) {

    }

    @Override
    public void onShowLocationSettings(ResolvableApiException e) {

    }
}
