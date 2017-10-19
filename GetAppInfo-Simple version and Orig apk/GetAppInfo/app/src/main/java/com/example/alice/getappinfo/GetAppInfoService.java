package com.example.alice.getappinfo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.content.pm.PackageManager.GET_DISABLED_COMPONENTS;
import static android.content.pm.PackageManager.GET_UNINSTALLED_PACKAGES;
import static android.media.CamcorderProfile.get;

public class GetAppInfoService extends Service {
    private static final String TAG = "GetAppInfoService";
    private static final String OUTPUT_PATH = "sdcard/app.csv";
    private static final int MSG_START_GET = 1;
    private HandlerThread handlerThread;
    private GetInfoHandler getHandler;
    private Handler mHandler;
    private Context mContext;

    public GetAppInfoService() {
    }

    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandler = new Handler(getMainLooper());
        start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        stop();
        super.onDestroy();
    }

    private void start() {
        if(handlerThread == null){
            handlerThread = new HandlerThread(TAG);
            handlerThread.start();
            getHandler = new GetInfoHandler(handlerThread.getLooper());
            getHandler.sendEmptyMessage(MSG_START_GET);
        }
    }

    private void stop() {
        if(handlerThread != null) {
            handlerThread.quitSafely();
            handlerThread = null;
            getHandler = null;
        }
    }

    private void showToast(final String str) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),str,1).show();
            }
        });
    }

    class GetInfoHandler extends Handler{
        public GetInfoHandler(Looper looper) {
            super();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START_GET:
                    ArrayList arlist = getAppInfo();
                    if (arlist == null) {
                        showToast("There is no app in the device!");
                    }
                    String str = parseToString(arlist);
                    if(str == null) {
                        showToast("can not get the app info!");
                    } else {
                        showToast("get application info finish!");
                    }
                    GetAppInfoService.this.stopSelf();
                    createCsvFile(OUTPUT_PATH, str);
            }
        }
    }

    private String parseToString(ArrayList arlist) {
        Iterator it = arlist.iterator();
        String string = "";
        for (int i = 0; i < arlist.size(); i++) {
            string += arlist.get(i).toString();
        }
        Log.d(TAG, "parseToString stringxy : " + string);
        return string;
    }

    private ArrayList<AppInfo> getAppInfo() {
        PackageManager pm = getApplication().getPackageManager();
        List<PackageInfo> pkglist = pm.getInstalledPackages(GET_UNINSTALLED_PACKAGES |
                GET_DISABLED_COMPONENTS);
        if (pkglist == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        Iterator pkgIterator = pkglist.iterator();
        for (; ; ) {
            if (!pkgIterator.hasNext()) {
                arrayList.add(0, getTitle());
                return arrayList;
            }
            PackageInfo pkginfo = (PackageInfo) pkgIterator.next();
            String str = pkginfo.packageName;
            if (!str.equals(getApplication().getPackageName())) {
                ApplicationInfo info = pkginfo.applicationInfo;
                AppInfo localAppInfo = new AppInfo();
                localAppInfo.appName = info.loadLabel(pm).toString();
                localAppInfo.apkpath = info.sourceDir + "";
                localAppInfo.pkgName = pkginfo.packageName;
                localAppInfo.verCode = pkginfo.versionCode + "";
                localAppInfo.verName = pkginfo.versionName;
                arrayList.add(localAppInfo);
            }
        }
    }

    private String getFileName(String paramString) {
        String str = "";
        if (TextUtils.isEmpty(paramString)) {
            str = "";
        }
        do {
            str = new File(paramString).getName();
            return str;
        } while (str != null);
    }

    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public static String getSdCardPath() {
        boolean exist = isSdCardExist();
        String sdpath = "";
        if (exist) {
            sdpath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
        } else {
            sdpath = "";
        }
        return sdpath;
    }
    private void createCsvFile(String path, String data) {
        try {
            FileOutputStream fo = new FileOutputStream(path);
            try {
                BufferedWriter bfw = new BufferedWriter(new OutputStreamWriter(fo,"UTF-8"),1024);
                bfw.write(data);
                bfw.flush();
                bfw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException ex) {
            Log.wtf(TAG, ex);
        }
    }

    private AppInfo getTitle() {
        AppInfo appinfo = new AppInfo();
        appinfo.apkpath = "APK_PATH";
        appinfo.appName = "APP_NAME";
        appinfo.pkgName = "PACKAGE_NAME";
        appinfo.verCode = "VERSION_CODE";
        appinfo.verName = "VERSION_NAME";
        return appinfo;
    }
    private static class AppInfo {
            String apkpath;
            String appName ;
            String pkgName ;
            String verCode ;
            String verName ;zzzzzz

        @Override
        public String toString() {
            return appName + "," + apkpath + "," + pkgName + "," + verCode + "," + verName + "\n";
        }
    }
}
