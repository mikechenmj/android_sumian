package com.sumian.sleepdoctor.utils;

import android.content.Context;
import android.support.annotation.StringRes;

import com.sumian.sleepdoctor.app.App;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/7 13:55
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ResourceUtil {
    public static Context getContext() {
        return App.Companion.getAppContext();
    }

    public static String getString(@StringRes int stringId) {
        return getContext().getResources().getString(stringId);
    }

}
