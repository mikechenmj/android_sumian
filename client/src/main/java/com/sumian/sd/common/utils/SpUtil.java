package com.sumian.sd.common.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.sumian.sd.app.App;

/**
 * Created by jzz
 * on 2017/10/18.
 * desc:
 */

public final class SpUtil {

    public static SharedPreferences initSp(String spFileName) {
        return App.Companion.getAppContext().getSharedPreferences(spFileName, Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor initEdit(String spFileName) {
        return initSp(spFileName).edit();
    }


    public static void apply(SharedPreferences.Editor editor) {
        editor.apply();
        //SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }

}
