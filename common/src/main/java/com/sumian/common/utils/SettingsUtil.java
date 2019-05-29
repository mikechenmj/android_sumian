package com.sumian.common.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.fragment.app.Fragment;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/6 22:05
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class SettingsUtil {

    public static void launchSettingActivityForResult(Activity activity, int requestCode) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, requestCode);
    }

    @SuppressWarnings("ConstantConditions")
    public static void launchSettingActivityForResult(Fragment fragment, int requestCode) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", fragment.getActivity().getPackageName(), null);
        intent.setData(uri);
        fragment.startActivityForResult(intent, requestCode);
    }
}
