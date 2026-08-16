package be.michiel.edgeback;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

public final class MainActivity extends Activity {
    private SharedPreferences prefs;
    private TextView serviceStatus;
    private TextView nativeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = Prefs.get(this);
        setContentView(buildUi());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshStatus();
    }

    private View buildUi() {
        int pad = Prefs.dp(this, 20);
        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(pad, pad, pad, pad);

        // Android 15/16 enforce edge-to-edge for recent target SDKs. Respect both
        // system bars and the display cutout so the header never sits under a
        // centred punch-hole/front camera or another cutout.
        root.setOnApplyWindowInsetsListener((v, insets) -> {
            int left = insets.getSystemWindowInsetLeft();
            int top = insets.getSystemWindowInsetTop();
            int right = insets.getSystemWindowInsetRight();
            int bottom = insets.getSystemWindowInsetBottom();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && insets.getDisplayCutout() != null) {
                left = Math.max(left, insets.getDisplayCutout().getSafeInsetLeft());
                top = Math.max(top, insets.getDisplayCutout().getSafeInsetTop());
                right = Math.max(right, insets.getDisplayCutout().getSafeInsetRight());
                bottom = Math.max(bottom, insets.getDisplayCutout().getSafeInsetBottom());
            }
            v.setPadding(pad + left, pad + top, pad + right, pad + bottom);
            return insets;
        });
        scroll.addView(root);
        root.requestApplyInsets();

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.ic_logo_mark);
        logo.setContentDescription(getString(R.string.app_name));
        logo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        LinearLayout.LayoutParams logoParams = new LinearLayout.LayoutParams(
                Prefs.dp(this, 72), Prefs.dp(this, 72));
        root.addView(logo, logoParams);

        TextView title = new TextView(this);
        title.setText(R.string.app_name);
        title.setTextSize(26);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        root.addView(title);

        TextView intro = new TextView(this);
        intro.setText(R.string.intro);
        intro.setTextSize(16);
        intro.setPadding(0, Prefs.dp(this, 8), 0, Prefs.dp(this, 16));
        root.addView(intro);

        serviceStatus = new TextView(this);
        serviceStatus.setTextSize(17);
        serviceStatus.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        root.addView(serviceStatus);

        nativeInfo = new TextView(this);
        nativeInfo.setTextSize(15);
        nativeInfo.setPadding(0, Prefs.dp(this, 5), 0, Prefs.dp(this, 12));
        root.addView(nativeInfo);

        Button accessibility = new Button(this);
        accessibility.setText(R.string.open_accessibility_settings);
        accessibility.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        root.addView(accessibility);

        addSection(root, getString(R.string.section_language));
        TextView languageSummary = new TextView(this);
        languageSummary.setText(R.string.language_summary);
        languageSummary.setTextSize(14);
        languageSummary.setPadding(0, 0, 0, Prefs.dp(this, 6));
        root.addView(languageSummary);

        Button language = new Button(this);
        language.setText(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? R.string.open_language_settings : R.string.open_system_language_settings);
        language.setOnClickListener(v -> openLanguageSettings());
        root.addView(language);

        addSection(root, getString(R.string.section_sides));
        addSwitch(root, getString(R.string.left_side), Prefs.LEFT, Prefs.DEFAULT_LEFT);
        addSwitch(root, getString(R.string.right_side), Prefs.RIGHT, Prefs.DEFAULT_RIGHT);

        addSection(root, getString(R.string.section_area_gesture));
        addSeek(root, getString(R.string.total_back_zone), getString(R.string.suffix_from_edge),
                Prefs.TARGET_WIDTH_DP, 16, 36, Prefs.DEFAULT_TARGET_WIDTH_DP);
        addSeek(root, getString(R.string.swipe_distance), getString(R.string.suffix_horizontal_before_back),
                Prefs.TRIGGER_DISTANCE_DP, 16, 44, Prefs.DEFAULT_TRIGGER_DISTANCE_DP);
        addSeek(root, getString(R.string.exclude_top), getString(R.string.suffix_dp),
                Prefs.TOP_EXCLUDE_DP, 0, 80, Prefs.DEFAULT_TOP_EXCLUDE_DP);
        addSeek(root, getString(R.string.exclude_bottom), getString(R.string.suffix_bottom_exclusion),
                Prefs.BOTTOM_EXCLUDE_DP, 40, 160, Prefs.DEFAULT_BOTTOM_EXCLUDE_DP);

        addSection(root, getString(R.string.section_behavior));
        addCheck(root, getString(R.string.haptic_back), Prefs.HAPTIC, Prefs.DEFAULT_HAPTIC);
        addCheck(root, getString(R.string.debug_zones), Prefs.DEBUG, Prefs.DEFAULT_DEBUG);

        Button reset = new Button(this);
        reset.setText(R.string.restore_defaults);
        reset.setOnClickListener(v -> {
            prefs.edit()
                    .putBoolean(Prefs.LEFT, Prefs.DEFAULT_LEFT)
                    .putBoolean(Prefs.RIGHT, Prefs.DEFAULT_RIGHT)
                    .putInt(Prefs.TARGET_WIDTH_DP, Prefs.DEFAULT_TARGET_WIDTH_DP)
                    .putInt(Prefs.TRIGGER_DISTANCE_DP, Prefs.DEFAULT_TRIGGER_DISTANCE_DP)
                    .putInt(Prefs.TOP_EXCLUDE_DP, Prefs.DEFAULT_TOP_EXCLUDE_DP)
                    .putInt(Prefs.BOTTOM_EXCLUDE_DP, Prefs.DEFAULT_BOTTOM_EXCLUDE_DP)
                    .putBoolean(Prefs.HAPTIC, Prefs.DEFAULT_HAPTIC)
                    .putBoolean(Prefs.DEBUG, Prefs.DEFAULT_DEBUG)
                    .apply();
            recreate();
        });
        LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rp.topMargin = Prefs.dp(this, 18);
        root.addView(reset, rp);

        TextView privacy = new TextView(this);
        privacy.setText(R.string.privacy_text);
        privacy.setTextSize(13);
        privacy.setPadding(0, Prefs.dp(this, 18), 0, Prefs.dp(this, 24));
        root.addView(privacy);

        return scroll;
    }

    private void openLanguageSettings() {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent = new Intent(Settings.ACTION_APP_LOCALE_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
        } else {
            intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
        }
        try {
            startActivity(intent);
        } catch (Throwable ignored) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }

    private void addSection(LinearLayout root, String text) {
        TextView v = new TextView(this);
        v.setText(text);
        v.setTextSize(19);
        v.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        v.setPadding(0, Prefs.dp(this, 22), 0, Prefs.dp(this, 6));
        root.addView(v);
    }

    private void addSwitch(LinearLayout root, String label, String key, boolean def) {
        Switch s = new Switch(this);
        s.setText(label);
        s.setTextSize(16);
        s.setChecked(prefs.getBoolean(key, def));
        s.setOnCheckedChangeListener((buttonView, isChecked) -> prefs.edit().putBoolean(key, isChecked).apply());
        root.addView(s);
    }

    private void addCheck(LinearLayout root, String label, String key, boolean def) {
        CheckBox c = new CheckBox(this);
        c.setText(label);
        c.setTextSize(16);
        c.setChecked(prefs.getBoolean(key, def));
        c.setOnCheckedChangeListener((buttonView, isChecked) -> prefs.edit().putBoolean(key, isChecked).apply());
        root.addView(c);
    }

    private void addSeek(LinearLayout root, String label, String suffix,
                         String key, int min, int max, int def) {
        TextView value = new TextView(this);
        value.setTextSize(15);
        int current = prefs.getInt(key, def);
        value.setText(getString(R.string.seek_value_format, label, current, suffix));
        root.addView(value);

        SeekBar seek = new SeekBar(this);
        seek.setMax(max - min);
        seek.setProgress(current - min);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int v = min + progress;
                value.setText(getString(R.string.seek_value_format, label, v, suffix));
                if (fromUser) prefs.edit().putInt(key, v).apply();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        root.addView(seek);
    }

    private void refreshStatus() {
        boolean enabled = isServiceEnabled();
        serviceStatus.setText(enabled ? R.string.service_active : R.string.service_inactive);
        int px = Prefs.detectNativeBackInsetPx(this);
        nativeInfo.setText(getString(R.string.native_zone_format, px, Prefs.pxToDp(this, px)));
    }

    private boolean isServiceEnabled() {
        AccessibilityManager am = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabled = am.getEnabledAccessibilityServiceList(
                AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
        for (AccessibilityServiceInfo info : enabled) {
            if (info.getResolveInfo() != null && info.getResolveInfo().serviceInfo != null
                    && getPackageName().equals(info.getResolveInfo().serviceInfo.packageName)
                    && EdgeBackAccessibilityService.class.getName().equals(info.getResolveInfo().serviceInfo.name)) {
                return true;
            }
        }
        return false;
    }
}
