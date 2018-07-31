package com.sumian.hw.common.cache;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.sumian.hw.common.util.SpUtil;
import com.sumian.hw.tab.device.bean.BlueDevice;


/**
 * Created by jzz
 * on 2017/11/22
 * <p>
 * desc:
 */

public final class BluePeripheralCache {

    private static final String TAG = BluePeripheralCache.class.getSimpleName();

    private static final String BLUE_CACHE_NAME = "bluePeripheralCache";
    private static final String KEY_BLUE_PERIPHERAL = "bluePeripheral";

    public static void updateCache(BlueDevice peripheral) {
        saveCache(peripheral);
    }

    private static void saveCache(BlueDevice peripheral) {
        if (peripheral == null) throw new NullPointerException("obj is null...");
        //Log.e(TAG, "saveCache: ------------->" + peripheral.toString());
        String json = JSON.toJSONString(peripheral);
        SharedPreferences.Editor edit = initEdit();
        edit.putString(KEY_BLUE_PERIPHERAL, json);
        apply(edit);
    }

    public static <T> T getCache(Class<T> clx) {
        String cacheJson = initSp().getString(KEY_BLUE_PERIPHERAL, null);
        if (TextUtils.isEmpty(cacheJson)) {
            //  Log.e(TAG, "getCache: ------------->device cache is null");
            return null;
        }
        //Log.e(TAG, "getCache: ------------->" + cacheJson);
        return JSON.parseObject(cacheJson, clx);
    }

    public static void clear() {
        SharedPreferences.Editor edit = initEdit();
        edit.clear();
        apply(edit);
    }


    private static SharedPreferences initSp() {
        return SpUtil.initSp(BLUE_CACHE_NAME);
    }

    private static SharedPreferences.Editor initEdit() {
        return SpUtil.initEdit(BLUE_CACHE_NAME);
    }


    private static void apply(SharedPreferences.Editor editor) {
        editor.apply();
    }
}
