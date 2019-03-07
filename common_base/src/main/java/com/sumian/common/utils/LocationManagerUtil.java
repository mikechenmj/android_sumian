package com.sumian.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import androidx.fragment.app.Fragment;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/11 10:09
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class LocationManagerUtil {

    public static boolean isLocationProviderEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null
            && (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    public static void startLocationSettingActivityForResult(Activity activity, int requestCode) {
        Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        activity.startActivityForResult(callGPSSettingIntent, requestCode);
    }

    public static void startLocationSettingActivityForResult(Fragment fragment, int requestCode) {
        Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        fragment.startActivityForResult(callGPSSettingIntent, requestCode);
    }
}
