package xyz.monkeytong.hongbao.services;

import android.accessibilityservice.AccessibilityService;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by JaneLuo on 2016/2/2 0002.
 */
public class XiuxiuService extends AccessibilityService implements  SharedPreferences.OnSharedPreferenceChangeListener{

    private SharedPreferences sharedPreferences;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        this.watchFlagsFromPreference();
    }

    private void watchFlagsFromPreference() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

//        Boolean watchOnLockFlag = sharedPreferences.getBoolean("pref_watch_on_lock", false);
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}
