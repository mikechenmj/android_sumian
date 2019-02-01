package com.sumian.sddoctor.app

import android.app.Application
import com.sumian.common.social.analytics.ActivityLifecycleCallbackForUserAnalysis
import com.sumian.sddoctor.log.LogActivityLifecycleCallbacks

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/14 9:29
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class App : Application() {

    companion object {
        private lateinit var mApplication: Application

        @JvmStatic
        fun getAppContext(): Application {
            return mApplication
        }
    }

    override fun onCreate() {
        super.onCreate()
        mApplication = this
        AppManager.init(this)
        registerActivityLifecycleCallbacks(ActivityLifecycleCallbackForUserAnalysis())
        registerActivityLifecycleCallbacks(LogActivityLifecycleCallbacks())
    }
}