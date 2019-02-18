package com.sumian.sd.buz.devicemanager;

import android.os.Environment;
import android.util.Log;

import com.sumian.sd.common.utils.StreamUtil;
import com.sumian.sd.common.utils.TimeUtilHw;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by jzz
 * on 2017/10/9
 * <p>
 * desc:
 */

@SuppressWarnings("ALL")
public final class FileHelper {

    public static final String TAG = FileHelper.class.getSimpleName();
    public static final String FILE_DIR = "sumian";
    private static final String EMG_FILE_NAME = "emg";
    private static final String PULSE_FILE_NAME = "pulse";
    private static final String SPEED_FILE_NAME = "speed";
    private static final String FILE_CONNECTOR = "";
    private static final String FILE_SUFFIX = "log";
    private static volatile FileHelper INSTANCE;

    private long mUnixTime;

    private volatile File mDirFile;

    private File mEmgFile;
    private File mPulseFile;
    private File mSpeedFile;

    private FileHelper() {
        //得到当前外部存储设备的目录
        //File.separator为文件分隔符”/“,方便之后在目录下创建文件
    }

    public static FileHelper init() {
        if (INSTANCE == null) {
            synchronized (FileHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FileHelper();
                }
            }
        }
        return INSTANCE;
    }

    private static String formatFileName(String fileName) {
        File dirFile = INSTANCE.mDirFile;
        String[] files = dirFile.list();

        if (files != null)
            //存在文件,直接返回该文件的名字
            for (String s : files) {
                if (s.startsWith(fileName)) {
                    return s;
                }
            }

        //初始化新文件名
        return String.format("%s%s%s%s", fileName, TimeUtilHw.formatDate2FileName(INSTANCE.mUnixTime), FILE_CONNECTOR, FILE_SUFFIX);
    }

    public static File createSleepFile(String fileName) {
        return createFileInSDCard(fileName);
    }

    public static File createEmgFile() {
        return INSTANCE.mEmgFile = createFileInSDCard(formatFileName(EMG_FILE_NAME));
    }

    public static File createPulseFile() {
        return INSTANCE.mPulseFile = createFileInSDCard(formatFileName(PULSE_FILE_NAME));
    }

    public static File createSpeedFile() {
        return INSTANCE.mSpeedFile = createFileInSDCard(formatFileName(SPEED_FILE_NAME));
    }

    public static File getEmgFile() {
        return INSTANCE.mEmgFile;
    }

    public static File getPulseFile() {
        return INSTANCE.mPulseFile;
    }

    public static File getSpeedFile() {
        return INSTANCE.mSpeedFile;
    }

    //在SD卡上创建文件
    public static File createFileInSDCard(String fileName) {
        File file = new File(INSTANCE.mDirFile, fileName);

        if (!file.exists()) {
            try {
                file.createNewFile();
                Log.e(TAG, "createFileInSDCard: ------新创建一个文件---->" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //  Log.e(TAG, "createFileInSDCard: ----------->" + file.getPath());
        return file;
    }

    public static File createSDDir(String dir) {
        File downloadFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        if (!downloadFile.exists()) {
            boolean mkdir = downloadFile.mkdir();
            // Log.e(TAG, "createSDDir: ----1-->");
        }

        File dirFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), dir);
        if (!dirFile.exists()) {
            boolean mkdir = dirFile.mkdir();
            //  Log.e(TAG, "createSDDir: -----2----->");
        }

        // Log.e(TAG, "createSDDir: -----3--->" + dirFile.getPath());
        INSTANCE.mDirFile = dirFile;
        return dirFile;
    }

    public static void appendEmgContent(String content) {
        appendContent(INSTANCE.mEmgFile, content);
    }

    public static void appendPulseContent(String content) {
        appendContent(INSTANCE.mPulseFile, content);
    }

    public static void appendSpeedContent(String content) {
        appendContent(INSTANCE.mSpeedFile, content);
    }

    /**
     * 追加文件：使用RandomAccessFile
     *
     * @param file    文件
     * @param content 追加的内容
     */
    public static void appendContent(File file, String content) {
        RandomAccessFile randomFile = null;
        try {
            // 打开一个随机访问文件流，按读写方式
            randomFile = new RandomAccessFile(file, "rw");
            // 将写文件指针移到文件尾。
            randomFile.seek(randomFile.length());
            randomFile.writeBytes(content + "\r\n");
            // Log.e(TAG, "appendContent: -------->" + content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StreamUtil.close(randomFile);
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

}
