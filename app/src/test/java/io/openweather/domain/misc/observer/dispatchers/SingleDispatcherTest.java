package io.openweather.domain.misc.observer.dispatchers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import io.openweather.domain.misc.observer.ObserverSubscriber;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SingleDispatcherTest {

    @Mock
    private ObserverSubscriber<Object> subscriber = mock(ObserverSubscriber.class);
    @Mock
    private SingleDispatcher<Object> source;
    private SingleDispatcher<Object> target;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        target = SingleDispatcher.create();
        target.subscribe(subscriber);
        target.source(source);

        assertThat(target.subscriber, is(subscriber));
    }

    @Test
    public void testOnNext() {
        Object test = new Object();

        target.onNext(test);

        verify(source).dispose();
        assertThat(target.subscriber, nullValue());
    }

    @Test
    public void testOnError() {
        Throwable throwable = new Throwable();

        target.onError(throwable);

        verify(source).dispose();
        assertThat(target.subscriber, nullValue());
    }
}