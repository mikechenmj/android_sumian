package com.sumian.sleepdoctor.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jzz
 * on 2017/10/18.
 * desc:
 */

public final class SpUtil {

    public static SharedPreferences initSp(Context context, String spFileName) {
        return context.getSharedPreferences(spFileName, Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor initEdit(Context context, String spFileName) {
        return initSp(context, spFileName).edit();
    }

    public static void apply(SharedPreferences.Editor editor) {
        editor.apply();
    }

}
