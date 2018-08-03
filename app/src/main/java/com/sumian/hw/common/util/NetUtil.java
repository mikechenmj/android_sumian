package com.sumian.hw.common.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sumian.sleepdoctor.app.HwApp;

/**
 * Created by jzz
 * on 2017/09/30.
 * <p>
 * desc:
 */

public final class NetUtil {

    /**
     * has internet
     *
     * @return has internet
     */
    public static boolean hasInternet() {
        ConnectivityManager cm = (ConnectivityManager) HwApp.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isAvailable() && info.isConnected();
    }
}
