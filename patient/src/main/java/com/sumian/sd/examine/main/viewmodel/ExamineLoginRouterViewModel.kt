package com.sumian.sd.examine.main.viewmodel

import android.util.Log
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.utils.JsonUtil
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.account.login.ValidatePhoneNumberActivity
import com.sumian.sd.examine.login.ExamineLoginRouterActivity
import com.sumian.sd.examine.login.viewmodel.BaseExamineViewModel
import com.umeng.socialize.UMAuthListener
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ReceiveChannel
import java.util.HashMap
import kotlinx.coroutines.withContext
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

class ExamineLoginRouterViewModel : BaseExamineViewModel() {

    suspend fun loginByWechat(activity: ExamineLoginRouterActivity) = withContext(Dispatchers.IO) {
        val channel = weChatLogin(activity)
        Log.i("MCJ", "channel.isclose: ${channel.isClosedForReceive}")
        val map = channel.receive()
        if (map.isNullOrEmpty()) {
            withContext(Dispatchers.Main) {
                ToastUtils.showShort(R.string.login_cancel)
            }
            return@withContext
        }
        checkOpenIsBind(map)
    }

    private fun CoroutineScope.weChatLogin(activity: ExamineLoginRouterActivity): Channel<MutableMap<String, String?>?> {
        val channel = Channel<MutableMap<String, String?>?>()
        AppManager.getOpenLogin().weChatLogin(activity, object : UMAuthListener {
            override fun onComplete(shareMedia: SHARE_MEDIA?, p1: Int, map: MutableMap<String, String?>?) {
                Log.i("MCJ", "onComplete")
                launch {
                    Log.i("MCJ", "onComplete launch")
                    channel.send(map)
                }
            }

            override fun onCancel(p0: SHARE_MEDIA?, p1: Int) {
                Log.i("MCJ", "onCancel")
                ToastUtils.showShort(R.string.login_cancel)
                launch {
                    Log.i("MCJ", "launch onCancel")
                    channel.send(null)
                }
            }

            override fun onError(p0: SHARE_MEDIA?, p1: Int, p2: Throwable?) {
                ToastUtils.showShort(R.string.no_have_wechat)
                Log.i("MCJ", "onError")
                launch {
                    Log.i("MCJ", "launch onError")
                    channel.send(null)
                }
            }

            override fun onStart(p0: SHARE_MEDIA?) {
                Log.i("MCJ", "onStart")
                ToastUtils.showShort(R.string.opening_wechat)
            }
        })
        return channel
    }

    suspend fun checkOpenIsBind(openMap: MutableMap<String, String?>): Boolean {
        return withContext(Dispatchers.IO) {
            val map = HashMap<String, Any?>()
            map["type"] = 0
            map["union_id"] = openMap["unionid"]
            val call = AppManager.getSdHttpService().loginOpenPlatform(map)
            mWorkTasks.add(call)
            val response = call.execute()
            val isSuccess = response.isSuccessful
            withContext(Dispatchers.Main) {
                if (isSuccess) {
                    AppManager.onLoginSuccess(response.body())
                } else {
                    val errorResponse = response.errorBody()?.let { getErrorResponseFromErrorBody(response.code(), it) }
                    if (errorResponse?.code == 404) {
                        openMap["nickname"] = openMap["screen_name"]
                        openMap["headimgurl"] = openMap["profile_image_url"]
                        val socialInfo = JsonUtil.toJson(openMap)
                        ValidatePhoneNumberActivity.launchForBindMobile(socialInfo)
                    } else {
                        ToastUtils.showShort(errorResponse?.message)
                    }
                }
                isSuccess
            }
        }
    }
}

