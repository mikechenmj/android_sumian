package com.sumian.device.util;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by jzz
 * on 2017/10/19.
 * desc:
 */

public class StreamUtil {

    public static final String TAG = StreamUtil.class.getSimpleName();


    public static void close(Closeable... closeables) {
        if (closeables == null) return;
        try {
            for (Closeable closeable : closeables) {
                if (closeable == null) continue;
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
//
//    @SuppressWarnings("UnusedAssignment")
//    public static String getJson(String fileName) {
//        String json = null;
//        BufferedReader bis = null;
//        try {
//            //获取assets资源管理器
//            AssetManager assetManager = App.Companion.getAppContext().getAssets();
//            //通过管理器打开文件并读取
//            bis = new BufferedReader(new InputStreamReader(assetManager.open(fileName, AssetManager.ACCESS_BUFFER), "utf-8"));
//
//            char[] bytes = new char[1024];
//            int len = -1;
//
//            StringBuilder sb = new StringBuilder();
//            while ((len = bis.read(bytes)) != -1) {
//                sb.append(bytes, 0, len);
//            }
//            json = sb.toString();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            close(bis);
//        }
//
//        return json;
//    }

    /**
     * 拷贝文件
     * 如果目标文件不存在将会自动创建
     *
     * @param srcFile  原文件
     * @param saveFile 目标文件
     * @return 是否拷贝成功
     */
    public static boolean copyFile(final File srcFile, final File saveFile) {
        File parentFile = saveFile.getParentFile();
        if (!parentFile.exists()) {
            if (!parentFile.mkdirs())
                return false;
        }

        BufferedInputStream inputStream = null;
        BufferedOutputStream outputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(srcFile));
            outputStream = new BufferedOutputStream(new FileOutputStream(saveFile));
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            close(outputStream, inputStream);
        }
        return true;
    }
}
