package com.sumian.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import androidx.fragment.app.Fragment;

import com.sumian.common.R;
import com.sumian.common.widget.dialog.SumianDialog;

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

    public static boolean checkLocationService(Activity context, int code) {
        boolean locationProviderEnable = LocationManagerUtil.isLocationProviderEnable(context);
        if (locationProviderEnable) {
            return true;
        }
        new SumianDialog(context)
                .setTitleText(R.string.open_location_service_dialog_title)
                .setMessageText(R.string.open_location_service_for_blue_scan_hint)
                .setRightBtn(R.string.confirm, v -> LocationManagerUtil.startLocationSettingActivityForResult(context, code), true)
                .setCanceledOnTouchOutsideWrap(false)
                .show();
        return false;
    }

    public static boolean checkLocationService(Fragment context, int code) {
        boolean locationProviderEnable = LocationManagerUtil.isLocationProviderEnable(context.getActivity());
        if (locationProviderEnable) {
            return true;
        }
        new SumianDialog(context.getActivity())
                .setTitleText(R.string.open_location_service_dialog_title)
                .setMessageText(R.string.open_location_service_for_blue_scan_hint)
                .setRightBtn(R.string.confirm, v -> LocationManagerUtil.startLocationSettingActivityForResult(context, code), true)
                .setCanceledOnTouchOutsideWrap(false)
                .show();
        return false;
    }
}
