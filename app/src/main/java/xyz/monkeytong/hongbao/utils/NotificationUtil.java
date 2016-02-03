package xyz.monkeytong.hongbao.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import xyz.monkeytong.hongbao.R;

/**
 * Created by JaneLuo on 2016/1/31 0031.
 */
public class NotificationUtil {
    private static NotificationUtil notificationUtil = null;

    public final static String ACTION_BTN = "com.example.notification.btn.login";
    public final static String INTENT_NAME = "btnid";
    public final static int INTENT_BTN_LOGIN = 1;
    public boolean flag = true;

    NotificationBroadcastReceiver mReceiver;

    static {
        notificationUtil = new NotificationUtil();
    }

    private NotificationUtil() {
        super();
    }

    public static NotificationUtil getInstance() {
        return notificationUtil;
    }

    /**
     * 在状态栏显示通知
     */
    public void showNotification(Context context) {

        mReceiver = new NotificationBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_BTN);
        context.registerReceiver(mReceiver, intentFilter);

        // 创建一个NotificationManager的引用
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.tv_up, "微信同步助手");
        remoteViews.setTextViewText(R.id.tv_down, "点击按钮可启用/停用插件监控");

        Intent intent = new Intent(ACTION_BTN);
        intent.putExtra(INTENT_NAME, INTENT_BTN_LOGIN);
        PendingIntent intentpi = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn_start, intentpi);

        Intent mAccessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        PendingIntent intentContent = PendingIntent.getActivity(context, 0, mAccessibleIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setOngoing(false);
        builder.setAutoCancel(false);
        builder.setContent(remoteViews);
        builder.setTicker("正在使用微信同步助手");
        builder.setSmallIcon(R.mipmap.ic_launcher);

        Notification notification = builder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;
        notification.contentIntent = intentContent;

        notificationManager.notify(R.string.nia_id, notification);
    }

    /**
     * 在状态栏显示通知
     */
    public void cleanNotification(Context context) {

        // 创建一个NotificationManager的引用
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(R.string.nia_id);
        if (mReceiver != null) {
            context.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    private void setPluginEnabled(Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = defaultSharedPreferences.edit();
        if (flag) {
            edit.putBoolean("pref_watch_notification", false);
            edit.putBoolean("pref_watch_list", false);
            edit.putBoolean("pref_watch_chat", false);
            edit.putBoolean("pref_watch_self", false);
            Toast.makeText(context, "插件监控已停用", Toast.LENGTH_SHORT).show();
        } else {
            edit.putBoolean("pref_watch_notification", true);
            edit.putBoolean("pref_watch_list", true);
            edit.putBoolean("pref_watch_chat", true);
            edit.putBoolean("pref_watch_self", true);
            Toast.makeText(context, "插件监控已启用", Toast.LENGTH_SHORT).show();
        }
        flag = !flag;
        edit.commit();

    }


    class NotificationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_BTN)) {
                int btn_id = intent.getIntExtra(INTENT_NAME, 0);
                switch (btn_id) {
                    case INTENT_BTN_LOGIN:
                        // 收起通知栏
                        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                        context.sendBroadcast(it);
                        setPluginEnabled(context);
                        break;
                }
            }
        }
    }
}
