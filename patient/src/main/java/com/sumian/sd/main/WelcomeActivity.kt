package com.sumian.sd.main

import android.graphics.Color
import android.os.Handler
import android.util.Log
import com.sumian.common.base.BaseActivity
import com.sumian.sd.BuildConfig
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.account.login.LoginActivity
import com.sumian.sd.common.utils.StatusBarUtil
import com.sumian.sd.examine.login.ExamineLoginRouterActivity

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
        if (BuildConfig.IS_EXAMINE_VERSION) {
            window.decorView.setBackgroundColor(Color.BLACK)
        }
    }

    override fun initData() {
        super.initData()
        Handler().postDelayed({
            val login = AppManager.getAccountViewModel().isLogin
            if (login) {
                AppManager.launchMain()
            } else {
                if (BuildConfig.IS_EXAMINE_VERSION) {
                    ExamineLoginRouterActivity.show(this)
                } else {
                    LoginActivity.show()
                }
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
