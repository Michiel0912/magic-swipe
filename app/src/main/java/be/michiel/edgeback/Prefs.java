package be.michiel.edgeback;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

final class Prefs {
    static final String FILE = "edge_back_prefs";
    static final String LEFT = "left_enabled";
    static final String RIGHT = "right_enabled";
    static final String TARGET_WIDTH_DP = "target_width_dp";
    static final String TRIGGER_DISTANCE_DP = "trigger_distance_dp";
    static final String TOP_EXCLUDE_DP = "top_exclude_dp";
    static final String BOTTOM_EXCLUDE_DP = "bottom_exclude_dp";
    static final String HAPTIC = "haptic";
    static final String DEBUG = "debug_zones";
    static final String AUTO_UPDATE_CHECK = "auto_update_check";
    static final String LAST_UPDATE_CHECK_MS = "last_update_check_ms";
    private static final String PREFS_MIGRATION_VERSION = "prefs_migration_version";
    private static final int CURRENT_PREFS_MIGRATION_VERSION = 1;
    private static final int LEGACY_DEFAULT_TOP_EXCLUDE_DP = 28;

    static final boolean DEFAULT_LEFT = true;
    static final boolean DEFAULT_RIGHT = true;
    static final int DEFAULT_TARGET_WIDTH_DP = 24;
    static final int DEFAULT_TRIGGER_DISTANCE_DP = 26;
    static final int DEFAULT_TOP_EXCLUDE_DP = 80;
    static final int DEFAULT_BOTTOM_EXCLUDE_DP = 88;
    static final boolean DEFAULT_HAPTIC = true;
    static final boolean DEFAULT_DEBUG = false;
    static final boolean DEFAULT_AUTO_UPDATE_CHECK = true;

    private Prefs() {}

    static SharedPreferences get(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(FILE, Context.MODE_PRIVATE);
        migrateIfNeeded(prefs);
        return prefs;
    }

    private static void migrateIfNeeded(SharedPreferences prefs) {
        int version = prefs.getInt(PREFS_MIGRATION_VERSION, 0);
        if (version >= CURRENT_PREFS_MIGRATION_VERSION) return;

        SharedPreferences.Editor editor = prefs.edit();

        // v0.3.0 used a 28dp default top exclusion, which can overlap toolbar actions
        // near the upper screen corners. Preserve custom values, but migrate that legacy
        // default to the validated 80dp safe area used by v0.3.1.
        int top = prefs.getInt(TOP_EXCLUDE_DP, LEGACY_DEFAULT_TOP_EXCLUDE_DP);
        if (!prefs.contains(TOP_EXCLUDE_DP) || top == LEGACY_DEFAULT_TOP_EXCLUDE_DP) {
            editor.putInt(TOP_EXCLUDE_DP, DEFAULT_TOP_EXCLUDE_DP);
        }

        editor.putInt(PREFS_MIGRATION_VERSION, CURRENT_PREFS_MIGRATION_VERSION).apply();
    }

    static int dp(Context context, float dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    static int detectNativeBackInsetPx(Context context) {
        try {
            Resources system = Resources.getSystem();
            int id = system.getIdentifier("gesture_nav_back_window_width", "dimen", "android");
            if (id != 0) {
                int value = system.getDimensionPixelSize(id);
                if (value > 0) return value;
            }
        } catch (Throwable ignored) {
        }
        return dp(context, 15);
    }

    static float pxToDp(Context context, int px) {
        return px / context.getResources().getDisplayMetrics().density;
    }
}
