package be.michiel.edgeback;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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
    private int COLOR_BG;
    private int COLOR_CARD;
    private int COLOR_CARD_ALT;
    private int COLOR_BORDER;
    private int COLOR_TEXT;
    private int COLOR_MUTED;
    private int COLOR_CYAN;
    private int COLOR_GREEN;
    private int COLOR_RED;
    private int COLOR_ON_ACCENT;
    private boolean darkMode;

    private SharedPreferences prefs;
    private TextView serviceStatus;
    private TextView nativeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = Prefs.get(this);
        darkMode = Prefs.isDarkMode(this, prefs);
        setTheme(darkMode
                ? android.R.style.Theme_Material_NoActionBar
                : android.R.style.Theme_Material_Light_NoActionBar);
        super.onCreate(savedInstanceState);

        applyPalette();
        applySystemBars();
        setContentView(buildUi());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshStatus();
        UpdateChecker.checkAutomatically(this, prefs);
    }

    private void applyPalette() {
        if (darkMode) {
            COLOR_BG = Color.rgb(6, 15, 27);
            COLOR_CARD = Color.rgb(15, 27, 45);
            COLOR_CARD_ALT = Color.rgb(18, 33, 54);
            COLOR_BORDER = Color.rgb(32, 55, 79);
            COLOR_TEXT = Color.rgb(248, 250, 252);
            COLOR_MUTED = Color.rgb(157, 174, 196);
            COLOR_CYAN = Color.rgb(34, 211, 238);
            COLOR_GREEN = Color.rgb(52, 211, 153);
            COLOR_RED = Color.rgb(251, 113, 133);
            COLOR_ON_ACCENT = Color.rgb(6, 15, 27);
        } else {
            COLOR_BG = Color.rgb(247, 249, 252);
            COLOR_CARD = Color.WHITE;
            COLOR_CARD_ALT = Color.rgb(241, 245, 249);
            COLOR_BORDER = Color.rgb(203, 213, 225);
            COLOR_TEXT = Color.rgb(15, 23, 42);
            COLOR_MUTED = Color.rgb(71, 85, 105);
            COLOR_CYAN = Color.rgb(8, 145, 178);
            COLOR_GREEN = Color.rgb(21, 128, 61);
            COLOR_RED = Color.rgb(190, 18, 60);
            COLOR_ON_ACCENT = Color.WHITE;
        }
    }

    private void applySystemBars() {
        getWindow().setStatusBarColor(COLOR_BG);
        getWindow().setNavigationBarColor(COLOR_BG);

        int flags = 0;
        if (!darkMode && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        if (!darkMode && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }
        getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    private View buildUi() {
        int pad = Prefs.dp(this, 18);
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(COLOR_BG);
        scroll.setClipToPadding(false);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(pad, pad, pad, pad);
        root.setBackgroundColor(COLOR_BG);
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
        scroll.addView(root, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        root.requestApplyInsets();

        addHeader(root);
        addServiceCard(root);
        addGestureCard(root);
        addProtectionCard(root);
        addLanguageCard(root);
        addAppearanceCard(root);
        addUpdateCard(root);
        addPrivacyCard(root);
        addFooter(root);
        return scroll;
    }

    private void addHeader(LinearLayout root) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.ic_logo_mark);
        logo.setContentDescription(getString(R.string.app_name));
        logo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        LinearLayout.LayoutParams logoParams = new LinearLayout.LayoutParams(
                Prefs.dp(this, 70), Prefs.dp(this, 70));
        logoParams.rightMargin = Prefs.dp(this, 14);
        row.addView(logo, logoParams);

        LinearLayout textCol = new LinearLayout(this);
        textCol.setOrientation(LinearLayout.VERTICAL);
        textCol.setGravity(Gravity.CENTER_VERTICAL);
        textCol.addView(text(getString(R.string.app_name), 29, COLOR_CYAN, true));
        textCol.addView(text("v0.4.1", 13, COLOR_MUTED, false));
        row.addView(textCol, new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        root.addView(row);

        TextView intro = text(getString(R.string.intro), 15, COLOR_TEXT, false);
        intro.setLineSpacing(0f, 1.12f);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.topMargin = Prefs.dp(this, 10);
        p.bottomMargin = Prefs.dp(this, 14);
        root.addView(intro, p);
    }

    private void addServiceCard(LinearLayout root) {
        LinearLayout card = card();
        LinearLayout top = new LinearLayout(this);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);
        serviceStatus = text("", 18, COLOR_GREEN, true);
        top.addView(serviceStatus, new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        top.addView(text("●", 18, COLOR_GREEN, true));
        card.addView(top);
        nativeInfo = text("", 14, COLOR_MUTED, false);
        nativeInfo.setLineSpacing(0f, 1.1f);
        LinearLayout.LayoutParams np = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        np.topMargin = Prefs.dp(this, 7);
        np.bottomMargin = Prefs.dp(this, 12);
        card.addView(nativeInfo, np);
        Button accessibility = primaryButton(getString(R.string.open_accessibility_settings));
        accessibility.setOnClickListener(v -> startActivity(
                new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        card.addView(accessibility);
        addCard(root, card);
    }

    private void addGestureCard(LinearLayout root) {
        LinearLayout card = card();
        addCardTitle(card, getString(R.string.section_area_gesture));
        addSwitch(card, getString(R.string.left_side), Prefs.LEFT, Prefs.DEFAULT_LEFT);
        addSwitch(card, getString(R.string.right_side), Prefs.RIGHT, Prefs.DEFAULT_RIGHT);
        addDivider(card);
        addSeek(card, getString(R.string.total_back_zone), getString(R.string.suffix_from_edge),
                Prefs.TARGET_WIDTH_DP, 16, 36, Prefs.DEFAULT_TARGET_WIDTH_DP);
        addSeek(card, getString(R.string.swipe_distance),
                getString(R.string.suffix_horizontal_before_back),
                Prefs.TRIGGER_DISTANCE_DP, 16, 44, Prefs.DEFAULT_TRIGGER_DISTANCE_DP);
        addCard(root, card);
    }

    private void addProtectionCard(LinearLayout root) {
        LinearLayout card = card();
        addCardTitle(card, getString(R.string.section_behavior));
        addSeek(card, getString(R.string.exclude_top), getString(R.string.suffix_dp),
                Prefs.TOP_EXCLUDE_DP, 0, 80, Prefs.DEFAULT_TOP_EXCLUDE_DP);
        addSeek(card, getString(R.string.exclude_bottom),
                getString(R.string.suffix_bottom_exclusion),
                Prefs.BOTTOM_EXCLUDE_DP, 40, 160, Prefs.DEFAULT_BOTTOM_EXCLUDE_DP);
        addDivider(card);
        addCheck(card, getString(R.string.haptic_back), Prefs.HAPTIC, Prefs.DEFAULT_HAPTIC);
        addCheck(card, getString(R.string.debug_zones), Prefs.DEBUG, Prefs.DEFAULT_DEBUG);
        addCard(root, card);
    }

    private void addLanguageCard(LinearLayout root) {
        LinearLayout card = card();
        addCardTitle(card, getString(R.string.section_language));
        TextView languageSummary = text(getString(R.string.language_summary), 14, COLOR_MUTED, false);
        languageSummary.setLineSpacing(0f, 1.1f);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = Prefs.dp(this, 12);
        card.addView(languageSummary, lp);
        Button language = secondaryButton(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? getString(R.string.open_language_settings)
                : getString(R.string.open_system_language_settings));
        language.setOnClickListener(v -> openLanguageSettings());
        card.addView(language);
        addCard(root, card);
    }

    private void addAppearanceCard(LinearLayout root) {
        LinearLayout card = card();
        addCardTitle(card, getString(R.string.section_appearance));

        TextView summary = text(getString(R.string.appearance_summary), 14, COLOR_MUTED, false);
        summary.setLineSpacing(0f, 1.1f);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        sp.bottomMargin = Prefs.dp(this, 12);
        card.addView(summary, sp);

        Button appearance = secondaryButton(getString(
                R.string.appearance_current, getAppearanceLabel()));
        appearance.setOnClickListener(v -> showAppearanceDialog());
        card.addView(appearance);
        addCard(root, card);
    }

    private String getAppearanceLabel() {
        String value = prefs.getString(Prefs.APPEARANCE, Prefs.DEFAULT_APPEARANCE);
        if (Prefs.APPEARANCE_LIGHT.equals(value)) return getString(R.string.appearance_light);
        if (Prefs.APPEARANCE_DARK.equals(value)) return getString(R.string.appearance_dark);
        return getString(R.string.appearance_system);
    }

    private void showAppearanceDialog() {
        String[] values = {
                Prefs.APPEARANCE_SYSTEM,
                Prefs.APPEARANCE_LIGHT,
                Prefs.APPEARANCE_DARK
        };
        String[] labels = {
                getString(R.string.appearance_system),
                getString(R.string.appearance_light),
                getString(R.string.appearance_dark)
        };

        String current = prefs.getString(Prefs.APPEARANCE, Prefs.DEFAULT_APPEARANCE);
        int checked = 0;
        if (Prefs.APPEARANCE_LIGHT.equals(current)) checked = 1;
        else if (Prefs.APPEARANCE_DARK.equals(current)) checked = 2;

        new AlertDialog.Builder(this)
                .setTitle(R.string.section_appearance)
                .setSingleChoiceItems(labels, checked, (dialog, which) -> {
                    prefs.edit().putString(Prefs.APPEARANCE, values[which]).apply();
                    dialog.dismiss();
                    recreate();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void addUpdateCard(LinearLayout root) {
        LinearLayout card = card();
        addCardTitle(card, getString(R.string.section_updates));

        TextView summary = text(getString(R.string.update_summary), 14, COLOR_MUTED, false);
        summary.setLineSpacing(0f, 1.1f);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        sp.bottomMargin = Prefs.dp(this, 10);
        card.addView(summary, sp);

        Switch automatic = new Switch(this);
        automatic.setText(getString(R.string.auto_update_check));
        automatic.setTextSize(15);
        automatic.setTextColor(COLOR_TEXT);
        automatic.setChecked(prefs.getBoolean(
                Prefs.AUTO_UPDATE_CHECK, Prefs.DEFAULT_AUTO_UPDATE_CHECK));
        automatic.setThumbTintList(accentStates(COLOR_CYAN, inactiveThumbColor()));
        automatic.setTrackTintList(accentStates(activeTrackColor(), inactiveTrackColor()));
        automatic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit()
                    .putBoolean(Prefs.AUTO_UPDATE_CHECK, isChecked);
            if (isChecked) editor.remove(Prefs.LAST_UPDATE_CHECK_MS);
            editor.apply();
            if (isChecked) UpdateChecker.checkAutomatically(this, prefs);
        });
        card.addView(automatic);

        Button check = secondaryButton(getString(R.string.check_updates_now));
        check.setOnClickListener(v -> UpdateChecker.checkNow(this));
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cp.topMargin = Prefs.dp(this, 10);
        card.addView(check, cp);
        addCard(root, card);
    }

    private void addPrivacyCard(LinearLayout root) {
        LinearLayout card = card();
        card.addView(text("✓", 18, COLOR_CYAN, true));
        TextView privacy = text(getString(R.string.privacy_text), 13, COLOR_MUTED, false);
        privacy.setLineSpacing(0f, 1.12f);
        LinearLayout.LayoutParams pp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pp.topMargin = Prefs.dp(this, 8);
        card.addView(privacy, pp);
        addCard(root, card);
    }

    private void addFooter(LinearLayout root) {
        Button github = secondaryButton("GitHub · Michiel0912/magic-swipe");
        github.setOnClickListener(v -> {
            Intent browser = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/Michiel0912/magic-swipe"));
            try { startActivity(browser); } catch (Throwable ignored) {}
        });
        LinearLayout.LayoutParams gp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        gp.topMargin = Prefs.dp(this, 2);
        root.addView(github, gp);

        Button reset = secondaryButton(getString(R.string.restore_defaults));
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
                    .putBoolean(Prefs.AUTO_UPDATE_CHECK, Prefs.DEFAULT_AUTO_UPDATE_CHECK)
                    .putString(Prefs.APPEARANCE, Prefs.DEFAULT_APPEARANCE)
                    .remove(Prefs.LAST_UPDATE_CHECK_MS)
                    .apply();
            recreate();
        });
        LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rp.topMargin = Prefs.dp(this, 10);
        root.addView(reset, rp);

        TextView footer = text("Magic Swipe · MIT", 12, COLOR_MUTED, false);
        footer.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams fp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        fp.topMargin = Prefs.dp(this, 14);
        fp.bottomMargin = Prefs.dp(this, 12);
        root.addView(footer, fp);
    }

    private LinearLayout card() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        int p = Prefs.dp(this, 16);
        card.setPadding(p, p, p, p);
        card.setBackground(rounded(COLOR_CARD, 18, COLOR_BORDER, 1));
        return card;
    }

    private void addCard(LinearLayout root, LinearLayout card) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.bottomMargin = Prefs.dp(this, 12);
        root.addView(card, p);
    }

    private void addCardTitle(LinearLayout card, String title) {
        TextView v = text(title, 18, COLOR_TEXT, true);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.bottomMargin = Prefs.dp(this, 10);
        card.addView(v, p);
    }

    private void addDivider(LinearLayout card) {
        View divider = new View(this);
        divider.setBackgroundColor(COLOR_BORDER);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, Prefs.dp(this, 1));
        p.topMargin = Prefs.dp(this, 10);
        p.bottomMargin = Prefs.dp(this, 12);
        card.addView(divider, p);
    }

    private TextView text(String value, float sizeSp, int color, boolean bold) {
        TextView v = new TextView(this);
        v.setText(value);
        v.setTextSize(sizeSp);
        v.setTextColor(color);
        if (bold) v.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return v;
    }

    private Button primaryButton(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(14);
        b.setTextColor(COLOR_ON_ACCENT);
        b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setAllCaps(false);
        b.setMinHeight(Prefs.dp(this, 48));
        b.setBackground(rounded(COLOR_CYAN, 14, COLOR_CYAN, 0));
        return b;
    }

    private Button secondaryButton(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(14);
        b.setTextColor(COLOR_TEXT);
        b.setAllCaps(false);
        b.setMinHeight(Prefs.dp(this, 48));
        b.setBackground(rounded(COLOR_CARD_ALT, 14, COLOR_BORDER, 1));
        return b;
    }

    private GradientDrawable rounded(int fill, int radiusDp, int strokeColor, int strokeDp) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(fill);
        g.setCornerRadius(Prefs.dp(this, radiusDp));
        if (strokeDp > 0) g.setStroke(Prefs.dp(this, strokeDp), strokeColor);
        return g;
    }

    private ColorStateList accentStates(int checkedColor, int uncheckedColor) {
        return new ColorStateList(
                new int[][] {
                        new int[] { android.R.attr.state_checked },
                        new int[] {}
                },
                new int[] { checkedColor, uncheckedColor });
    }

    private int inactiveThumbColor() {
        return darkMode ? Color.rgb(110, 124, 145) : Color.rgb(148, 163, 184);
    }

    private int activeTrackColor() {
        return darkMode ? Color.rgb(20, 104, 126) : Color.rgb(103, 232, 249);
    }

    private int inactiveTrackColor() {
        return darkMode ? Color.rgb(55, 69, 88) : Color.rgb(203, 213, 225);
    }

    private int seekBackgroundColor() {
        return darkMode ? Color.rgb(58, 72, 91) : Color.rgb(203, 213, 225);
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

    private void addSwitch(LinearLayout root, String label, String key, boolean def) {
        Switch s = new Switch(this);
        s.setText(label);
        s.setTextSize(16);
        s.setTextColor(COLOR_TEXT);
        s.setChecked(prefs.getBoolean(key, def));
        s.setPadding(0, Prefs.dp(this, 3), 0, Prefs.dp(this, 3));
        s.setThumbTintList(accentStates(COLOR_CYAN, inactiveThumbColor()));
        s.setTrackTintList(accentStates(activeTrackColor(), inactiveTrackColor()));
        s.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean(key, isChecked).apply());
        root.addView(s);
    }

    private void addCheck(LinearLayout root, String label, String key, boolean def) {
        CheckBox c = new CheckBox(this);
        c.setText(label);
        c.setTextSize(15);
        c.setTextColor(COLOR_TEXT);
        c.setChecked(prefs.getBoolean(key, def));
        c.setPadding(0, Prefs.dp(this, 3), 0, Prefs.dp(this, 3));
        c.setButtonTintList(accentStates(COLOR_CYAN, inactiveThumbColor()));
        c.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean(key, isChecked).apply());
        root.addView(c);
    }

    private void addSeek(LinearLayout root, String label, String suffix,
                         String key, int min, int max, int def) {
        int current = prefs.getInt(key, def);
        TextView value = text(
                getString(R.string.seek_value_format, label, current, suffix),
                14, COLOR_TEXT, false);
        root.addView(value);

        SeekBar seek = new SeekBar(this);
        seek.setMax(max - min);
        seek.setProgress(current - min);
        seek.setProgressTintList(ColorStateList.valueOf(COLOR_CYAN));
        seek.setProgressBackgroundTintList(ColorStateList.valueOf(seekBackgroundColor()));
        seek.setThumbTintList(ColorStateList.valueOf(COLOR_TEXT));
        seek.setPadding(0, 0, 0, Prefs.dp(this, 8));
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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
        serviceStatus.setTextColor(enabled ? COLOR_GREEN : COLOR_RED);
        int px = Prefs.detectNativeBackInsetPx(this);
        nativeInfo.setText(getString(R.string.native_zone_format, px, Prefs.pxToDp(this, px)));
    }

    private boolean isServiceEnabled() {
        AccessibilityManager am = (AccessibilityManager)
                getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabled = am.getEnabledAccessibilityServiceList(
                AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
        for (AccessibilityServiceInfo info : enabled) {
            if (info.getResolveInfo() != null
                    && info.getResolveInfo().serviceInfo != null
                    && getPackageName().equals(info.getResolveInfo().serviceInfo.packageName)
                    && EdgeBackAccessibilityService.class.getName().equals(
                            info.getResolveInfo().serviceInfo.name)) {
                return true;
            }
        }
        return false;
    }
}
