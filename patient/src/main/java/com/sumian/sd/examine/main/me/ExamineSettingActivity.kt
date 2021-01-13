package com.sumian.sd.examine.main.me

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import com.avos.avoscloud.AVInstallation
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseActivity
import com.sumian.common.helper.ToastHelper
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.JsonUtil.Companion.toJson
import com.sumian.device.manager.DeviceManager
import com.sumian.sd.BuildConfig
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.app.AppManager.getAccountViewModel
import com.sumian.sd.app.AppManager.getOpenLogin
import com.sumian.sd.app.AppManager.getSdHttpService
import com.sumian.sd.buz.account.bean.Social
import com.sumian.sd.buz.account.login.LoginActivity
import com.sumian.sd.buz.account.login.SettingPasswordActivity
import com.sumian.sd.buz.account.model.AccountManager
import com.sumian.sd.buz.qrcode.activity.QrCodeActivity
import com.sumian.sd.common.h5.H5Uri
import com.sumian.sd.common.h5.SimpleWebActivity
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import com.sumian.sd.examine.login.ExamineLoginRouterActivity
import com.sumian.sd.examine.main.me.setting.ExamineFeedbackActivity
import com.sumian.sd.widget.dialog.SumianAlertDialog
import com.umeng.socialize.UMAuthListener
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.android.synthetic.main.examine_setting.*
import java.util.*

class ExamineSettingActivity : BaseActivity(), UMAuthListener {

    companion object {
        fun show() {
            ActivityUtils.startActivity(ExamineSettingActivity::class.java)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.examine_setting
    }

    override fun initWidget() {
        super.initWidget()
        lay_feedback.setOnClickListener {
            startActivity(Intent(this, ExamineFeedbackActivity::class.java))
        }
        lay_about_me.setOnClickListener {
            SimpleWebActivity.launch(this, H5Uri.ABOUT_US)
        }
        lay_modify_pwd.setOnClickListener {
            SettingPasswordActivity.start(getAccountViewModel().userInfo!!.hasPassword, null)
        }
        bt_bind_wechat.setOnToggleChanged {
            showDialog(it)
        }
        lay_unbind_sleepy.setOnClickListener {
            if (!DeviceManager.isMonitorConnected()) {
                ToastHelper.show(getString(R.string.please_connect_monitor_before_change_bind))
                return@setOnClickListener
            }
            QrCodeActivity.show(this)
        }
        examine_title_bar.setOnBackClickListener { finish() }
        tv_logout.setOnClickListener { logout() }
        updateDvWechatUI(getAccountViewModel().userInfo?.socialites)
    }

    private fun logout() {
        val call = getSdHttpService().logout(AVInstallation.getCurrentInstallation().installationId)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<Unit>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(R.string.logout_failed_please_check_network)
            }

            override fun onSuccess(response: Unit?) {
                AppManager.logoutAndLaunchLoginActivity()
            }
        })
    }

    private fun showDialog(isChecked: Boolean) {
        val title = if (isChecked) R.string.bind_wechat_title else R.string.unbind_wechat_title
        val message = if (isChecked) R.string.bind_wechat_message else R.string.unbind_wechat_message
        val leftBtn = R.string.cancel
        val rightBtn = if (isChecked) R.string.bind else R.string.unbind
        SumianAlertDialog(this)
                .setTitle(title)
                .setMessage(message)
                .whitenLeft()
                .setLeftBtn(leftBtn) { v: View? ->
                    if (isChecked) bt_bind_wechat.setToggleOff() else bt_bind_wechat.setToggleOn()
                }
                .setRightBtn(rightBtn) { v: View? ->
                    if (isChecked) {
                        bindWechat(this, this)
                    } else {
                        val socialites: List<Social> = AccountManager.userInfo!!.socialites
                        Log.i("MCJ", "!isChecked $socialites")
                        if (socialites == null || socialites.size == 0) {
                            return@setRightBtn
                        }
                        val social = socialites[0]
                        unBindWechat(social.id)
                    }
                }
                .show()
    }

    private fun bindWechat(activity: Activity?, umAuthListener: UMAuthListener?) {
        getOpenLogin().weChatLogin(activity, umAuthListener)
    }

    fun bindSocial(socialType: Int, socialInfo: String?) {
        val call = getSdHttpService().bindSocialites(Social.SOCIAL_TYPE_WECHAT, socialInfo!!)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<Social?>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                onBindSocialFailed(errorResponse.message)
            }

            override fun onSuccess(response: Social?) {
                onBindSocialSuccess(response)
            }

            override fun onFinish() {
                super.onFinish()
            }
        })
    }

    fun unBindWechat(socialId: Int) {
        Log.i("MCJ", "unBindWechat $socialId")
        val call = getSdHttpService().unbindSocialites(socialId, true)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<String?>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                onUnBindWechatFailed(errorResponse.message)
                Log.i("MCJ", "unBindWechat onFailure ${errorResponse.message}")
            }

            override fun onSuccess(response: String?) {
                onUnBindWechatSuccess()
                Log.i("MCJ", "unBindWechat onSuccess $response")
            }

            override fun onFinish() {
                super.onFinish()
            }
        })
    }

    fun onUnBindWechatSuccess() {
        ToastUtils.showShort(R.string.unbind_success)
        updateSocialites(null)
        finish()
        if (BuildConfig.IS_EXAMINE_VERSION) {
            ExamineLoginRouterActivity.show()
        } else {
            LoginActivity.show()
        }
    }

    fun onUnBindWechatFailed(error: String?) {
        ToastUtils.showShort(error)
    }

    fun onBindSocialSuccess(social: Social?) {
        updateSocialites(social)
    }

    fun onBindSocialFailed(error: String?) {
        bindSocialitesFailed(error ?: "")
    }

    private fun bindSocialitesFailed(message: String) {
        ToastUtils.showShort(message)
        bt_bind_wechat.setToggleOff()
    }

    private fun updateSocialites(social: Social?) {
        val socials: MutableList<Social> = ArrayList()
        if (social != null) {
            socials.add(social)
        }
        AccountManager.userInfo!!.socialites = socials
        getAccountViewModel().updateUserInfo(AccountManager.userInfo)
        getOpenLogin().deleteWechatTokenCache(this, null)
    }

    override fun onComplete(p0: SHARE_MEDIA?, p1: Int, p2: MutableMap<String, String>?) {
        p2?.get("name")?.let { p2.put("nickname", it) }
        val userInfoJson = toJson(p2)
        bindSocial(Social.SOCIAL_TYPE_WECHAT, userInfoJson)
    }

    override fun onCancel(p0: SHARE_MEDIA?, p1: Int) {
        bindSocialitesFailed(getString(R.string.bind_canceled))
    }

    override fun onError(p0: SHARE_MEDIA?, p1: Int, p2: Throwable?) {
        p2?.message?.let { bindSocialitesFailed(it) }
    }

    override fun onStart(p0: SHARE_MEDIA?) {
    }

    private fun updateDvWechatUI(socialites: List<Social>?) {
        val hasSocial = socialites != null && socialites.size > 0
        if (hasSocial) bt_bind_wechat.setToggleOn() else bt_bind_wechat.setToggleOff()
    }
}