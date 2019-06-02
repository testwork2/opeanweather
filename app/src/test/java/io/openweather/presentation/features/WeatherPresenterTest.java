package io.openweather.presentation.features;

import com.google.android.gms.common.api.ResolvableApiException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.openweather.ObserverTestConsumer;
import io.openweather.domain.entities.LatLon;
import io.openweather.domain.entities.Optional;
import io.openweather.domain.entities.ValidationResult;
import io.openweather.domain.entities.Weather;
import io.openweather.WeatherBuilder;
import io.openweather.domain.features.Resources;
import io.openweather.domain.features.weather.CheckSettingsLocationUseCase;
import io.openweather.domain.features.weather.SubscribeChangingLocationUseCase;
import io.openweather.domain.features.weather.ValidatePlaceNameUseCase;
import io.openweather.domain.features.weather.WeatherRepository;
import io.openweather.domain.misc.exceptions.IllegalDataException;
import io.openweather.domain.network.HttpCodes;
import io.openweather.domain.network.HttpResponseException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class WeatherPresenterTest {

    @Mock private CheckSettingsLocationUseCase settingsLocationUseCase;
    @Mock private SubscribeChangingLocationUseCase subscribeChangingLocationUseCase;
    @Mock private WeatherRepository weatherRepository;
    @Mock private ValidatePlaceNameUseCase validatePlaceNameUseCase;
    @Mock private Resources resources;
    @Mock private WeatherContract.View view;

    private WeatherPresenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        WeatherPresenter presenter = new WeatherPresenter(settingsLocationUseCase,
                subscribeChangingLocationUseCase, weatherRepository, validatePlaceNameUseCase,
                resources);
        this.presenter = spy(presenter);
    }

    @Test
    public void testViewAttachedAndDetached() {
        ObserverTestConsumer<Optional<Weather>> testConsumer = new ObserverTestConsumer<>(Optional.empty());

        when(weatherRepository.getLastWeatherData()).thenReturn(testConsumer);

        presenter.attachView(view);
        presenter.loadWeather();
        verify(view).requestLocationWithPermission();

        presenter.detachView();
        verify(presenter, times(5)).dispose(any());

        presenter.loadWeather();
        verifyZeroInteractions(view);
    }

    @Test
    public void testGetLastWeatherIfExists() {
        String place = "place";
        Weather weather = new WeatherBuilder().setPlace(place).build();

        ObserverTestConsumer<Optional<Weather>> testConsumer = new ObserverTestConsumer<>(Optional.of(weather));

        when(weatherRepository.getLastWeatherData()).thenReturn(testConsumer);
        when(weatherRepository.getWeatherByPlaceName(anyString())).thenReturn(new ObserverTestConsumer<>());

        presenter.attachView(view);
        presenter.loadWeather();

        verify(presenter, times(2)).dispose(any());
        verify(weatherRepository).getWeatherByPlaceName(place);
        verify(view).onWeatherLoaded(eq(weather));
        verify(view, never()).onShowError(anyString());
        verify(view, never()).requestLocationWithPermission();
    }

    @Test
    public void testGetLastWeatherIfNotExists() {
        ObserverTestConsumer<Optional<Weather>> testConsumer = new ObserverTestConsumer<>(Optional.empty());

        when(weatherRepository.getLastWeatherData()).thenReturn(testConsumer);

        presenter.attachView(view);
        presenter.loadWeather();

        verify(view, never()).onWeatherLoaded(any(Weather.class));
        verify(view, never()).onShowError(anyString());
        verify(view).requestLocationWithPermission();
    }


    @Test
    public void testGetLastWeatherFailed() {
        Throwable throwable = new Throwable();

        ObserverTestConsumer<Optional<Weather>> testConsumer = new ObserverTestConsumer<>(throwable);

        when(weatherRepository.getLastWeatherData()).thenReturn(testConsumer);

        presenter.attachView(view);
        presenter.loadWeather();

        verify(view, never()).onWeatherLoaded(any(Weather.class));
        verify(view, never()).onShowError(anyString());
        verify(view).requestLocationWithPermission();
    }

    @Test
    public void testRequestLocationSettingsSuccess() {
        ObserverTestConsumer<Object> testConsumer = new ObserverTestConsumer<>(true);

        when(settingsLocationUseCase.execute()).thenReturn(testConsumer);
        doNothing().when(presenter).observeLocationUpdates();

        presenter.attachView(view);
        presenter.requestLocationSettings();

        verify(presenter).dispose(any());
        verify(presenter).observeLocationUpdates();
        verify(view, never()).onShowError(anyString());
    }

    @Test
    public void testRequestLocationSettingsFailed() {
        String errorMessage = "errorMessage";
        Exception exception = new Exception(errorMessage);
        ObserverTestConsumer<Object> testConsumer = new ObserverTestConsumer<>(exception);

        when(settingsLocationUseCase.execute()).thenReturn(testConsumer);

        presenter.attachView(view);
        presenter.requestLocationSettings();

        verify(presenter, never()).observeLocationUpdates();
        verify(view).onShowError(errorMessage);
        verify(view, never()).onShowLocationSettings(any());
    }

    @Test
    public void testRequestLocationSettingsFailedWithResolvableApiException() {
        ResolvableApiException exception = mock(ResolvableApiException.class);
        ObserverTestConsumer<Object> testConsumer = new ObserverTestConsumer<>(exception);

        when(settingsLocationUseCase.execute()).thenReturn(testConsumer);

        presenter.attachView(view);
        presenter.requestLocationSettings();

        verify(presenter, never()).observeLocationUpdates();
        verify(view, never()).onShowError(anyString());
        verify(view).onShowLocationSettings(eq(exception));
    }

    @Test
    public void testObserveLocationUpdatesSuccess() {
        LatLon latLon = new LatLon(1, 3);
        Weather weather = new WeatherBuilder().build();

        ObserverTestConsumer<LatLon> latLonConsumer = new ObserverTestConsumer<>(latLon);
        ObserverTestConsumer<Weather> weatherConsumer = new ObserverTestConsumer<>(weather);

        when(subscribeChangingLocationUseCase.execute()).thenReturn(latLonConsumer);
        when(weatherRepository.getWeatherByPos(any())).thenReturn(weatherConsumer);

        presenter.attachView(view);
        presenter.observeLocationUpdates();

        InOrder inOrder = Mockito.inOrder(view, weatherRepository, subscribeChangingLocationUseCase);
        inOrder.verify(view).onShowProgress(eq(true));
        inOrder.verify(view).onStateChanged(eq(InputState.DISABLED));

        inOrder.verify(subscribeChangingLocationUseCase).execute();
        inOrder.verify(weatherRepository).getWeatherByPos(latLon);

        inOrder.verify(view).onShowProgress(false);
        inOrder.verify(view).onWeatherLoaded(weather);
        inOrder.verify(view).onStateChanged(InputState.DEFAULT);

        verify(weatherRepository, never()).getWeatherByPlaceName(anyString());

    }

    @Test
    public void testObserveLocationUpdatesFailed() {
        String error = "error";
        Throwable throwable = new Exception(error);
        ObserverTestConsumer<LatLon> latLonConsumer = new ObserverTestConsumer<>(throwable);

        when(subscribeChangingLocationUseCase.execute()).thenReturn(latLonConsumer);

        presenter.attachView(view);
        presenter.observeLocationUpdates();

        InOrder inOrder = Mockito.inOrder(view, weatherRepository, subscribeChangingLocationUseCase);
        inOrder.verify(view).onShowProgress(eq(true));
        inOrder.verify(view).onStateChanged(eq(InputState.DISABLED));

        inOrder.verify(subscribeChangingLocationUseCase).execute();

        inOrder.verify(view).onShowProgress(false);
        inOrder.verify(view).onShowError(eq(error));
        inOrder.verify(view).onStateChanged(InputState.DEFAULT);

        verify(weatherRepository, never()).getWeatherByPlaceName(anyString());
        verify(weatherRepository, never()).getWeatherByPos(any(LatLon.class));
    }

    @Test
    public void testOnEditPlaceClick() {
        presenter.attachView(view);
        presenter.onEditPlaceClick();
        verify(view).onStateChanged(eq(InputState.EDITABLE));
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testSavePlaceWithFailValidation() {
        String place = "      ";
        String errorMessage = "errorMessage";
        IllegalDataException validationError = new IllegalDataException(errorMessage);

        when(validatePlaceNameUseCase.execute(place))
                .thenReturn(new ValidationResult<>(validationError));

        presenter.attachView(view);
        presenter.onSavePlaceClick(place);

        verify(view).onShowError(errorMessage);
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testSavePlaceWithSuccessLoad() {
        String place = "place";
        Weather weather = new WeatherBuilder().build();
        ObserverTestConsumer<Weather> weatherConsumer = new ObserverTestConsumer<>(weather);

        when(validatePlaceNameUseCase.execute(place)).thenReturn(new ValidationResult<>(place));
        when(weatherRepository.getWeatherByPlaceName(place)).thenReturn(weatherConsumer);

        presenter.attachView(view);
        presenter.onSavePlaceClick(place);

        InOrder inOrder = Mockito.inOrder(view, validatePlaceNameUseCase, weatherRepository, presenter);
        inOrder.verify(validatePlaceNameUseCase).execute(eq(place));
        inOrder.verify(view).onShowProgress(true);
        inOrder.verify(view).onStateChanged(eq(InputState.DISABLED));
        inOrder.verify(presenter).dispose(any());
        inOrder.verify(view).onShowProgress(eq(false));
        inOrder.verify(view).onWeatherLoaded(eq(weather));
        inOrder.verify(view).onStateChanged(eq(InputState.DEFAULT));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testSavePlaceWithNotFoundPlace() {
        String errorMessage = "errorMessage";
        HttpResponseException exception = new HttpResponseException(HttpCodes.HTTP_NOT_FOUND.getCode(), new Throwable());

        when(resources.getUnknownPlaceMessage()).thenReturn(errorMessage);

        testSavePlaceFail(exception, InputState.EDITABLE);

        verify(resources).getUnknownPlaceMessage();
        verify(view).onShowError(errorMessage);
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testSavePlaceWithFailureLoad() {
        String errorMessage = "exceptionMessage";
        Throwable throwable = new Exception(errorMessage);

        testSavePlaceFail(throwable, InputState.DEFAULT);

        verify(view).onShowError(errorMessage);
        verifyNoMoreInteractions(view);
    }


    private void testSavePlaceFail(Throwable throwable, InputState stateAfterError) {
        String place = "place";

        ObserverTestConsumer<Weather> weatherConsumer = new ObserverTestConsumer<>(throwable);

        when(validatePlaceNameUseCase.execute(place)).thenReturn(new ValidationResult<>(place));
        when(weatherRepository.getWeatherByPlaceName(place)).thenReturn(weatherConsumer);

        presenter.attachView(view);
        presenter.onSavePlaceClick(place);

        verify(view).onShowProgress(true);
        verify(view).onStateChanged(eq(InputState.DISABLED));

        verify(view).onShowProgress(false);
        verify(view).onStateChanged(eq(stateAfterError));
    }

}