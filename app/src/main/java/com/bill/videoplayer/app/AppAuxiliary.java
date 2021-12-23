package com.bill.videoplayer.app;

import android.app.Application;
import android.content.Context;

/**
 * author ywb
 * date 2021/12/23
 * desc
 */
public class AppAuxiliary {

    private Context mAppContext;

    private static class SingletonHolder {
        private final static AppAuxiliary instance = new AppAuxiliary();
    }

    public static AppAuxiliary getInstance() {
        return SingletonHolder.instance;
    }

    private AppAuxiliary() {
    }

    public void install(Application application) {
        mAppContext = application.getApplicationContext();
    }

    public Context getAppContext() {
        return mAppContext;
    }
}
