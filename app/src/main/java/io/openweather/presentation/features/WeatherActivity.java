package io.openweather.presentation.features;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.transition.Fade;
import androidx.transition.TransitionManager;

import com.google.android.gms.common.api.ResolvableApiException;

import io.openweather.R;
import io.openweather.ServiceLocator;
import io.openweather.domain.entities.Weather;
import io.openweather.presentation.misc.PermissionHelper;

public class WeatherActivity extends AppCompatActivity implements WeatherContract.View {

    public static final String PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;

    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final String TAG = "WeatherActivity";

    private final PermissionHelper permissionHelper = new PermissionHelper();
    private final WeatherContract.Presenter presenter = ServiceLocator.providePresenter();

    private View progressBar;
    private EditText cityInput;
    private ConstraintLayout rootLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_weather);
        progressBar = findViewById(R.id.progress_bar);
        presenter.attachView(this);
        presenter.loadWeather();

        rootLayout = findViewById(R.id.root_layout);
        rootLayout.loadLayoutDescription(R.xml.weather_states);
        cityInput = findViewById(R.id.city_input);

        cityInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                presenter.onEditCityClick();
                return true;
            }
            return false;
        });
        findViewById(R.id.location_button).setOnClickListener((v) -> presenter.onLocationClick());
        findViewById(R.id.edit_city_button).setOnClickListener((v) -> presenter.onEditCityClick());
        findViewById(R.id.save_city_button).setOnClickListener((v) ->
                presenter.onSaveCityClick(cityInput.getText()));
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionHelper.verifyPermissions(requestCode, grantResults)) {
            presenter.requestLocationSettings();
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
        Toast.makeText(
                this,
                message == null ? getString(R.string.unknown_error) : message,
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    public void onShowProgress(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onShowLocationSettings(@NonNull ResolvableApiException e) {
        try {
            e.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
        } catch (IntentSender.SendIntentException ex) {
            onShowError(ex.getMessage());
        }
    }

    @Override
    public void onWeatherLoaded(@NonNull Weather weather) {
        TextView cityTextView = findViewById(R.id.city_input);
        TextView descriptionTextView = findViewById(R.id.description_text_view);
        ImageView iconImageView = findViewById(R.id.icon_image_view);
        TextView tempTextView = findViewById(R.id.temp_text_view);
        TextView pressureTextView = findViewById(R.id.pressure_text_view);
        TextView humidityTextView = findViewById(R.id.humidity_text_view);
        TextView dateTextView = findViewById(R.id.date_text_view);

        cityTextView.setText(weather.getCity());
        descriptionTextView.setText(weather.getDescription());
        iconImageView.setImageResource(new WeatherIconParser().parseGroupId(weather.getGroupId()));
        tempTextView.setText(getString(R.string.temperature, weather.getTempWithSign()));
        pressureTextView.setText(getString(R.string.pressure, weather.getPressure()));
        humidityTextView.setText(getString(R.string.humidity, weather.getHumidity()));
        dateTextView.setText(getString(R.string.date_updated, weather.getUpdatedDate()));
    }

    @Override
    public void onStateChanged(InputState state) {
        switch (state) {
            case DISABLE:
                cityInput.setEnabled(false);
                break;
            case EDITABLE:
                cityInput.setEnabled(true);
                cityInput.requestFocus();
                cityInput.setSelection(Math.max(0, cityInput.getText().length()));
                showKeyboard(cityInput);
                break;
        }
        TransitionManager.beginDelayedTransition(rootLayout, new Fade());
        rootLayout.setState(state.getStateId(), 0, 0);
    }

    @Override
    public void requestLocationWithPermission() {
        if (!permissionHelper.hasSelfPermissions(this, PERMISSION)) {
            permissionHelper.requestPermissions(this, PERMISSION);
        } else {
            presenter.requestLocationSettings();
        }
    }

    private void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInputFromWindow(view.getWindowToken(), 0, 0);
        }
    }
}
