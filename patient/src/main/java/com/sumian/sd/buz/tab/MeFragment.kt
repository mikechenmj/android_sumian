package com.sumian.sd.buz.tab

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cn.leancloud.chatkit.LCIMManager
import com.avos.avoscloud.AVInstallation
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sumian.common.base.BaseFragment
import com.sumian.common.image.loadImage
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.account.bean.UserInfo
import com.sumian.sd.buz.account.login.SettingPasswordActivity
import com.sumian.sd.buz.account.userProfile.UserInfoActivity
import com.sumian.sd.buz.notification.NotificationViewModel
import com.sumian.sd.buz.onlinereport.OnlineReportListActivity
import com.sumian.sd.buz.setting.remind.RemindSettingActivity
import com.sumian.sd.buz.version.VersionActivity
import com.sumian.sd.buz.version.VersionManager
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import com.sumian.sd.common.utils.UiUtils
import com.sumian.sd.widget.dialog.SumianAlertDialog
import kotlinx.android.synthetic.main.fragment_tab_me.*

/**
 * Created by jzz
 * on 2018/1/16.
 * desc:
 */

class MeFragment : BaseFragment() {
    private var dialog: BottomSheetDialog? = null

    private val mNotificationViewModel by lazy {
        ViewModelProviders.of(activity!!)
                .get(NotificationViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_tab_me
    }

    override fun initWidget() {
        super.initWidget()
        iv_avatar.setOnClickListener { ActivityUtils.startActivity(UserInfoActivity::class.java) }
        tv_nickname.setOnClickListener { ActivityUtils.startActivity(UserInfoActivity::class.java) }
        sdv_online_report.setOnClickListener { OnlineReportListActivity.launchForShowAll(this) }
        sdv_remind.setOnClickListener { RemindSettingActivity.show() }
        sdv_app_version.setOnClickListener { ActivityUtils.startActivity(VersionActivity::class.java) }
        sdv_modify_password.setOnClickListener { SettingPasswordActivity.start(AppManager.getAccountViewModel().userInfo!!.hasPassword, null) }
        sdv_clear_cache.setOnClickListener { showClearCacheDialog() }
        tv_logout.setOnClickListener { showLogoutDialog() }

        val packageInfo = UiUtils.getPackageInfo(activity)
        val versionName = packageInfo!!.versionName
        sdv_app_version.setContent(versionName)
        VersionManager.queryAppVersion()
        VersionManager.mAppUpgradeMode.observe(this, Observer {
            sdv_app_version.showRedDot(it == VersionManager.UPGRADE_MODE_NORMAL || it == VersionManager.UPGRADE_MODE_FORCE)
        })
    }

    private fun showClearCacheDialog() {
        SumianAlertDialog(activity)
                .hideTopIcon(true)
                .setTitle(R.string.clear_cache)
                .setMessage(R.string.clear_cache_hint)
                .setLeftBtn(R.string.cancel, null)
                .setRightBtn(R.string.confirm) { v12 ->
                    val b = FileUtils.deleteAllInDir(activity!!.cacheDir)
                    LogUtils.d(b)
                    ToastUtils.showShort(R.string.clear_success)
                }
                .show()
    }

    override fun initData() {
        super.initData()
        val userProfile = AppManager.getAccountViewModel().userInfo!!
        updateUserProfile(userProfile)
        AppManager.getAccountViewModel().getUserInfoLiveData().observe(this, Observer<UserInfo> { userInfo ->
            run {
                userInfo?.let {
                    updateUserProfile(it)
                }
            }
        })
//        mNotificationViewModel
//                .unreadCount
//                .observe(this, Observer<Int> { updateNotificationIcon() })
//        LCIMManager.getInstance().unreadCountLiveData.observe(this, Observer<Int> { updateNotificationIcon() })
//        VersionManager.queryAppVersion()
//        VersionManager.mAppUpgradeMode.observe(this, Observer {
//            sdv_app_version.showRedDot(it == VersionManager.UPGRADE_MODE_FORCE)
//        })
    }

    private fun updateNotificationIcon() {
        val notificationCount = mNotificationViewModel.unreadCount.value
        val hasNotification = notificationCount != null && notificationCount > 0
        val hasIm = LCIMManager.getInstance().unreadMessageCount > 0
//        iv_notification.isActivated = hasNotification || hasIm
    }

    private fun updateUserProfile(userProfile: UserInfo) {
        iv_avatar.loadImage(userProfile.avatar, R.mipmap.ic_info_avatar_patient, R.mipmap.ic_info_avatar_patient)
        val nickname = userProfile.nickname
        tv_nickname.text = nickname
    }

    private fun showLogoutDialog() {
        if (dialog == null) {
            dialog = BottomSheetDialog(activity!!)
            @SuppressLint("InflateParams") val inflate = LayoutInflater.from(activity!!).inflate(R.layout.lay_bottom_sheet_logout, null, false)
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
}
