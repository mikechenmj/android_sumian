package com.sumian.hw.utils;

import android.app.Activity;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.sumian.blue.model.BluePeripheral;
import com.sumian.hw.app.HwAppManager;
import com.sumian.hw.gather.FileHelper;
import com.sumian.hw.log.LogManager;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/2 17:42
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class AppUtil {
    /**
     * exit app
     */
    public static void exitApp(Activity activity) {
        HwAppManager.getJobScheduler().release(activity.getApplicationContext());
        BluePeripheral bluePeripheral = HwAppManager.getBlueManager().getBluePeripheral();
        if (bluePeripheral != null) {
            bluePeripheral.close();
        }
        FileHelper.closeUploadThread();
        LogManager.appendUserOperationLog("用户退出 app.......");
        ActivityUtils.finishAllActivities();
        AppUtils.exitApp();
    }
}
