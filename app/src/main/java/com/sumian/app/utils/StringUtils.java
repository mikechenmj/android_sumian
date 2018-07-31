package com.sumian.app.utils;

import android.support.annotation.StringRes;

import com.sumian.app.app.App;

import java.util.Locale;

/**
 * Created by sm
 * on 2018/3/5.
 * desc:
 */

public final class StringUtils {

    public static String format(String format, Object... args) {
        return String.format(Locale.getDefault(), format, args);
    }


    public static String getText(@StringRes int id) {
        return App.getAppContext().getResources().getString(id);
    }
}
