package com.smartheritage.visitor;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.app.Notification;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends android.app.Activity implements IBeaconScanner.Listener {

    private static final String PREFERENCES = "smart-heritage";
    private static final String TOKEN_KEY = "access-token";
    private static final String LANGUAGE_KEY = "language";
    private static final String LAST_CONTENT_KEY = "last-content";
    private static final String CHANNEL_ID = "heritage-nearby";
    private static final int BLE_PERMISSION_REQUEST = 41;
    private static final long BEACON_LOST_AFTER_MS = 8000;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final long[] lastBeaconSeen = {0};
    private ApiClient api;
    private SharedPreferences preferences;
    private LinearLayout root;
    private LinearLayout content;
    private ScrollView scroll;
    private Spinner languageSpinner;
    private String language = "vi";
    private String token;
    private String username = "";
    private String currentView = "home";
    private IBeaconScanner beaconScanner;
    private String activeBeaconKey = "";
    private Long activeBeaconId;
    private JSONObject activeArtifact;
    private long activeArtifactStartedAt;
    private String activeEventKey;

    private final Runnable beaconTimeout = new Runnable() {
        @Override
        public void run() {
            if (!activeBeaconKey.isEmpty()
                    && SystemClock.elapsedRealtime() - lastBeaconSeen[0] >= BEACON_LOST_AFTER_MS) {
                finishCurrentInteraction();
                activeBeaconKey = "";
                activeBeaconId = null;
                activeArtifact = null;
            }
            mainHandler.postDelayed(this, 2000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        token = preferences.getString(TOKEN_KEY, null);
        language = preferences.getString(LANGUAGE_KEY, "vi");
        api = new ApiClient(BuildConfig.API_BASE_URL);
        api.setAccessToken(token);
        createNotificationChannel();
        if (token == null) showLogin();
        else loadProfile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainHandler.removeCallbacks(beaconTimeout);
        mainHandler.postDelayed(beaconTimeout, 2000);
    }

    @Override
    protected void onPause() {
        stopBeaconScan();
        finishCurrentInteraction();
        activeBeaconKey = "";
        mainHandler.removeCallbacks(beaconTimeout);
        super.onPause();
    }

    private void showLogin() {
        createRoot();
        addTitle("Smart Heritage");
        addText("Khám phá di sản bằng trải nghiệm đa phương tiện và iBeacon.");
        EditText identifier = input("Tài khoản hoặc email");
        EditText password = input("Mật khẩu");
        password.setInputType(129);
        EditText registerUsername = input("Tên người dùng (khi đăng ký)");
        EditText email = input("Email (khi đăng ký)");
        root.addView(identifier);
        root.addView(password);
        root.addView(registerUsername);
        root.addView(email);
        addButton("Đăng nhập", view -> api.post("/api/auth/login",
                json("identifier", identifier.getText().toString().trim(),
                        "password", password.getText().toString()), new ApiClient.Callback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        acceptLogin(response.optJSONObject("data"));
                    }

                    @Override
                    public void onError(String message) {
                        toast(message);
                    }
                }));
        addButton("Tạo tài khoản du khách", view -> api.post("/api/auth/register",
                json("username", registerUsername.getText().toString().trim(),
                        "email", email.getText().toString().trim(),
                        "password", password.getText().toString(),
                        "languageCode", language), new ApiClient.Callback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        acceptLogin(response.optJSONObject("data"));
                    }

                    @Override
                    public void onError(String message) {
                        toast(message);
                    }
                }));
    }

    private void acceptLogin(JSONObject data) {
        if (data == null) {
            toast("Phản hồi đăng nhập không hợp lệ.");
            return;
        }
        token = data.optString("accessToken", null);
        username = data.optString("username", "");
        if (token == null || token.trim().isEmpty()) {
            toast("Máy chủ chưa cấp access token.");
            return;
        }
        preferences.edit().putString(TOKEN_KEY, token).putString(LANGUAGE_KEY, language).apply();
        api.setAccessToken(token);
        showHome();
    }

    private void loadProfile() {
        api.get("/api/users/me", new ApiClient.Callback() {
            @Override
            public void onSuccess(JSONObject response) {
                JSONObject user = response.optJSONObject("data");
                if (user == null || !"VISITOR".equals(user.optString("role"))) {
                    logout(false);
                    toast("Tài khoản này không phải tài khoản du khách.");
                    return;
                }
                username = user.optString("username", "");
                language = user.optString("preferredLanguage", language);
                preferences.edit().putString(LANGUAGE_KEY, language).apply();
                showHome();
            }

            @Override
            public void onError(String message) {
                if (message.contains("401")) logout(false);
                else showOfflineContent(message);
            }
        });
    }

    private void showHome() {
        currentView = "home";
        createRoot();
        addTitle("Smart Heritage");
        addText("Xin chào, " + username + ". Nội dung đã tải vẫn xem được khi mất kết nối.");
        LinearLayout controls = horizontal();
        languageSpinner = new Spinner(this);
        controls.addView(languageSpinner, new LinearLayout.LayoutParams(0,
                dp(48), 1));
        Button scanButton = button("Dò beacon");
        scanButton.setOnClickListener(view -> requestBlePermissions());
        controls.addView(scanButton);
        root.addView(controls);
        loadLanguages();

        LinearLayout tabs = horizontal();
        addTab(tabs, "Khám phá", "home");
        addTab(tabs, "Yêu thích", "favorites");
        addTab(tabs, "Lịch sử", "history");
        addTab(tabs, "Hồ sơ", "profile");
        root.addView(tabs);

        scroll = new ScrollView(this);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(4), dp(12), dp(4), dp(28));
        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
        renderCurrentView();
        showCachedContentButton();
    }

    private void addTab(LinearLayout parent, String label, String view) {
        Button tab = button(label);
        tab.setOnClickListener(ignored -> {
            finishCurrentInteraction();
            currentView = view;
            renderCurrentView();
        });
        parent.addView(tab, new LinearLayout.LayoutParams(0, dp(44), 1));
    }

    private void renderCurrentView() {
        if (content == null) return;
        content.removeAllViews();
        switch (currentView) {
            case "favorites" -> loadFavorites();
            case "history" -> loadHistory();
            case "profile" -> renderProfile();
            default -> loadCatalog();
        }
    }

    private void loadLanguages() {
        api.get("/api/languages", new ApiClient.Callback() {
            @Override
            public void onSuccess(JSONObject response) {
                JSONArray data = response.optJSONArray("data");
                if (data == null || languageSpinner == null) return;
                List<String> labels = new ArrayList<>();
                List<String> codes = new ArrayList<>();
                int selected = 0;
                for (int i = 0; i < data.length(); i++) {
                    JSONObject item = data.optJSONObject(i);
                    if (item == null) continue;
                    codes.add(item.optString("code"));
                    labels.add(item.optString("name"));
                    if (language.equalsIgnoreCase(item.optString("code"))) selected = codes.size() - 1;
                }
                languageSpinner.setAdapter(new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, labels));
                languageSpinner.setSelection(selected);
                languageSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(android.widget.AdapterView<?> parent, View view,
                            int position, long id) {
                        if (position < codes.size() && !language.equals(codes.get(position))) {
                            language = codes.get(position);
                            preferences.edit().putString(LANGUAGE_KEY, language).apply();
                            updatePreferredLanguage();
                            if ("home".equals(currentView)) loadCatalog();
                        }
                    }

                    @Override
                    public void onNothingSelected(android.widget.AdapterView<?> parent) {
                    }
                });
            }

            @Override
            public void onError(String message) {
                toast(message);
            }
        });
    }

    private void updatePreferredLanguage() {
        if (token == null) return;
        api.get("/api/users/me", new ApiClient.Callback() {
            @Override
            public void onSuccess(JSONObject response) {
                JSONObject user = response.optJSONObject("data");
                if (user != null) {
                    JSONObject profile = json("username", username,
                            "email", user.optString("email"), "preferredLanguage", language);
                    api.put("/api/users/me", profile, new ApiClient.Callback() {
                        @Override
                        public void onSuccess(JSONObject ignored) {
                        }

                        @Override
                        public void onError(String message) {
                            toast(message);
                        }
                    });
                }
            }

            @Override
            public void onError(String message) {
                toast(message);
            }
        });
    }

    private void loadCatalog() {
        api.get("/api/sites", new ApiClient.Callback() {
            @Override
            public void onSuccess(JSONObject response) {
                JSONArray sites = response.optJSONArray("data");
                if (sites != null) {
                    addSectionTitle(content, "Khu di tích");
                    for (int i = 0; i < sites.length(); i++) {
                        JSONObject site = sites.optJSONObject(i);
                        if (site == null) continue;
                        addText(content, site.optString("name") + " · " + site.optString("address"));
                    }
                }
                loadArtifacts();
            }

            @Override
            public void onError(String message) {
                toast(message);
                loadArtifacts();
            }
        });
    }

    private void loadArtifacts() {
        String path = "/api/artifacts?lang=" + encode(language);
        api.get(path, new ApiClient.Callback() {
            @Override
            public void onSuccess(JSONObject response) {
                JSONArray artifacts = response.optJSONArray("data");
                if (artifacts == null) return;
                addSectionTitle(content, "Hiện vật");
                EditText search = input("Tìm theo mã hoặc vị trí");
                content.addView(search);
                Button searchButton = button("Tìm");
                content.addView(searchButton);
                searchButton.setOnClickListener(view -> api.get(
                        "/api/artifacts?search=" + encode(search.getText().toString())
                                + "&lang=" + encode(language), new ApiClient.Callback() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                content.removeAllViews();
                                JSONArray found = result.optJSONArray("data");
                                if (found != null) addArtifacts(found);
                            }

                            @Override
                            public void onError(String message) {
                                toast(message);
                            }
                        }));
                addArtifacts(artifacts);
            }

            @Override
            public void onError(String message) {
                toast(message);
                showOfflineContent(message);
            }
        });
    }

    private void addArtifacts(JSONArray artifacts) {
        for (int i = 0; i < artifacts.length(); i++) {
            JSONObject artifact = artifacts.optJSONObject(i);
            if (artifact == null) continue;
            JSONObject translation = artifact.optJSONObject("content");
            String title = translation == null ? artifact.optString("code")
                    : translation.optString("title", artifact.optString("code"));
            Button item = button(title + " · " + artifact.optString("siteName"));
            item.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            item.setOnClickListener(view -> showArtifact(artifact, false, true));
            content.addView(item);
        }
    }

    private void showArtifact(JSONObject artifact, boolean fromBeacon, boolean trackInteraction) {
        ensureContentContainer();
        if (trackInteraction && !fromBeacon) {
            finishCurrentInteraction();
            activeArtifact = artifact;
            activeBeaconId = null;
            activeEventKey = UUID.randomUUID().toString();
            activeArtifactStartedAt = SystemClock.elapsedRealtime();
        }
        currentView = "detail";
        content.removeAllViews();
        JSONObject translation = artifact.optJSONObject("content");
        if (translation == null) translation = new JSONObject();
        addSectionTitle(content, translation.optString("title", artifact.optString("code")));
        addText(content, translation.optString("description", "Chưa có nội dung."));
        addText(content, "Khu di tích: " + artifact.optString("siteName"));
        addText(content, "Vị trí: " + artifact.optString("locationName"));

        addMediaButton("Mở ảnh", translation.optString("imageUrl", ""));
        addMediaButton("Xem video", translation.optString("videoUrl", ""));
        addMediaButton("Nghe thuyết minh", translation.optString("audioUrl", ""));

        Button favorite = button("Thêm vào yêu thích");
        favorite.setOnClickListener(view -> api.post("/api/favorites/"
                + artifact.optLong("id") + "?lang=" + encode(language), new JSONObject(),
                callbackWithMessage("Đã lưu hiện vật yêu thích.")));
        content.addView(favorite);
        Button rate = button("Đánh giá hiện vật");
        rate.setOnClickListener(view -> showFeedbackDialog(artifact.optLong("id")));
        content.addView(rate);
        Button map = button("Mở vị trí trên bản đồ");
        map.setOnClickListener(view -> openMap(artifact));
        content.addView(map);
        preferences.edit().putString(LAST_CONTENT_KEY, artifact.toString()).apply();
    }

    private void addMediaButton(String label, String url) {
        if (url == null || url.trim().isEmpty()) return;
        Button media = button(label);
        media.setOnClickListener(view -> {
            try {
                Uri mediaUri = Uri.parse(url);
                if (mediaUri.getScheme() == null) {
                    mediaUri = Uri.parse(BuildConfig.API_BASE_URL + (url.startsWith("/") ? url : "/" + url));
                }
                startActivity(new Intent(Intent.ACTION_VIEW, mediaUri));
            } catch (android.content.ActivityNotFoundException exception) {
                toast("Không tìm thấy ứng dụng để mở nội dung này.");
            }
        });
        content.addView(media);
    }

    private void openMap(JSONObject artifact) {
        double latitude = artifact.optDouble("latitude", Double.NaN);
        double longitude = artifact.optDouble("longitude", Double.NaN);
        if (Double.isNaN(latitude) || Double.isNaN(longitude)) {
            toast("Hiện vật chưa có tọa độ bản đồ.");
            return;
        }
        String label = artifact.optString("locationName", artifact.optString("code"));
        Uri location = Uri.parse("geo:" + latitude + "," + longitude
                + "?q=" + latitude + "," + longitude + "(" + Uri.encode(label) + ")");
        startActivity(new Intent(Intent.ACTION_VIEW, location));
    }

    private void loadFavorites() {
        api.get("/api/favorites?lang=" + encode(language), new ApiClient.Callback() {
            @Override
            public void onSuccess(JSONObject response) {
                JSONArray data = response.optJSONArray("data");
                if (data == null) return;
                for (int i = 0; i < data.length(); i++) {
                    JSONObject artifact = data.optJSONObject(i);
                    if (artifact == null) continue;
                    JSONObject translation = artifact.optJSONObject("content");
                    String title = translation == null ? artifact.optString("code")
                            : translation.optString("title", artifact.optString("code"));
                    LinearLayout row = horizontal();
                    Button open = button(title);
                    open.setOnClickListener(view -> showArtifact(artifact, false, true));
                    Button remove = button("Bỏ lưu");
                    remove.setOnClickListener(view -> api.delete(
                            "/api/favorites/" + artifact.optLong("id"), new ApiClient.Callback() {
                                @Override
                                public void onSuccess(JSONObject ignored) {
                                    loadFavorites();
                                }

                                @Override
                                public void onError(String message) {
                                    toast(message);
                                }
                            }));
                    row.addView(open, new LinearLayout.LayoutParams(0, dp(48), 1));
                    row.addView(remove);
                    content.addView(row);
                }
            }

            @Override
            public void onError(String message) {
                toast(message);
            }
        });
    }

    private void loadHistory() {
        api.get("/api/history", new ApiClient.Callback() {
            @Override
            public void onSuccess(JSONObject response) {
                JSONArray data = response.optJSONArray("data");
                if (data == null) return;
                for (int i = 0; i < data.length(); i++) {
                    JSONObject interaction = data.optJSONObject(i);
                    if (interaction == null) continue;
                    Button item = button(interaction.optString("artifactCode")
                            + " · " + interaction.optInt("durationSeconds") + " giây");
                    item.setOnClickListener(view -> api.get(
                            "/api/artifacts/" + interaction.optLong("artifactId")
                                    + "?lang=" + encode(language), new ApiClient.Callback() {
                                @Override
                                public void onSuccess(JSONObject result) {
                                    JSONObject artifact = result.optJSONObject("data");
                                    if (artifact != null) showArtifact(artifact, false, true);
                                }

                                @Override
                                public void onError(String message) {
                                    toast(message);
                                }
                            }));
                    content.addView(item);
                }
            }

            @Override
            public void onError(String message) {
                toast(message);
            }
        });
    }

    private void renderProfile() {
        addSectionTitle(content, "Tài khoản");
        addText(content, username);
        addText(content, "Ngôn ngữ ưu tiên: " + language);
        addButton(content, "Đổi mật khẩu", view -> showPasswordDialog());
        addButton(content, "Đăng xuất", view -> logout(true));
    }

    private void showFeedbackDialog(long artifactId) {
        LinearLayout layout = vertical();
        RatingBar rating = new RatingBar(this);
        rating.setNumStars(5);
        rating.setStepSize(1);
        layout.addView(rating);
        EditText comment = input("Chia sẻ cảm nhận");
        layout.addView(comment);
        new AlertDialog.Builder(this).setTitle("Đánh giá hiện vật").setView(layout)
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Gửi", (dialog, which) -> api.post("/api/feedback",
                        json("artifactId", artifactId, "rating", Math.max(1, (int) rating.getRating()),
                                "comment", comment.getText().toString()),
                        callbackWithMessage("Cảm ơn bạn đã gửi đánh giá.")))
                .show();
    }

    private void showPasswordDialog() {
        LinearLayout layout = vertical();
        EditText current = input("Mật khẩu hiện tại");
        current.setInputType(129);
        EditText next = input("Mật khẩu mới (ít nhất 8 ký tự)");
        next.setInputType(129);
        layout.addView(current);
        layout.addView(next);
        new AlertDialog.Builder(this).setTitle("Đổi mật khẩu").setView(layout)
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Cập nhật", (dialog, which) -> api.put("/api/users/me/password",
                        json("currentPassword", current.getText().toString(),
                                "newPassword", next.getText().toString()),
                        callbackWithMessage("Đã đổi mật khẩu.")))
                .show();
    }

    private void requestBlePermissions() {
        List<String> requested = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= 31) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) requested.add(Manifest.permission.ACCESS_FINE_LOCATION);
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN)
                    != PackageManager.PERMISSION_GRANTED) requested.add(Manifest.permission.BLUETOOTH_SCAN);
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) requested.add(Manifest.permission.BLUETOOTH_CONNECT);
        } else if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requested.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!requested.isEmpty()) {
            requestPermissions(requested.toArray(new String[0]), BLE_PERMISSION_REQUEST);
            return;
        }
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, BLE_PERMISSION_REQUEST);
        }
        startBeaconScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != BLE_PERMISSION_REQUEST) return;
        boolean approved = grantResults.length > 0;
        for (int result : grantResults) approved &= result == PackageManager.PERMISSION_GRANTED;
        if (approved) startBeaconScan();
        else toast("Cần quyền Bluetooth để dò beacon ở gần.");
    }

    private void startBeaconScan() {
        stopBeaconScan();
        beaconScanner = new IBeaconScanner(this, this);
        try {
            if (!beaconScanner.start()) toast("Bluetooth đang tắt hoặc thiết bị không hỗ trợ BLE.");
            else toast("Đang dò iBeacon gần bạn.");
        } catch (SecurityException exception) {
            toast("Quyền Bluetooth chưa được cấp.");
        }
    }

    private void stopBeaconScan() {
        if (beaconScanner != null) {
            try {
                beaconScanner.stop();
            } catch (SecurityException exception) {
                toast("Không thể dừng Bluetooth scan do thiếu quyền.");
            }
            beaconScanner = null;
        }
    }

    @Override
    public void onBeacon(IBeaconScanner.Beacon beacon) {
        runOnUiThread(() -> handleBeacon(beacon));
    }

    @Override
    public void onScanError(int errorCode) {
        runOnUiThread(() -> toast("BLE scan thất bại: " + errorCode));
    }

    private void handleBeacon(IBeaconScanner.Beacon beacon) {
        if (beacon.rssi < -80) return;
        lastBeaconSeen[0] = SystemClock.elapsedRealtime();
        if (beacon.key().equals(activeBeaconKey)) return;
        finishCurrentInteraction();
        activeBeaconKey = beacon.key();
        String detectedKey = beacon.key();
        api.get("/api/beacons/resolve?uuid=" + encode(beacon.uuid) + "&major=" + beacon.major
                + "&minor=" + beacon.minor + "&lang=" + encode(language), new ApiClient.Callback() {
            @Override
            public void onSuccess(JSONObject response) {
                if (!detectedKey.equals(activeBeaconKey)) return;
                JSONObject resolution = response.optJSONObject("data");
                if (resolution == null) {
                    activeBeaconKey = "";
                    return;
                }
                activeBeaconId = resolution.optLong("beaconId");
                activeArtifact = resolution.optJSONObject("artifact");
                if (activeArtifact == null) return;
                activeEventKey = UUID.randomUUID().toString();
                activeArtifactStartedAt = SystemClock.elapsedRealtime();
                showArtifact(activeArtifact, true, true);
                notifyNearby(activeArtifact);
            }

            @Override
            public void onError(String message) {
                activeBeaconKey = "";
                toast("Beacon đã nhận diện nhưng chưa được gán nội dung.");
            }
        });
    }

    private void finishCurrentInteraction() {
        if (activeArtifact == null || activeEventKey == null) return;
        int durationSeconds = (int) Math.max(0,
                (SystemClock.elapsedRealtime() - activeArtifactStartedAt) / 1000);
        JSONObject body = json("eventKey", activeEventKey,
                "artifactId", activeArtifact.optLong("id"),
                "beaconId", activeBeaconId,
                "languageCode", language,
                "durationSeconds", durationSeconds);
        api.post("/api/interactions", body, new ApiClient.Callback() {
            @Override
            public void onSuccess(JSONObject response) {
            }

            @Override
            public void onError(String message) {
                toast("Chưa đồng bộ được lịch sử tương tác: " + message);
            }
        });
        activeArtifact = null;
        activeBeaconId = null;
        activeEventKey = null;
    }

    private void notifyNearby(JSONObject artifact) {
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return;
        String title = artifact.optJSONObject("content") == null
                ? artifact.optString("code")
                : artifact.optJSONObject("content").optString("title", artifact.optString("code"));
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pending = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.notify(1, new Notification.Builder(this, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle("Di sản ở gần bạn")
                    .setContentText(title)
                    .setContentIntent(pending)
                    .setAutoCancel(true)
                    .build());
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Nội dung di sản ở gần", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    private void showCachedContentButton() {
        String cached = preferences.getString(LAST_CONTENT_KEY, null);
        if (cached == null) return;
        Button offline = button("Mở nội dung đã xem gần đây");
        offline.setOnClickListener(view -> {
            try {
                showArtifact(new JSONObject(cached), false, false);
            } catch (JSONException exception) {
                toast("Nội dung ngoại tuyến bị lỗi.");
            }
        });
        ensureContentContainer();
        content.addView(offline);
    }

    private void showOfflineContent(String message) {
        createRoot();
        addTitle("Smart Heritage");
        addText(message);
        ensureContentContainer();
        String cached = preferences.getString(LAST_CONTENT_KEY, null);
        if (cached != null) {
            try {
                showArtifact(new JSONObject(cached), false, false);
            } catch (JSONException exception) {
                toast("Không thể mở nội dung đã lưu.");
            }
        }
    }

    private void logout(boolean callServer) {
        String currentToken = token;
        stopBeaconScan();
        finishCurrentInteraction();
        if (callServer && currentToken != null) {
            api.post("/api/auth/logout", new JSONObject(), new ApiClient.Callback() {
                @Override
                public void onSuccess(JSONObject response) {
                }

                @Override
                public void onError(String message) {
                    toast(message);
                }
            });
        }
        token = null;
        username = "";
        api.setAccessToken(null);
        preferences.edit().remove(TOKEN_KEY).apply();
        showLogin();
    }

    private ApiClient.Callback callbackWithMessage(String successMessage) {
        return new ApiClient.Callback() {
            @Override
            public void onSuccess(JSONObject response) {
                toast(successMessage);
            }

            @Override
            public void onError(String message) {
                toast(message);
            }
        };
    }

    private void createRoot() {
        root = vertical();
        root.setPadding(dp(18), dp(18), dp(18), dp(12));
        root.setBackgroundColor(0xFFF4F7F5);
        setContentView(root);
        content = null;
        scroll = null;
    }

    private void ensureContentContainer() {
        if (content != null) return;
        scroll = new ScrollView(this);
        content = vertical();
        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
    }

    private void addTitle(String text) {
        TextView title = new TextView(this);
        title.setText(text);
        title.setTextColor(0xFF153A35);
        title.setTextSize(25);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setPadding(0, dp(8), 0, dp(8));
        root.addView(title);
    }

    private void addSectionTitle(LinearLayout parent, String text) {
        TextView title = new TextView(this);
        title.setText(text);
        title.setTextColor(0xFF153A35);
        title.setTextSize(19);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setPadding(0, dp(12), 0, dp(8));
        parent.addView(title);
    }

    private void addText(String text) {
        addText(root, text);
    }

    private void addText(LinearLayout parent, String text) {
        TextView label = new TextView(this);
        label.setText(text);
        label.setTextColor(0xFF42534F);
        label.setTextSize(15);
        label.setPadding(0, dp(8), 0, dp(8));
        parent.addView(label);
    }

    private EditText input(String hint) {
        EditText field = new EditText(this);
        field.setSingleLine(true);
        field.setHint(hint);
        field.setTextSize(15);
        field.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(52)));
        return field;
    }

    private void addButton(String label, View.OnClickListener action) {
        addButton(root, label, action);
    }

    private void addButton(LinearLayout parent, String label, View.OnClickListener action) {
        Button button = button(label);
        button.setOnClickListener(action);
        parent.addView(button);
    }

    private Button button(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextColor(0xFF153A35);
        button.setAllCaps(false);
        button.setMinHeight(dp(44));
        return button;
    }

    private LinearLayout vertical() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        return layout;
    }

    private LinearLayout horizontal() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        return layout;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private JSONObject json(Object... values) {
        JSONObject body = new JSONObject();
        try {
            for (int i = 0; i + 1 < values.length; i += 2) {
                body.put((String) values[i], values[i + 1]);
            }
        } catch (JSONException exception) {
            toast("Không thể tạo dữ liệu yêu cầu.");
        }
        return body;
    }

    private String encode(String value) {
        return Uri.encode(value);
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
