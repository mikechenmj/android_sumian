@file:Suppress("UNUSED_ANONYMOUS_PARAMETER", "UNUSED_ANONYMOUS_PARAMETER")

package com.sumian.sd.buz.setting

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Observer
import com.avos.avoscloud.AVInstallation
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sumian.common.base.BaseActivity
import com.sumian.common.helper.ToastHelper
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.account.login.SettingPasswordActivity
import com.sumian.sd.buz.devicemanager.DeviceManager
import com.sumian.sd.buz.qrcode.activity.QrCodeActivity
import com.sumian.sd.buz.setting.remind.RemindSettingActivity
import com.sumian.sd.buz.setting.version.VersionActivity
import com.sumian.sd.buz.setting.version.VersionManager
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.buz.upgrade.activity.DeviceVersionNoticeActivity
import com.sumian.sd.common.h5.H5Uri
import com.sumian.sd.common.h5.SimpleWebActivity
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import com.sumian.sd.common.utils.UiUtils
import com.sumian.sd.widget.TitleBar
import com.sumian.sd.widget.dialog.SumianAlertDialog
import com.sumian.sd.widget.divider.SettingDividerView
import kotlinx.android.synthetic.main.activity_main_setting.*
import kotlinx.android.synthetic.main.lay_setting_divider_item.view.*

/**
 * Created by jzz
 * on 2018/1/21.
 * desc:
 */

class SettingActivity : BaseActivity(), TitleBar.OnBackClickListener, View.OnClickListener {

    private val mSdvAppVersion: SettingDividerView  by lazy {
        findViewById<SettingDividerView>(R.id.sdv_app_version)
    }

    private var dialog: BottomSheetDialog? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_main_setting
    }

    override fun initWidget() {
        super.initWidget()
        findViewById<TitleBar>(R.id.title_bar).setOnBackClickListener(this)
        findViewById<View>(R.id.sdv_app_version).setOnClickListener(this)
        findViewById<View>(R.id.sdv_about_us).setOnClickListener(this)
        findViewById<View>(R.id.tv_logout).setOnClickListener(this)
        findViewById<View>(R.id.sdv_remind).setOnClickListener(this)
        findViewById<View>(R.id.sdv_device_version).setOnClickListener(this)
        findViewById<View>(R.id.sdv_change_bind).setOnClickListener(this)
        findViewById<View>(R.id.sdv_feedback).setOnClickListener(this)
        findViewById<View>(R.id.sdv_modify_password).setOnClickListener(this)
        findViewById<View>(R.id.sdv_clear_cache).setOnClickListener(this)

        VersionManager.queryVersion()
        VersionManager.mHasUpgradeLiveData.observe(this, Observer { sdv_app_version.showRedDot(it) })
    }

    override fun initData() {
        super.initData()
        invalidVersion()
    }

    override fun onBack(v: View) {
        finish()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sdv_remind -> RemindSettingActivity.show()
            R.id.sdv_app_version -> ActivityUtils.startActivity(VersionActivity::class.java)
            R.id.sdv_about_us -> SimpleWebActivity.launch(this, H5Uri.ABOUT_US)
            R.id.sdv_device_version -> DeviceVersionNoticeActivity.show(v.context)
            R.id.sdv_change_bind -> {
                if (!DeviceManager.isMonitorConnected()) {
                    ToastHelper.show(getString(R.string.please_connect_monitor_before_change_bind))
                    return
                }

                SumianAlertDialog(this)
                        .hideTopIcon(true)
                        .setCancelable(true)
                        .setCloseIconVisible(false)
                        .setTitle(R.string.are_sure_2_bind)
                        .setMessage("此功能适用于监测仪或速眠仪发生故障，更换设备后重新绑定速眠仪的操作，是否继续？")
                        .setLeftBtn(R.string.cancel, null)
                        .whitenLeft()
                        .setRightBtn(R.string.sure) { v1 -> QrCodeActivity.show(this@SettingActivity) }
                        .show()
            }
            R.id.sdv_feedback -> FeedbackActivity.show()
            R.id.sdv_modify_password -> SettingPasswordActivity.start(AppManager.getAccountViewModel().userInfo!!.hasPassword, null)
            R.id.tv_logout -> showLogoutDialog()
            R.id.sdv_clear_cache -> SumianAlertDialog(this)
                    .hideTopIcon(true)
                    .setTitle(R.string.clear_cache)
                    .setMessage(R.string.clear_cache_hint)
                    .setLeftBtn(R.string.cancel, null)
                    .setRightBtn(R.string.confirm) { v12 ->
                        val b = FileUtils.deleteAllInDir(cacheDir)
                        LogUtils.d(b)
                        ToastUtils.showShort(R.string.clear_success)
                    }
                    .show()
            else -> {
            }
        }
    }

    private fun invalidVersion() {
        val packageInfo = UiUtils.getPackageInfo(this)
        val versionName = packageInfo!!.versionName
        mSdvAppVersion.setContent(versionName)
        sdv_device_version.v_dot.visibility = if (DeviceManager.hasFirmwareNeedUpdate()) View.VISIBLE else View.GONE
    }

    private fun showLogoutDialog() {
        if (dialog == null) {
            dialog = BottomSheetDialog(this)
            @SuppressLint("InflateParams") val inflate = LayoutInflater.from(this).inflate(R.layout.lay_bottom_sheet_logout, null, false)
            inflate.findViewById<View>(R.id.tv_logout).setOnClickListener { v ->
                logout()
                dialog!!.dismiss()
            }
            inflate.findViewById<View>(R.id.tv_cancel).setOnClickListener { v -> dialog!!.dismiss() }
            dialog!!.setContentView(inflate)
            dialog!!.setCanceledOnTouchOutside(true)
        }
        if (dialog != null && !dialog!!.isShowing) {
            dialog!!.show()
        }
    }

    private fun logout() {
        val call = AppManager.getSdHttpService().logout(AVInstallation.getCurrentInstallation().installationId)
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

    override fun getPageName(): String {
        return StatConstants.page_setting
    }
}
