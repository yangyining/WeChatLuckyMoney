package xyz.monkeytong.hongbao;


import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by JaneLuo on 2016/2/2 0002.
 */
public class HongbaoApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "900019440", false);
    }

}
