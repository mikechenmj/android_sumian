package com.sumian.sd.main

import android.os.Handler
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseActivity
import com.sumian.hw.log.LogManager
import com.sumian.sd.R
import com.sumian.sd.account.login.LoginActivity
import com.sumian.sd.app.App
import com.sumian.sd.app.AppManager
import com.sumian.sd.kefu.KefuManager
import com.sumian.sd.utils.StatusBarUtil


/**
 * Created by jzz
 * on 2017/9/30
 *
 *
 * desc:
 */

class WelcomeActivity : BaseActivity() {

    companion object {
        private const val SPLASH_DURATION = 500L
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_welcome
    }

    override fun initWidget() {
        super.initWidget()
        StatusBarUtil.setStatusBarTextColorDark(this, true)
    }

    override fun initData() {
        super.initData()
        AppManager.initOnFirstActivityStart(App.getAppContext())
        LogManager.appendUserOperationLog("用户启动 app.......")
        Handler().postDelayed({
            val login = AppManager.getAccountViewModel().isLogin
            if (login) {
                 AppManager.launchMain()
                val launchCustomerServiceActivity = intent.getBooleanExtra("key_launch_online_customer_service_activity", false)
                if (launchCustomerServiceActivity) {
                    KefuManager.launchKefuActivity()
                }
            } else {
                ActivityUtils.startActivity(LoginActivity::class.java)
            }
        }, SPLASH_DURATION)
    }

    override fun onStop() {
        super.onStop()
        // 之所以在onStop finish，是因为在启动MainActivity 时马上finish 会导致无法执行Activity转场动画，会显示APP下面的内容。
        finish()
    }

    override fun onBackPressed() {
        //super.onBackPressed();
    }


}
