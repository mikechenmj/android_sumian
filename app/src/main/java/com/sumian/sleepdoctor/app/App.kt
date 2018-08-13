package com.sumian.sleepdoctor.app

import android.app.Application

/**
 * Created by jzz
 * on 2018/1/15.
 * desc:
 */

class App : Application() {
    companion object {
        private lateinit var mAppContext: Application
        fun getAppContext() = mAppContext
    }

    override fun onCreate() {
        super.onCreate()
        mAppContext = this
        AppManager.getInstance().init(this)
    }
}

