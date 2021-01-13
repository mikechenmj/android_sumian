package com.sumian.sd.examine.login.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.account.login.CaptchaHelper
import com.sumian.sd.buz.account.login.ImageCaptchaDialogActivity
import com.sumian.sd.buz.account.login.LoginActivity
import com.sumian.sd.examine.login.bean.RegisterBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExamineRegisterViewModel : BaseExamineViewModel() {

    companion object {
        const val SUCCESS = 1
        const val FAILED = 2
        const val IMAGE_CAPTCHA = 3
    }

    suspend fun doRegister(mobile: String, pwd: String, captcha: String): Boolean {
        val registerBody: RegisterBody = RegisterBody()
                .setMobile(mobile)
                .setPassword(pwd)
                .setCaptcha(captcha)
        return withContext(Dispatchers.IO) {
            val call = AppManager.getSdHttpService().doRegister(registerBody)
            mWorkTasks.add(call)
            val response = call.execute()
            val isSuccess = response.isSuccessful
            withContext(Dispatchers.Main) {
                if (isSuccess) {
                    AppManager.onLoginSuccess(response.body())
                } else {
                    val errorResponse = response.errorBody()?.let { getErrorResponseFromErrorBody(response.code(), it) }
                    ToastUtils.showShort(errorResponse?.message)
                }
            }
            isSuccess
        }
    }

    suspend fun doCaptcha(mobile: String): Int {
        val channel = Channel<Int>()
        val call = CaptchaHelper.requestCaptcha(mobile, object : CaptchaHelper.RequestCaptchaListener {
            override fun onFail(code: Int) {
                if (code != 4001) {
                    viewModelScope.launch {
                        channel.send(FAILED)
                        channel.close()
                    }
                } else {
                    viewModelScope.launch {
                        channel.send(IMAGE_CAPTCHA)
                        channel.close()
                    }
                }
            }

            override fun onStart() {
            }

            override fun onSuccess() {
                viewModelScope.launch {
                    channel.send(SUCCESS)
                    channel.close()
                }
            }

            override fun onFinish() {
            }
        })
        mWorkTasks.add(call)
        return channel.receive()
    }

}