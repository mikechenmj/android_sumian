package com.sumian.sd.common.utils;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/4/24 15:40
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class StorageUtil {

    private static boolean isExternalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(
            Environment.MEDIA_MOUNTED);
    }

    public static long getAvailableExternalStorageSize() {
        if (!isExternalMemoryAvailable()) {
            return 0L;
        }
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(externalStorageDirectory.getPath());
        return statFs.getAvailableBytes();
    }

}
