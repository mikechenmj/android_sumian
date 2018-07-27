package com.sumian.app.utils;

import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/28 19:58
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class JsonUtil {
    private static Gson sGson = new Gson();

    public static String toJson(Object o) {
        return sGson.toJson(o);
    }

    @Nullable
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return sGson.fromJson(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param type Such as new TypeToken<Map<String, Integer>>() {}.getType(); new TypeToken<List<String>>(){}.getType();
     */
    @Nullable
    public static <T> T fromJson(String json, Type type) {
        try {
            return sGson.fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
