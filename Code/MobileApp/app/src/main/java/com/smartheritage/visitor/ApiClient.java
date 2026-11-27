package com.smartheritage.visitor;

import android.os.Handler;
import android.os.Looper;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONException;
import org.json.JSONObject;

public final class ApiClient {

    public interface Callback {
        void onSuccess(JSONObject response);
        void onError(String message);
    }

    private static final ExecutorService IO = Executors.newFixedThreadPool(3);
    private static final Handler MAIN = new Handler(Looper.getMainLooper());
    private final String baseUrl;
    private volatile String accessToken;

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl.replaceAll("/+$", "");
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void get(String path, Callback callback) {
        request("GET", path, null, callback);
    }

    public void post(String path, JSONObject body, Callback callback) {
        request("POST", path, body, callback);
    }

    public void put(String path, JSONObject body, Callback callback) {
        request("PUT", path, body, callback);
    }

    public void delete(String path, Callback callback) {
        request("DELETE", path, null, callback);
    }

    private void request(String method, String path, JSONObject body, Callback callback) {
        String requestToken = accessToken;
        IO.execute(() -> {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) URI.create(baseUrl + path).toURL().openConnection();
                connection.setRequestMethod(method);
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(10000);
                connection.setRequestProperty("Accept", "application/json");
                String token = requestToken;
                if (token != null && !token.trim().isEmpty()) {
                    connection.setRequestProperty("Authorization", "Bearer " + token);
                }
                if (body != null) {
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                    try (OutputStream output = connection.getOutputStream()) {
                        output.write(body.toString().getBytes(StandardCharsets.UTF_8));
                    }
                }

                int status = connection.getResponseCode();
                InputStream stream = status >= 400 ? connection.getErrorStream() : connection.getInputStream();
                String responseText = read(stream);
                if (status < 200 || status >= 300) {
                    String message = "Request failed (" + status + ")";
                    if (!responseText.trim().isEmpty()) {
                        try {
                            message = "HTTP " + status + ": "
                                    + new JSONObject(responseText).optString("message", message);
                        } catch (JSONException ignored) {
                            message = "Request failed (" + status + ")";
                        }
                    }
                    String error = message;
                    MAIN.post(() -> callback.onError(error));
                    return;
                }
                JSONObject result = responseText.trim().isEmpty() ? new JSONObject()
                        : new JSONObject(responseText);
                MAIN.post(() -> callback.onSuccess(result));
            } catch (IOException | JSONException | IllegalArgumentException exception) {
                String message = exception.getMessage() == null
                        ? "Could not reach Smart Heritage server" : exception.getMessage();
                MAIN.post(() -> callback.onError(message));
            } finally {
                if (connection != null) connection.disconnect();
            }
        });
    }

    private String read(InputStream stream) throws IOException {
        if (stream == null) return "";
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) output.append(line);
            return output.toString();
        }
    }
}
