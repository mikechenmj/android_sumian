package com.sumian.device.authentication

import com.sumian.device.net.NetworkManager
import com.sumian.device.util.LogManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/14 17:58
 * desc   :
 * version: 1.0
 */
object AuthenticationManager {
    var mToken: String? = null

    fun login() {
        NetworkManager.getApi().loginByCaptcha("13570464488", "000000")
                .enqueue(object : Callback<Token> {
                    override fun onFailure(call: Call<Token>, t: Throwable) {
                        LogManager.log("login fail")
                    }

                    override fun onResponse(call: Call<Token>, response: Response<Token>) {
                        if (response.isSuccessful) {
                            mToken = response.body()?.token
                            LogManager.log("login success $mToken")
                        } else {
                            LogManager.log("login fail")
                        }
                    }
                })
    }

}