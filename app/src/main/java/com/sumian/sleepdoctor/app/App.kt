package com.sumian.sleepdoctor.app

import android.app.Application
import android.content.Context
import android.util.Log
import com.tencent.smtt.sdk.QbSdk

/**
 * Created by jzz
 * on 2018/1/15.
 * desc:
 */

class App : Application() {

    companion object {

        private val TAG: String = App::class.java.simpleName

        private lateinit var mAppContext: Context

        fun getAppContext() = mAppContext

    }

    override fun onCreate() {
        super.onCreate()

        mAppContext = this

        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。

        val cb = object : QbSdk.PreInitCallback {

            override fun onViewInitFinished(arg0: Boolean) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.e(TAG, " onViewInitFinished is $arg0")
            }

            override fun onCoreInitFinished() {
                // TODO Auto-generated method stub
                Log.e(TAG, "")
            }
        }
        //x5内核初始化接口
        QbSdk.initX5Environment(this, cb)

        AppManager.init().with(this)

    }


}
