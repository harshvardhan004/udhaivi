package com.udhaivi.udhaivihealthcare;

import android.app.Application;

import com.udhaivi.udhaivihealthcare.Util.SharedPreferenceUtils;
import com.udhaivi.udhaivihealthcare.ble.BleManager;
import com.udhaivi.udhaivihealthcare.daomananger.DbManager;

public class Myapp extends Application {
    private static Myapp instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        DbManager.init(this);
        SharedPreferenceUtils.init(this);
        BleManager.init(this);
    }

    public static Myapp getInstance() {
        return instance;
    }
}
