package com.sumian.sleepdoctor.account.cache;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.sumian.sleepdoctor.app.App;
import com.sumian.sleepdoctor.utils.JsonUtil;
import com.sumian.sleepdoctor.utils.SpUtil;

/**
 * Created by jzz
 * on 2017/9/30
 * <p>
 * desc:
 */

public final class AccountCache {

    private static final String TAG = AccountCache.class.getSimpleName();

    private static final String CACHE_NAME = AccountCache.class.getSimpleName();

    private static final String KEY_USER = "userInfo";
    private static final String KEY_TOKEN = "userToken";

    public static void updateUserCache(Object obj) {
        saveUserCache(obj);
    }

    public static void updateTokenCache(Object obj) {
        saveTokenCache(obj);
    }

    private static void saveUserCache(Object obj) {
        if (obj == null) {
            clearCache();
            return;
        }

        //  PlayLog.e(TAG, "saveUserCache: ------------->" + obj.toString());
        SharedPreferences.Editor edit = initEdit();
        String json = JsonUtil.toJson(obj);
        edit.putString(KEY_USER, json);
        apply(edit);
    }

    private static void saveTokenCache(Object obj) {
        if (obj == null) {
            clearCache();
            return;
        }
        // PlayLog.e(TAG, "saveTokenCache: ------------->" + obj.toString());
        SharedPreferences.Editor edit = initEdit();
        String json = JsonUtil.toJson(obj);
        edit.putString(KEY_TOKEN, json);
        apply(edit);
    }

    public static <T> T getUserCache(Class<T> clx) {
        String cacheJSon = initSp().getString(KEY_USER, null);
        if (TextUtils.isEmpty(cacheJSon)) {
            //  PlayLog.e(TAG, "getCache: ------------->user cache is null");
            return null;
        }
        // PlayLog.e(TAG, "getCache: ------------->" + cacheJSon);
        return JsonUtil.fromJson(cacheJSon, clx);
    }

    public static <T> T getTokenCache(Class<T> clx) {
        String cacheJSon = initSp().getString(KEY_TOKEN, null);
        if (TextUtils.isEmpty(cacheJSon)) {
            // PlayLog.e(TAG, "getCache: ------------->token cache is null");
            return null;
        }
        // PlayLog.e(TAG, "getCache: ------------->" + cacheJSon);
        return JsonUtil.fromJson(cacheJSon, clx);
    }

    public static void clearCache() {
        SharedPreferences.Editor edit = initEdit();
        edit.clear();
        apply(edit);
    }


    private static SharedPreferences initSp() {
        return SpUtil.initSp(App.Companion.getAppContext(), CACHE_NAME);
    }

    private static SharedPreferences.Editor initEdit() {
        return SpUtil.initEdit(App.Companion.getAppContext(), CACHE_NAME);
    }

    private static void apply(SharedPreferences.Editor editor) {
        SpUtil.apply(editor);
    }
}
