package com.sumian.sd.utils;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.hyphenate.chat.ChatClient;
import com.sumian.blue.manager.BlueManager;
import com.sumian.blue.model.BluePeripheral;
import com.sumian.common.operator.AppOperator;
import com.sumian.hw.log.LogManager;
import com.sumian.hw.utils.NotificationUtil;
import com.sumian.sd.account.bean.Token;
import com.sumian.sd.account.login.LoginActivity;
import com.sumian.sd.account.login.NewUserGuideActivity;
import com.sumian.sd.app.App;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.main.MainActivity;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/2 17:42
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class AppUtil {

    public static void exitApp() {
        AppManager.getSleepDataUploadManager().release();
        BluePeripheral bluePeripheral = AppManager.getBlueManager().getBluePeripheral();
        if (bluePeripheral != null) {
            bluePeripheral.close();
        }
        LogManager.appendUserOperationLog("用户退出 app.......");
        ActivityUtils.finishAllActivities();
    }

    public static void logoutAndLaunchLoginActivity() {
        AppManager.getOpenAnalytics().onProfileSignOff();
        AppOperator.runOnThread(() -> BlueManager.init().doStopScan());
        ChatClient.getInstance().logout(true, null);
        NotificationUtil.Companion.cancelAllNotification(App.Companion.getAppContext());
        AppManager.getAccountViewModel().updateToken(null);
        AppManager.getOpenLogin().deleteWechatTokenCache(ActivityUtils.getTopActivity(), null);
        ActivityUtils.finishAllActivities();
        ActivityUtils.startActivity(LoginActivity.class);
    }

    public static void launchMainAndFinishAll() {
        ActivityUtils.finishAllActivities();
        launchMain();
    }

    public static void launchMain() {
        ActivityUtils.startActivity(MainActivity.class);
    }

    public static void launchMainOrNewUserGuide() {
        Token token = AppManager.getAccountViewModel().getToken();
        if (token != null && token.is_new) {
            ActivityUtils.startActivity(NewUserGuideActivity.class);
            ActivityUtils.finishAllActivities();
        } else {
            launchMainAndFinishAll();
        }
    }

    public static Class<MainActivity> getMainClass() {
        return MainActivity.class;
    }

    public static boolean isAppForeground() {
        return AppUtils.isAppForeground();
    }
}
