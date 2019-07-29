package com.sumian.devicedemo.app

import android.app.Application
import com.blankj.utilcode.util.Utils
import com.sumian.device.authentication.AuthenticationManager
import com.sumian.device.manager.DeviceManager

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/5 14:49
 * desc   :
 * version: 1.0
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
        DeviceManager.init(
                this,
                DeviceManager.Params(baseUrl = "https://sdapi-test.sumian.com/")
        )

        AuthenticationManager.login()
    }

}