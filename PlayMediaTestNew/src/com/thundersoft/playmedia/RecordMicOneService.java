package com.thundersoft.playmedia;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RecordMicOneService extends Service {
    private static final String TAG ="RecordMicOne";

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        RecorderMicHelper.get(getApplicationContext()).stop();
        RecorderMicHelper.get(getApplicationContext()).start("mmitest=mic1");
        return START_NOT_STICKY;
    }
}
