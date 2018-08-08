package com.sumian.hw.utils;

import android.app.Activity;

import com.blankj.utilcode.util.ActivityUtils;
import com.hyphenate.chat.ChatClient;
import com.sumian.blue.manager.BlueManager;
import com.sumian.blue.model.BluePeripheral;
import com.sumian.common.operator.AppOperator;
import com.sumian.hw.account.activity.HwLoginActivity;
import com.sumian.hw.account.cache.HwAccountCache;
import com.sumian.hw.common.cache.BluePeripheralCache;
import com.sumian.hw.common.config.SumianConfig;
import com.sumian.hw.gather.FileHelper;
import com.sumian.hw.log.LogManager;
import com.sumian.hw.reminder.ReminderManager;
import com.sumian.sleepdoctor.app.App;
import com.sumian.sleepdoctor.app.AppManager;
import com.sumian.sleepdoctor.setting.SettingActivity;

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
        AppManager.getJobScheduler().release(activity.getApplicationContext());
        BluePeripheral bluePeripheral = AppManager.getBlueManager().getBluePeripheral();
        if (bluePeripheral != null) {
            bluePeripheral.close();
        }
        FileHelper.closeUploadThread();
        LogManager.appendUserOperationLog("用户退出 app.......");
        ActivityUtils.finishAllActivities();
    }

    public static void logout(){
        AppManager.getOpenAnalytics().onProfileSignOff();
        AppOperator.runOnThread(() -> {
            ReminderManager.updateReminder(null);
            HwAccountCache.clearCache();
            SumianConfig.clear();
            BluePeripheralCache.clear();
            BlueManager.init().doStopScan();
        });
        ChatClient.getInstance().logout(true, null);
        NotificationUtil.Companion.cancelAllNotification(App.Companion.getAppContext());
        AppManager.getAccountViewModel().updateToken(null);
        AppManager.getOpenLogin().deleteWechatTokenCache(ActivityUtils.getTopActivity(), null);
        ActivityUtils.finishAllActivities();
        ActivityUtils.startActivity(HwLoginActivity.class);
    }
}
