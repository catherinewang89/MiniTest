package com.thundersoft.playmedia;

import java.io.File;
import java.io.IOException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaRecorder;
//import android.net.sip.SipSession.State;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.util.Log;

public class RecordMicTwoService extends Service {
    private static final String TAG ="RecordMicTwo";

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        RecorderMicHelper.get(getApplicationContext()).stop();
        RecorderMicHelper.get(getApplicationContext()).start("mmitest=mic2");
        return START_NOT_STICKY;
    }
}
