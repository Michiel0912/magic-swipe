package be.michiel.edgeback;

import android.accessibilityservice.AccessibilityService;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

public final class EdgeBackAccessibilityService extends AccessibilityService
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private WindowManager windowManager;
    private SharedPreferences prefs;
    private EdgeTouchView leftView;
    private EdgeTouchView rightView;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        prefs = Prefs.get(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        rebuildOverlays();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Intentionally empty: this service does not read window or screen content.
    }

    @Override
    public void onInterrupt() {
        // No long-running accessibility actions.
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        rebuildOverlays();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        rebuildOverlays();
    }

    @Override
    public void onDestroy() {
        removeOverlays();
        if (prefs != null) prefs.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    private void rebuildOverlays() {
        if (windowManager == null || prefs == null) return;
        removeOverlays();

        int nativeInsetPx = Prefs.detectNativeBackInsetPx(this);
        int targetWidthPx = Prefs.dp(this,
                prefs.getInt(Prefs.TARGET_WIDTH_DP, Prefs.DEFAULT_TARGET_WIDTH_DP));
        int extensionWidthPx = targetWidthPx - nativeInsetPx;
        if (extensionWidthPx <= 0) return;

        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int topPx = Prefs.dp(this, prefs.getInt(Prefs.TOP_EXCLUDE_DP, Prefs.DEFAULT_TOP_EXCLUDE_DP));
        int bottomPx = Prefs.dp(this,
                prefs.getInt(Prefs.BOTTOM_EXCLUDE_DP, Prefs.DEFAULT_BOTTOM_EXCLUDE_DP));
        int activeHeight = Math.max(Prefs.dp(this, 120), screenHeight - topPx - bottomPx);

        if (prefs.getBoolean(Prefs.LEFT, Prefs.DEFAULT_LEFT)) {
            leftView = new EdgeTouchView(true);
            windowManager.addView(leftView,
                    makeLayoutParams(true, nativeInsetPx, extensionWidthPx, topPx, activeHeight));
        }

        if (prefs.getBoolean(Prefs.RIGHT, Prefs.DEFAULT_RIGHT)) {
            rightView = new EdgeTouchView(false);
            windowManager.addView(rightView,
                    makeLayoutParams(false, nativeInsetPx, extensionWidthPx, topPx, activeHeight));
        }
    }

    private WindowManager.LayoutParams makeLayoutParams(boolean left, int nativeInsetPx,
                                                         int widthPx, int topPx, int heightPx) {
        int flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH;

        WindowManager.LayoutParams p = new WindowManager.LayoutParams(
                widthPx,
                heightPx,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                flags,
                PixelFormat.TRANSLUCENT);
        p.gravity = Gravity.TOP | (left ? Gravity.LEFT : Gravity.RIGHT);
        p.x = nativeInsetPx;
        p.y = topPx;
        p.setTitle(left ? "MagicSwipeLeft" : "MagicSwipeRight");
        return p;
    }

    private void removeOverlays() {
        if (windowManager == null) return;
        if (leftView != null) {
            try { windowManager.removeViewImmediate(leftView); } catch (Throwable ignored) {}
            leftView = null;
        }
        if (rightView != null) {
            try { windowManager.removeViewImmediate(rightView); } catch (Throwable ignored) {}
            rightView = null;
        }
    }

    private final class EdgeTouchView extends View {
        private final boolean left;
        private float downRawX;
        private float downRawY;
        private long downTime;
        private boolean triggered;

        EdgeTouchView(boolean left) {
            super(EdgeBackAccessibilityService.this);
            this.left = left;
            setClickable(true);
            refreshAppearance();
        }

        private void refreshAppearance() {
            boolean debug = prefs != null && prefs.getBoolean(Prefs.DEBUG, Prefs.DEFAULT_DEBUG);
            if (!debug) {
                setBackgroundColor(Color.TRANSPARENT);
            } else {
                setBackgroundColor(left ? 0x334CAF50 : 0x332196F3);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            final int action = event.getActionMasked();
            if (action == MotionEvent.ACTION_DOWN) {
                downRawX = event.getRawX();
                downRawY = event.getRawY();
                downTime = SystemClock.uptimeMillis();
                triggered = false;
                return true;
            }

            if (action == MotionEvent.ACTION_MOVE && !triggered) {
                float dx = event.getRawX() - downRawX;
                float dy = event.getRawY() - downRawY;
                float inward = left ? dx : -dx;
                float absY = Math.abs(dy);
                int triggerPx = Prefs.dp(EdgeBackAccessibilityService.this,
                        prefs.getInt(Prefs.TRIGGER_DISTANCE_DP, Prefs.DEFAULT_TRIGGER_DISTANCE_DP));
                long age = SystemClock.uptimeMillis() - downTime;

                // Only a clear, short horizontal inward movement triggers Back.
                if (age <= 900 && inward >= triggerPx && inward > absY * 1.20f) {
                    triggered = true;
                    if (prefs.getBoolean(Prefs.HAPTIC, Prefs.DEFAULT_HAPTIC)) {
                        performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                    }
                    performGlobalAction(GLOBAL_ACTION_BACK);
                }
                return true;
            }

            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                triggered = false;
                return true;
            }
            return true;
        }
    }
}
