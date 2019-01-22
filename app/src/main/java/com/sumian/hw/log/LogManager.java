package com.sumian.hw.log;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.HandlerThread;
import android.util.Log;
import android.webkit.WebSettings;

import com.sumian.sd.app.App;
import com.sumian.sd.log.SdLogManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogManager {

    private static final String TAG = LogManager.class.getSimpleName();

    private static volatile LogManager INSTANCE;

    static final String LOG_FILE_NAME = "Android_sumian_app.txt";

    private static final int PHONE_INFO_TYPE = 0x01;//系统信息
    private static final int BLUETOOTH_ADAPTER_TYPE = 0x02;//蓝牙
    private static final int USER_OPERATION_TYPE = 0x03;//用户操作
    private static final int MONITOR_TYPE = 0x04;//监测仪
    private static final int SPEED_SLEEPER_TYPE = 0x05;//速眠仪
    private static final int TRANSPARENT_DATA_TYPE = 0x06;//透传数据

    private static final String DELIMITER = " : ";
    private static final String LEFT_BRACKETS = "[";
    private static final String RIGHT_BRACKETS = "]";

    private File mLogFile;

    private ThreadLocal<SimpleDateFormat> mDateFormatThreadLocal = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ", Locale.getDefault());
        }
    };

    private LogManager() {
        HandlerThread handlerThread = new HandlerThread("log thread");
        handlerThread.start();
        this.mLogFile = new File(App.Companion.getAppContext().getCacheDir(), LOG_FILE_NAME);
    }

    private boolean fileIsExists() {
        if (init().mLogFile.exists()) {
            return true;
        } else {
            try {
                return init().mLogFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static LogManager init() {
        if (INSTANCE == null) {
            synchronized (LogManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LogManager();
                }
            }
        }
        return INSTANCE;
    }

    public static void appendPhoneUSerAgentLog() {
        formatLog(PHONE_INFO_TYPE, getUserAgent());
        formatLog(PHONE_INFO_TYPE, getAppInfo());
    }

    public static void appendPhoneLog(String phoneLog) {
        formatLog(PHONE_INFO_TYPE, phoneLog);
    }

    public static void appendFormatPhoneLog(String format, Object... args) {
        String string = String.format(Locale.getDefault(), format, args);
        appendPhoneLog(string);
    }

    public static void appendBluetoothLog(String bluetoothLog) {
        formatLog(BLUETOOTH_ADAPTER_TYPE, bluetoothLog);
    }

    public static void appendUserOperationLog(String userOperation) {
        formatLog(USER_OPERATION_TYPE, userOperation);
    }

    public static void appendSpeedSleeperLog(String speedSleeperLog) {
        formatLog(SPEED_SLEEPER_TYPE, speedSleeperLog);
    }

    public static void appendMonitorLog(String monitorLog) {
        formatLog(MONITOR_TYPE, monitorLog);
    }

    public static void appendTransparentLog(String transparentLog) {
        formatLog(TRANSPARENT_DATA_TYPE, transparentLog);
    }

    private static void formatLog(int deviceType, String log) {
        String formatLog = String.format(Locale.getDefault(), "%s%s%s%s", formatLogType(deviceType), formatDate(), DELIMITER, log);
        appendLog(formatLog);
    }


    private static String formatLogType(int logType) {
        String formatType = null;
        switch (logType) {
            case PHONE_INFO_TYPE:
                formatType = "系统信息";
                break;
            case BLUETOOTH_ADAPTER_TYPE:
                formatType = "  蓝牙  ";
                break;
            case USER_OPERATION_TYPE:
                formatType = "用户操作";
                break;
            case MONITOR_TYPE:
                formatType = " 监测仪 ";
                break;
            case SPEED_SLEEPER_TYPE:
                formatType = " 速眠仪 ";
                break;
            case TRANSPARENT_DATA_TYPE:
                formatType = "透传数据";
                break;
        }
        return String.format(Locale.getDefault(), "%s%s%s", LEFT_BRACKETS, formatType, RIGHT_BRACKETS);
    }

    private static String formatDate() {
        return String.format(Locale.getDefault(), "%s%s%s", LEFT_BRACKETS, init().mDateFormatThreadLocal.get().format(new Date()), RIGHT_BRACKETS);
    }

    private static void appendLog(String log) {
        Log.d("LogManager", log);
        SdLogManager.INSTANCE.logDevice(log);
    }

    private static String getUserAgent() {
        String userAgent;
        try {
            userAgent = WebSettings.getDefaultUserAgent(App.Companion.getAppContext());
        } catch (Exception e) {
            userAgent = System.getProperty("http.agent");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }

        // Log.e(TAG, "getUserAgent: ------>" + sb.toString());
        return sb.toString();
    }

    private static String getAppInfo() {
        String appInfo = null;

        PackageManager packageManager = App.Companion.getAppContext().getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(App.Companion.getAppContext().getPackageName(), 0);
            String versionName = packageInfo.versionName;
            int versionCode = packageInfo.versionCode;

            appInfo = "versionName=" + versionName + "  versionCode=" + versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return appInfo;
    }
}
