package be.michiel.edgeback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

final class UpdateChecker {
    private static final String LATEST_RELEASE_API =
            "https://api.github.com/repos/Michiel0912/magic-swipe/releases/latest";
    private static final long AUTO_CHECK_INTERVAL_MS = 24L * 60L * 60L * 1000L;

    private UpdateChecker() {}

    static void checkAutomatically(Activity activity, SharedPreferences prefs) {
        if (!prefs.getBoolean(Prefs.AUTO_UPDATE_CHECK, Prefs.DEFAULT_AUTO_UPDATE_CHECK)) return;

        long now = System.currentTimeMillis();
        long last = prefs.getLong(Prefs.LAST_UPDATE_CHECK_MS, 0L);
        if (last > 0L && now - last < AUTO_CHECK_INTERVAL_MS) return;

        // Record the attempt before starting it so activity resumes do not cause a retry loop.
        prefs.edit().putLong(Prefs.LAST_UPDATE_CHECK_MS, now).apply();
        check(activity, false);
    }

    static void checkNow(Activity activity) {
        check(activity, true);
    }

    private static void check(Activity activity, boolean manual) {
        new Thread(() -> {
            try {
                ReleaseInfo release = fetchLatestRelease();
                String currentVersion = getCurrentVersion(activity);
                boolean newer = compareVersions(release.version, currentVersion) > 0;

                activity.runOnUiThread(() -> {
                    if (activity.isFinishing() || activity.isDestroyed()) return;
                    if (newer) {
                        showUpdateDialog(activity, currentVersion, release);
                    } else if (manual) {
                        Toast.makeText(activity,
                                activity.getString(R.string.update_up_to_date, currentVersion),
                                Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Throwable error) {
                if (manual) {
                    activity.runOnUiThread(() -> {
                        if (activity.isFinishing() || activity.isDestroyed()) return;
                        Toast.makeText(activity, R.string.update_check_failed, Toast.LENGTH_LONG).show();
                    });
                }
            }
        }, "MagicSwipeUpdateCheck").start();
    }

    private static ReleaseInfo fetchLatestRelease() throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(LATEST_RELEASE_API).openConnection();
        connection.setConnectTimeout(7000);
        connection.setReadTimeout(7000);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/vnd.github+json");
        connection.setRequestProperty("X-GitHub-Api-Version", "2026-03-10");
        connection.setRequestProperty("User-Agent", "Magic-Swipe-Android");

        try {
            int status = connection.getResponseCode();
            if (status < 200 || status >= 300) {
                throw new IllegalStateException("GitHub HTTP " + status);
            }

            StringBuilder json = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) json.append(line);
            }

            JSONObject root = new JSONObject(json.toString());
            String tag = root.optString("tag_name", "").trim();
            if (tag.isEmpty()) throw new IllegalStateException("Release has no tag");

            String version = normalizeVersion(tag);
            String body = root.optString("body", "").trim();
            String releasePage = root.optString("html_url", "").trim();
            String apkUrl = "";

            JSONArray assets = root.optJSONArray("assets");
            if (assets != null) {
                for (int i = 0; i < assets.length(); i++) {
                    JSONObject asset = assets.optJSONObject(i);
                    if (asset == null) continue;
                    String name = asset.optString("name", "");
                    String url = asset.optString("browser_download_url", "");
                    if (name.toLowerCase().endsWith(".apk") && !url.isEmpty()) {
                        apkUrl = url;
                        break;
                    }
                }
            }

            if (apkUrl.isEmpty()) apkUrl = releasePage;
            if (apkUrl.isEmpty()) throw new IllegalStateException("Release has no download URL");
            return new ReleaseInfo(version, body, apkUrl);
        } finally {
            connection.disconnect();
        }
    }

    private static String getCurrentVersion(Activity activity) throws Exception {
        PackageInfo info = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
        String version = info.versionName;
        return version == null ? "0" : normalizeVersion(version);
    }

    private static void showUpdateDialog(Activity activity, String currentVersion, ReleaseInfo release) {
        String notes = release.body;
        if (notes.length() > 1800) notes = notes.substring(0, 1800) + "…";

        StringBuilder message = new StringBuilder(activity.getString(
                R.string.update_available_message, currentVersion, release.version));
        if (!notes.isEmpty()) message.append("\n\n").append(notes);

        new AlertDialog.Builder(activity)
                .setTitle(R.string.update_available_title)
                .setMessage(message.toString())
                .setNegativeButton(R.string.update_later, null)
                .setPositiveButton(R.string.update_download, (dialog, which) -> {
                    Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(release.downloadUrl));
                    try {
                        activity.startActivity(browser);
                    } catch (Throwable ignored) {
                        Toast.makeText(activity, R.string.update_open_failed, Toast.LENGTH_LONG).show();
                    }
                })
                .show();
    }

    static int compareVersions(String left, String right) {
        String[] a = normalizeVersion(left).split("\\.");
        String[] b = normalizeVersion(right).split("\\.");
        int count = Math.max(a.length, b.length);
        for (int i = 0; i < count; i++) {
            int av = i < a.length ? parsePart(a[i]) : 0;
            int bv = i < b.length ? parsePart(b[i]) : 0;
            if (av != bv) return Integer.compare(av, bv);
        }
        return 0;
    }

    private static String normalizeVersion(String version) {
        String v = version == null ? "" : version.trim();
        if (v.startsWith("v") || v.startsWith("V")) v = v.substring(1);
        int dash = v.indexOf('-');
        if (dash >= 0) v = v.substring(0, dash);
        return v.isEmpty() ? "0" : v;
    }

    private static int parsePart(String value) {
        int result = 0;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c < '0' || c > '9') break;
            result = result * 10 + (c - '0');
        }
        return result;
    }

    private static final class ReleaseInfo {
        final String version;
        final String body;
        final String downloadUrl;

        ReleaseInfo(String version, String body, String downloadUrl) {
            this.version = version;
            this.body = body;
            this.downloadUrl = downloadUrl;
        }
    }
}
