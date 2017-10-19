
package com.thundersoft.playmedia;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

public class RecorderMicHelper {
    private static final String TAG = "RecorderMicHelper";
    private static final int DELAY = 5200;
    
    public static final int NO_ERROR = 0;
    public static final int TYPE_ERROR_FILE_INIT = 1;

    private static final int MSG_START = 1;
    private static final int MSG_STOP = 2;
    
    enum State {
        IDLE,
        WAITING,
        ACTIVE
    }
    
    private static RecorderMicHelper sInstance;
    private Context mContext;
    private File mRecAudioFile;
    private File mRecAudioPath;
    private MediaRecorder mMediaRecorder;
    private State mState = State.IDLE;

    private RecorderMicHelper(Context context) {
        mContext = context;
    }

    public static RecorderMicHelper get(Context context) {
        if (sInstance == null) {
            sInstance = new RecorderMicHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public void start(String micType){
        synchronized (this) {
            if("mmitest=none".equals(micType)
                    || "mmitest=mic1".equals(micType)
                    || "mmitest=mic2".equals(micType)){
                mHandler.removeMessages(0);
                stop();
                if(mState == State.IDLE){
                    startRecord(micType);
                    mHandler.sendEmptyMessageDelayed(0, DELAY);
                }
                return;
            }
            throw new IllegalArgumentException(micType);            
        }
    }

    public void stop() {
        synchronized (this) {
            if (mState == State.ACTIVE) {
                stopRecord();
            }
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            stop();
            Process.killProcess(Process.myPid());
        };
    };

    private int initRecordFile(String mic){
        mRecAudioPath = mContext.getFilesDir();
        String filenameTwo = "mictwo.wav";
        if ("mmitest=mic1".equals(mic)) {
            filenameTwo = "micone.wav";
        } else if ("mmitest=mic2".equals(mic)) {
            filenameTwo = "mictwo.wav";
        } else {
            return TYPE_ERROR_FILE_INIT;
        }
        File baseTwo = new File(mRecAudioPath.toString());
        if(!baseTwo.isDirectory()&& !baseTwo.mkdir()){
             Log.e(TAG, "Recording File aborted - can't create baseTwo directory : " + baseTwo.getPath());
        }
        File outFileTwo = new File(mRecAudioPath.toString()+ "/" + filenameTwo);
        Log.e(TAG,"baseTwo :"+baseTwo+", outFile :"+outFileTwo );
        try{
            if(outFileTwo.exists()){
                outFileTwo.delete();
            }
            boolean bRet = outFileTwo.createNewFile();
            if(!bRet){
                Log.e(TAG,"getRecordFile,fn:"+ filenameTwo +", failed");
                return TYPE_ERROR_FILE_INIT;
            }

        } catch (IOException e){
            Log.e(TAG,"getRecordFile,fn:"+ filenameTwo +", "+e);
            return TYPE_ERROR_FILE_INIT;
        }
        mRecAudioFile = outFileTwo;
        return NO_ERROR;

    }
    private void startRecord(String mic){
        int errorTwo = initRecordFile(mic);
        if (errorTwo != NO_ERROR){
            Log.e(TAG,"error,can not create a new file to record !");
            return;
        }
        mState = State.WAITING;
        AudioManager am = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        am.setParameters(mic) ;
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.WAVE);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.LPCM);
        Log.d(TAG,"filepath :"+mRecAudioPath+",mRecAudioFile = "+mRecAudioFile);
        mMediaRecorder.setOutputFile(mRecAudioFile.getAbsolutePath());
        try{
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            mState = State.ACTIVE;
        } catch (IOException e){
            AudioManager audioMngr = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
            boolean isInCallTwo = audioMngr.getMode() == AudioManager.MODE_IN_CALL;
            if (isInCallTwo) {
                Log.e(TAG, "RuntimeException when recorder start, cause : incall!");
            } else {
                Log.e(TAG, "RuntimeException when recorder start !");
            }
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            e.printStackTrace();
            mState = State.IDLE;
        }
    }
    private void stopRecord(){
        if(mMediaRecorder == null){
            Log.e(TAG, "error, mRecorder is null !");
            return;
        }
        try {
            mState = State.WAITING;
            mMediaRecorder.stop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mMediaRecorder.release();
            mMediaRecorder = null;
            AudioManager audioMngr = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
            audioMngr.setParameters("mmitest=none");
            mState = State.IDLE;
        }
    }
}
