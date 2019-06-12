package com.sumian.common.buz.kefu

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.AppUtils
import com.qiyukf.nimlib.sdk.NimIntent
import com.qiyukf.unicorn.api.*
import com.sumian.common.R
import com.sumian.common.utils.JsonUtil

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/31 14:36
 * desc   : ref:https://qiyukf.com/docs/guide/android/Android_SDK_Guide.html#%E5%85%B3%E8%81%94%E7%94%A8%E6%88%B7%E5%92%8C%E8%B5%84%E6%96%99
 * version: 1.0
 */
object KefuManager {
    val mUnreadCountLiveData = MutableLiveData<Int>()
    private var mKefuActivityTitle = ""

    fun init(application: Application, params: KefuParams) {
        val appKey = "facb8c478e145c8ca843ee40ae6fde8b"
        mKefuActivityTitle = params.kefuActivityTitle
        Unicorn.init(application, appKey, options(params), GlideImageLoader(application))
        Unicorn.addUnreadCountChangeListener({ count -> mUnreadCountLiveData.value = count }, true)
    }

    fun logout() {
        Unicorn.logout()
    }

    fun setUserInfo(userInfo: UserInfo) {
        val ysfUserInfo = YSFUserInfo()
        val list = ArrayList<UserInfoDataItem>()
        list.add(UserInfoDataItem("real_name", userInfo.name))
        list.add(UserInfoDataItem("avatar", userInfo.avatar))
        list.add(UserInfoDataItem("mobile_phone", userInfo.mobile))
        ysfUserInfo.data = JsonUtil.toJson(list)
        ysfUserInfo.userId = userInfo.id
        Unicorn.setUserInfo(ysfUserInfo)
    }

    private fun options(params: KefuParams): YSFOptions {
        val options = YSFOptions()
        // notification
        val notificationConfig = StatusBarNotificationConfig()
        notificationConfig.notificationSmallIconId = params.notificationSmallIconId
        notificationConfig.notificationEntrance = params.notificationEntrance
        options.statusBarNotificationConfig = notificationConfig
        // ui
        val uiCustomization = UICustomization()
        uiCustomization.leftAvatar = Uri.parse("android.resource://${AppUtils.getAppPackageName()}/" + R.drawable.ic_chat_assiant_default).toString()
        uiCustomization.rightAvatar = params.userAvatar
        options.uiCustomization = uiCustomization
        // bot event
        options.onBotEventListener = object : OnBotEventListener() {
            override fun onUrlClick(context: Context?, url: String?): Boolean {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context!!.startActivity(intent)
                return true
            }
        }
        return options
    }

    fun isFromUnicorn(bundle: Bundle): Boolean {
        return bundle.containsKey(NimIntent.EXTRA_NOTIFY_CONTENT)
    }

    fun launchKefuActivity(context: Context) {
        val source = ConsultSource("uri", mKefuActivityTitle, "")
        Unicorn.openServiceActivity(context, mKefuActivityTitle, source)
    }

    data class KefuParams(
            var notificationEntrance: Class<out Activity>,
            var kefuActivityTitle: String,
            var userAvatar: String? = null,
            var notificationSmallIconId: Int)

    data class UserInfoDataItem(val key: String, val value: String)

    //https://qiyukf.com/docs/guide/android/Android_SDK_Guide.html#%E5%85%B3%E8%81%94%E7%94%A8%E6%88%B7%E5%92%8C%E8%B5%84%E6%96%99
    data class UserInfo(val id: String, val name: String, val avatar: String, val mobile: String)
}