package xyz.monkeytong.hongbao.activities;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;

import java.util.List;

import im.fir.sdk.FIR;
import xyz.monkeytong.hongbao.R;
import xyz.monkeytong.hongbao.utils.ConnectivityUtil;
import xyz.monkeytong.hongbao.utils.NotificationUtil;
import xyz.monkeytong.hongbao.utils.UpdateTask;



public class MainActivity extends Activity {
    private final Intent mAccessibleIntent =
            new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);

    private SharedPreferences sharedPreferences;

    private Button switchPlugin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        FIR.init(this);
        setContentView(R.layout.activity_main);
        switchPlugin = (Button) findViewById(R.id.button_accessible);

        handleMaterialStatusBar();
        updateServiceStatus();

        explicitlyLoadPreferences();

        Boolean changedValue = sharedPreferences.getBoolean("pref_watch_on_lock", false);
        // 显示常驻状态栏通知
        if(changedValue){
            NotificationUtil.showNotification(this);
        }
        // 清楚常驻状态栏通知
        else {
            NotificationUtil.cleanNotification(this);
        }
    }

    private void explicitlyLoadPreferences() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    /**
     * 适配MIUI沉浸状态栏
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void handleMaterialStatusBar() {
        // Not supported in APK level lower than 21
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;

        Window window = this.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(0xffd84e43);

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateServiceStatus();

        // Check for update when WIFI is connected or on first time.
        if (ConnectivityUtil.isWifi(this) || UpdateTask.count == 0) new UpdateTask(this, false).update();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void updateServiceStatus() {
        boolean serviceEnabled = false;

        AccessibilityManager accessibilityManager =
                (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> accessibilityServices =
                accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info.getId().equals(getPackageName() + "/.services.HongbaoService")) {
                serviceEnabled = true;
                break;
            }
        }

        if (serviceEnabled) {
            switchPlugin.setText(R.string.service_off);
        } else {
            switchPlugin.setText(R.string.service_on);
        }
    }

    public void onButtonClicked(View view) {
        startActivity(mAccessibleIntent);
    }

    public void openGithubReleaseNotes(View view) {
        Intent webViewIntent = new Intent(this, WebViewActivity.class);
        webViewIntent.putExtra("title", "发布日志");
        webViewIntent.putExtra("url", "https://github.com/geeeeeeeeek/WeChatLuckyMoney/issues?q=is%3Aissue+is%3Aopen+label%3A%22release+notes%22");
        startActivity(webViewIntent);
    }

    public void openSettings(View view) {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

}
