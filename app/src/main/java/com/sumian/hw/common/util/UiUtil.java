package com.sumian.hw.common.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.ColorRes;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumian.common.helper.ToastHelper;
import com.sumian.sd.BuildConfig;
import com.sumian.sd.R;
import com.sumian.sd.app.App;

import java.util.List;

/**
 * Created by jzz
 * on 2017/10/12.
 * desc:
 */

public final class UiUtil {

    private static long mBackPressedTime;

    public static int getColor(@ColorRes int colorId) {
        return App.Companion.getAppContext().getResources().getColor(colorId);
    }

    public static void updateSleepDuration(Integer second, TextView tvHour, TextView tvHourLabel, TextView tvMin, TextView tvMinLabel) {
        tvHour.setText(NumberUtil.formatHour(second));
        tvMin.setText(NumberUtil.formatMin(second));
        goneLabel(tvHourLabel, tvMinLabel, second == null ? View.GONE : View.VISIBLE);
    }

    public static void updateSleepCompare(Integer second, ImageView ivCompare, TextView tvMin, TextView tvMinLabel) {
        tvMin.setTextColor((second == null) || (second >= 0) ? UiUtil.getColor(R.color.bt_hole_color) : UiUtil.getColor(R.color.warn_color));
        tvMin.setText(NumberUtil.second2Min(second));
        goneCompareLabel(ivCompare, tvMinLabel, second == null ? View.GONE : View.VISIBLE);
        ivCompare.setImageResource(second == null || second < 0 ? R.mipmap.ic_report_down : R.mipmap.ic_report_up);
        tvMinLabel.setTextColor(second == null || second >= 0 ? UiUtil.getColor(R.color.bt_hole_color) : UiUtil.getColor(R.color.warn_color));
    }

    private static void goneLabel(TextView tvHourLabel, TextView tvMinLabel, int gone) {
        tvHourLabel.setVisibility(gone);
        tvMinLabel.setVisibility(gone);
    }

    private static void goneCompareLabel(ImageView ivCompare, TextView tvMinLabel, int gone) {
        ivCompare.setVisibility(gone);
        tvMinLabel.setVisibility(gone);
    }

    public static Typeface getTypeface() {
        return Typeface.createFromAsset(App.Companion.getAppContext().getResources().getAssets(), "dincond_medium.otf");
    }

    public static void closeKeyboard(EditText view) {
        view.clearFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) return;
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    public static void openKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) return;
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }


    public static void showSoftKeyboard(View view) {
        if (view == null) return;
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        if (!view.isFocused()) view.requestFocus();

        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager == null) return;
        inputMethodManager.showSoftInput(view, 0);
    }

    /**
     * updateDialogType pwd /hide pwd
     *
     * @param ivShowPwd isShowPwd
     * @param etPwd     etPwd
     */
    public static void notifyInputType(ImageView ivShowPwd, EditText etPwd) {
        Object tag = ivShowPwd.getTag();
        if (tag != null) {
            //hide
            ivShowPwd.setTag(null);
            ivShowPwd.setImageResource(R.mipmap.ic_eye_hide);
            etPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            //FoldAnimation
            ivShowPwd.setTag(true);
            ivShowPwd.setImageResource(R.mipmap.ic_eye_dispaly);
            etPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            etPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        etPwd.setSelection(etPwd.getText().length());
    }

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
            ToastHelper.show(R.string.none_market);
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
                context.startActivity(intent);
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
