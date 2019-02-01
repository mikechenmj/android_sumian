package com.sumian.sddoctor.service.publish.utils;


import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by sm
 * on 2018/2/1.
 * desc:文件路径的获取和拼接
 */
public class FilePathUtil {

    /**
     * 生成存储文件的路径，如果有sd卡则获取sd卡路径，否则获取应用缓存区路径。
     *
     * @param context    应用Context
     * @param folderPath 文件夹路径
     * @param fileName   文件名
     * @return 生成的文件路径
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static String makeFilePath(Context context, String folderPath, String fileName) {
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName);
        if (file.exists()) {
            return file.getAbsolutePath();
        } else {
            try {
                boolean newFile = file.createNewFile();
                if (newFile) {
                    return file.getAbsolutePath();
                } else {
                    File cacheFile = new File(context.getCacheDir(), fileName);
                    if (cacheFile.exists()) {
                        return cacheFile.getAbsolutePath();
                    } else {
                        if (cacheFile.createNewFile()) {
                            return cacheFile.getAbsolutePath();
                        } else {
                            throw new NullPointerException("创建文件失败");
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        throw new NullPointerException("创建文件失败");
    }


    /**
     * 得到文件夹目录
     *
     * @param context    context
     * @param folderPath folderPath
     * @return folderDir
     */
    @SuppressWarnings("unused")
    public static String getFolderDir(Context context, String folderPath) {
        File file;
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            file = new File(android.os.Environment.getExternalStorageDirectory(),
                    folderPath);
        } else {
            file = context.getApplicationContext().getCacheDir();
        }
        if (!file.exists() || !file.isDirectory()) {
            boolean mkdirs = file.mkdirs();
        }

        return file.getPath();
    }

    /**
     * 清空某一路径下的文件
     *
     * @param context  context
     * @param filePath filePath
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void clearFilePath(Context context, File filePath) {
        if (!filePath.exists()) {
            return;
        }
        if (filePath.isFile()) {
            filePath.delete();
            return;
        }
        if (filePath.isDirectory()) {
            File[] folders = filePath.listFiles();
            for (File folder : folders) {
                clearFilePath(context, folder);
            }
        }
    }

}

