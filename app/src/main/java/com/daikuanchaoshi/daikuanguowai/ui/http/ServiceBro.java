package com.daikuanchaoshi.daikuanguowai.ui.http;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.OnDeviceIdsRead;
import com.lykj.aextreme.afinal.utils.Debug;

import androidx.annotation.Nullable;

public class ServiceBro extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        Adjust.getGoogleAdId(this, new OnDeviceIdsRead() {
            @Override
            public void onGoogleAdIdRead(String googleAdId) {
                Debug.e("---------------googleAdId==="+googleAdId);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
