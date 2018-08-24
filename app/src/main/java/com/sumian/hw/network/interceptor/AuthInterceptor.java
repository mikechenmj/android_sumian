package com.sumian.hw.network.interceptor;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.webkit.WebSettings;

import com.sumian.blue.model.BluePeripheral;
import com.sumian.hw.common.util.SystemUtil;
import com.sumian.hw.device.bean.BlueDevice;
import com.sumian.sd.app.App;
import com.sumian.sd.app.AppManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jzz
 * on 2017/9/26
 * <p>
 * desc:
 */

public class AuthInterceptor implements Interceptor {

    private static final String TAG = AuthInterceptor.class.getSimpleName();

    private static final String WITH_SIGN = "&";

    public static AuthInterceptor create() {
        return new AuthInterceptor();
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {

        Request request = chain
            .request()
            .newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("UserInfo-Agent", getUserAgent())
            .addHeader("Accept-Language", SystemUtil.getSystemLanguage())
            .addHeader("Authorization", "Bearer " + AppManager.getAccountViewModel().getTokenString())
            .addHeader("Device-Info", Uri.encode(formatDeviceInfo(), "utf-8"))
            .build();

        return chain.proceed(request);
    }

    private String formatDeviceInfo() {
        //Device-Info": "app_version=速眠-test_1.2.3.1&model=iPhone10,3&system=iOS_11.3.1&monitor_fw=&monitor_sn=&sleeper_fw=&sleeper_sn="
        return "app_version=" + SystemUtil.getPackageInfo(App.Companion.getAppContext()).versionName
            + WITH_SIGN + "model=" + SystemUtil.getDeviceBrand() + " " + SystemUtil.getSystemModel()
            + WITH_SIGN + "system=" + SystemUtil.getSystemVersion()
            + WITH_SIGN + "monitor_fw=" + formatMonitorInfo(AppManager.getDeviceModel().getMonitorVersion())
            + WITH_SIGN + "monitor_sn=" + formatMonitorInfo(AppManager.getDeviceModel().getMonitorSn())
            + WITH_SIGN + "sleeper_fw=" + formatSpeedSleeperInfo(AppManager.getDeviceModel().getSleepyVersion())
            + WITH_SIGN + "sleeper_sn=" + formatSpeedSleeperInfo(AppManager.getDeviceModel().getSleepySn());
    }

    private String formatMonitorInfo(String monitorInfo) {
        BluePeripheral bluePeripheral = AppManager.getBlueManager().getBluePeripheral();
        if (bluePeripheral == null || !bluePeripheral.isConnected()) {
            return "";
        }
        return TextUtils.isEmpty(monitorInfo) ? "" : monitorInfo;
    }

    private String formatSpeedSleeperInfo(String speedSleeperInfo) {
        BluePeripheral bluePeripheral = AppManager.getBlueManager().getBluePeripheral();
        if (bluePeripheral == null || !bluePeripheral.isConnected()) {
            return "";
        }
        BlueDevice speedSleeper = AppManager.getDeviceModel().getBlueDevice();
        if (speedSleeper != null && speedSleeper.speedSleeper != null
                && !speedSleeper.speedSleeper.isConnected()) {
            return "";
        } else {
            return TextUtils.isEmpty(speedSleeperInfo) ? "" : speedSleeperInfo;
        }
    }

    private String getUserAgent() {
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

}
