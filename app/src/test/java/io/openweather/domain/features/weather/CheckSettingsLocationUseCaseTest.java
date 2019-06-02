package io.openweather.domain.features.weather;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.openweather.domain.features.location.LocationProvider;
import io.openweather.domain.misc.observer.ObserverDispatcher;
import io.openweather.domain.misc.observer.dispatchers.SingleDispatcher;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CheckSettingsLocationUseCaseTest {

    @Mock
    private LocationProvider locationProvider;
    private CheckSettingsLocationUseCase useCase;


    @Test
    public void testGettingSettings() {
        MockitoAnnotations.initMocks(this);
        CheckSettingsLocationUseCase useCase = new CheckSettingsLocationUseCase(locationProvider);

        ObserverDispatcher<Object> dispatcher = mock(ObserverDispatcher.class);
        when(locationProvider.checkSettings()).thenReturn(dispatcher);
        useCase.execute();
        verify(dispatcher).connect(any(SingleDispatcher.class));
    }

}