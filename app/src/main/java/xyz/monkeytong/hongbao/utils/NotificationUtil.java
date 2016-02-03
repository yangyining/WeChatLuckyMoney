package xyz.monkeytong.hongbao.utils;

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
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

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

    // 创建一个NotificationManager的引用
    private static NotificationManager notificationManager = null;
    private static NotificationCompat.Builder builder = null;
    private NotificationBroadcastReceiver mReceiver;
    private static Context context = null;

    private NotificationUtil() {
        super();
    }

    public static NotificationUtil getInstance(Context value) {
        if (notificationUtil == null){
            notificationUtil = new NotificationUtil();
            context = value;
            notificationManager = (NotificationManager)context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
            builder = new NotificationCompat.Builder(context);

            builder.setOngoing(true);
            builder.setAutoCancel(false);
            builder.setTicker("正在使用微信红包助手");
            builder.setSmallIcon(R.mipmap.ic_launcher);
        }
        return notificationUtil;
    }

    /**
     * 在状态栏显示通知
     */
    public void showNotification() {
        mReceiver = new NotificationBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_BTN);
        context.registerReceiver(mReceiver, intentFilter);

        notifyNotification();
    }

    private void notifyNotification() {
        Intent mAccessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        PendingIntent intentContent = PendingIntent.getActivity(context, 0, mAccessibleIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContent(getRemoteViews());
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;
        notification.contentIntent = intentContent;

        notificationManager.notify(R.string.nia_id, notification);
    }

    @NonNull
    private RemoteViews getRemoteViews() {
        String btn_text ;
        String view_text;
        if (flag) {
            view_text = "插件监控已启用";
            btn_text = "点击停用";
        } else {
            view_text = "插件监控已停用";
            btn_text = "点击启用";
        }
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.tv_up, "微信红包助手");
        remoteViews.setTextViewText(R.id.tv_down, view_text);
        remoteViews.setTextViewText(R.id.btn_start, btn_text);

        Intent intent = new Intent(ACTION_BTN);
        intent.putExtra(INTENT_NAME, INTENT_BTN_LOGIN);
        PendingIntent intentpi = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn_start, intentpi);
        return remoteViews;
    }

    /**
     * 在状态栏显示通知
     */
    public void cleanNotification() {
        // 创建一个NotificationManager的引用
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(R.string.nia_id);
        if (mReceiver != null) {
            context.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    private void setPluginEnabled(Context context, Intent intent) {

        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = defaultSharedPreferences.edit();
        if (flag) {
            edit.putBoolean("pref_watch_notification", false);
            edit.putBoolean("pref_watch_list", false);
            edit.putBoolean("pref_watch_chat", false);
            edit.putBoolean("pref_watch_self", false);
        } else {
            edit.putBoolean("pref_watch_notification", true);
            edit.putBoolean("pref_watch_list", true);
            edit.putBoolean("pref_watch_chat", true);
            edit.putBoolean("pref_watch_self", true);
        }
        edit.commit();

        flag = !flag;

        // 更新通知栏信息
        notifyNotification();

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
//                        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//                        context.sendBroadcast(it);
                        setPluginEnabled(context, intent);
                        break;
                }
            }
        }
    }
}
