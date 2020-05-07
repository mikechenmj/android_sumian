package com.sumian.sd.common.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.Gravity;

import com.sumian.common.helper.ToastHelper;
import com.sumian.sd.BuildConfig;
import com.sumian.sd.R;

import java.util.List;

/**
 * <pre>
 *     @author : sm
 *     e-mail : yaoqi.y@sumian.com
 *     time   : 2018/6/29 13:53
 *
 *     version: 1.0
 *
 *     desc   :
 *
 * </pre>
 */
public final class UiUtils {

    /**
     * 2  * 获取版本号
     * 3  * @return 当前应用的版本号
     * 4
     */
    public static PackageInfo getPackageInfo(Context context) {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info;
    }

    private static void gotoMarket(Context context, String pck) {
        if (!isHaveMarket(context)) {
            ToastHelper.show(context, context.getString(R.string.none_market), Gravity.CENTER);
            // 打开应用商店失败 可能是没有手机没有安装应用市场
            // 调用系统浏览器进入商城
            openLinkBySystem(context, BuildConfig.DEFAULT_APK_DOWNLOAD_URL);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + pck));
        try {
            intent.setClassName("com.tencent.android.qqdownloader", "com.tencent.pangu.link.LinkProxyActivity");
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                ToastHelper.show(context, context.getString(R.string.none_market), Gravity.CENTER);
                openLinkBySystem(context, BuildConfig.DEFAULT_APK_DOWNLOAD_URL);
            }
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            openLinkBySystem(context, BuildConfig.DEFAULT_APK_DOWNLOAD_URL);
        }

    }

    private static boolean isHaveMarket(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_APP_MARKET);
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> intentActivities = pm.queryIntentActivities(intent, 0);
        return intentActivities.size() > 0;
    }

    public static void openAppInMarket(Context context) {
        if (context == null) return;
        String pckName = context.getPackageName();
        gotoMarket(context, pckName);
    }

    /**
     * 调用系统浏览器打开网页
     *
     * @param url 地址
     */
    private static void openLinkBySystem(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


}
