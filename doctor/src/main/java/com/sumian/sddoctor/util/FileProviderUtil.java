package com.sumian.sddoctor.util;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.sumian.sddoctor.BuildConfig;

import java.io.File;

import androidx.core.content.FileProvider;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/29 11:33
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class FileProviderUtil {

    public static Uri getCompatUriForFile(Context context, File file) {
        insertMediaStore(context, file);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return Uri.fromFile(file);
        } else {
            return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
        }
    }

    /**
     * 其实可以直接使用该 insert 操作返回的 uri 去提供给外部多媒体文件路径.  而不需要对 sdk.version.Code>=24 做判断进行区别对待
     * <p>
     * 可以这么理解默认系统底层 contentResolver 已经对 uri 进行了转换
     *
     * @param context context
     * @param file    file
     */
    private static void insertMediaStore(Context context, File file) {
        ContentValues contentValues = new ContentValues(1);
        contentValues.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
    }
}