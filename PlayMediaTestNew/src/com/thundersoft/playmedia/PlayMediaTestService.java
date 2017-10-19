package com.thundersoft.playmedia;


import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;
import android.os.Process;

public class PlayMediaTestService extends Service {

    private MediaPlayer mPlayer;
    
    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        playSound();
        return super.onStartCommand(intent, flags, startId);
    }
    private void playSound(){
        try {
            stopSound();
            mPlayer = MediaPlayer.create(getBaseContext(), R.raw.sound);
            mPlayer.setOnCompletionListener(mListener);
            mPlayer.setLooping(false);
            mPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopSound(){
        if(mPlayer != null){
            mPlayer.stop();
            mPlayer.reset();
            mPlayer.release();
            mPlayer.setOnCompletionListener(null);
            mPlayer = null;
        }
    }

    private OnCompletionListener mListener = new OnCompletionListener(){
        @Override
        public void onCompletion(MediaPlayer mp) {
            stopSound();
            stopSelf();
            Process.killProcess(Process.myPid());
        }
    };
}
