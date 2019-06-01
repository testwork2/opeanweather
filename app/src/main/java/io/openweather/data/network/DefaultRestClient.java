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

import io.openweather.domain.network.RestCall;
import io.openweather.domain.network.RestClient;

public class DefaultRestClient implements RestClient {

    @Override
    @Nullable
    public RestCall<String> call(String url) {
        return new RestCall<String>() {

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
                                return null;
                            }
                            result.append(line).append('\n');
                        }
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
