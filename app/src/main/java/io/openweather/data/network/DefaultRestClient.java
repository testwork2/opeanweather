package io.openweather.data.network;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import io.openweather.domain.network.HttpResponseException;
import io.openweather.domain.network.RequestCancelledException;
import io.openweather.domain.network.RestCall;
import io.openweather.domain.network.RestClient;

public class DefaultRestClient implements RestClient {

    @Override
    @Nullable
    public RestCall call(String url) {
        return new RestCall() {

            private volatile boolean isCancelled;

            @Override
            @WorkerThread
            public String execute() throws IOException {
                URL obj = new URL(url);
                HttpURLConnection urlConnection = (HttpURLConnection) obj.openConnection();
                StringBuilder result = new StringBuilder();
                try {
                    try (InputStream in = new BufferedInputStream(urlConnection.getInputStream())) {
                        BufferedReader r = new BufferedReader(new InputStreamReader(in));
                        for (String line; (line = r.readLine()) != null; ) {
                            if (isCancelled) {
                                throw new RequestCancelledException();
                            }
                            result.append(line).append('\n');
                        }
                    }
                } catch (IOException exception) {
                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode > 0) {
                        throw new HttpResponseException(responseCode, exception);
                    } else {
                        throw exception;
                    }
                } finally {
                    urlConnection.disconnect();
                }
                return result.toString();
            }

            @Override
            public void cancel() {
                isCancelled = true;
            }
        };
    }

}
