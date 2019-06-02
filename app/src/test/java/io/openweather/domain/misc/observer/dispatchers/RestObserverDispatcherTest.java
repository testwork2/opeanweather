package io.openweather.domain.misc.observer.dispatchers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.concurrent.Executor;

import io.openweather.domain.misc.observer.Function;
import io.openweather.domain.misc.observer.ObserverSubscriber;
import io.openweather.domain.network.RequestCancelledException;
import io.openweather.domain.network.RestCall;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RestObserverDispatcherTest {

    @Mock private ObserverSubscriber<String> subscriber;
    @Mock private RestCall restCall;
    @Mock private Executor fetchExecutor;
    @Mock private Executor notifyExecutor;
    @Mock private Function<String, String> mapperFunction;

    private RestObserverDispatcher<String> dispatcher;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        dispatcher = spy(new RestObserverDispatcher<>(restCall, fetchExecutor, notifyExecutor, mapperFunction));

        mockExecutor(fetchExecutor);
        mockExecutor(notifyExecutor);
    }

    @Test
    public void testDispose() {
        dispatcher.dispose();
        verify(restCall).cancel();
    }

    @Test
    public void testSuccess() throws IOException {
        String mapFrom = "from";
        String mapTo = "to";

        when(mapperFunction.apply(anyString())).thenReturn(mapTo);
        when(restCall.execute()).thenReturn(mapFrom);

        dispatcher.subscribe(subscriber);

        verify(mapperFunction).apply(mapFrom);
        verify(notifyExecutor).execute(any());
        verify(dispatcher).onNext(mapTo);
    }

    @Test
    public void testRequestCancelled() throws IOException {

        when(restCall.execute()).thenThrow(RequestCancelledException.class);

        dispatcher.subscribe(subscriber);

        verify(dispatcher, never()).onNext(anyString());
        verify(dispatcher, never()).onError(any());
    }

    @Test
    public void testFail() throws IOException {
        IOException mock = mock(IOException.class);
        when(restCall.execute()).thenThrow(mock);

        dispatcher.subscribe(subscriber);

        verify(dispatcher, never()).onNext(anyString());
        verify(dispatcher).onError(eq(mock));
    }

    private void mockExecutor(Executor executor) {
        doAnswer(invocation -> {
            ((Runnable) invocation.getArguments()[0]).run();
            return invocation;
        }).when(executor).execute(any());
    }
}