package com.sumian.sleepdoctor.app

import android.app.Application
import android.content.Context
import com.sumian.hw.app.App

/**
 * Created by jzz
 * on 2018/1/15.
 * desc:
 */

class App : Application() {

    companion object {

        private lateinit var mAppContext: Context

        fun getAppContext() = mAppContext

    }

    override fun onCreate() {
        super.onCreate()

        mAppContext = this

        AppManager.init().with(this)
        App.init(this)
    }

}

