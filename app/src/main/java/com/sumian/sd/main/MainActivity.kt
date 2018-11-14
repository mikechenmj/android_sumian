@file:Suppress("unused", "UNUSED_ANONYMOUS_PARAMETER")

package com.sumian.sd.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.hyphenate.helpdesk.easeui.UIProvider
import com.sumian.common.base.BaseActivity
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.SettingsUtil
import com.sumian.hw.leancloud.HwLeanCloudHelper
import com.sumian.hw.log.LogManager
import com.sumian.hw.upgrade.activity.DeviceVersionNoticeActivity
import com.sumian.hw.upgrade.model.VersionModel
import com.sumian.hw.utils.FragmentUtil
import com.sumian.sd.R
import com.sumian.sd.account.bean.Token
import com.sumian.sd.account.bean.UserInfo
import com.sumian.sd.app.App
import com.sumian.sd.app.AppManager
import com.sumian.sd.constants.SpKeys
import com.sumian.sd.device.AutoSyncDeviceDataUtil
import com.sumian.sd.device.DeviceManager
import com.sumian.sd.diary.DataFragment
import com.sumian.sd.event.EventBusUtil
import com.sumian.sd.event.NotificationUnreadCountChangeEvent
import com.sumian.sd.homepage.HomepageFragment
import com.sumian.sd.leancloud.LeanCloudManager
import com.sumian.sd.main.event.ChangeMainTabEvent
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.notification.NotificationViewModel
import com.sumian.sd.setting.version.delegate.VersionDelegate
import com.sumian.sd.tab.DoctorFragment
import com.sumian.sd.tab.MeFragment
import com.sumian.sd.utils.NotificationUtil
import com.sumian.sd.utils.StatusBarUtil
import com.sumian.sd.utils.SumianExecutor
import com.sumian.sd.widget.dialog.SumianAlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : BaseActivity(), HwLeanCloudHelper.OnShowMsgDotCallback, VersionModel.ShowDotCallback {

    companion object {
        const val TAB_INVALID = -1
        const val TAB_0 = 0
        const val TAB_1 = 1
        const val TAB_2 = 2
        const val TAB_3 = 3

        private const val KEY_TAB_INDEX = "key_tab_name"
        private const val KEY_TAB_DATA = "key_tab_data"
        private const val REQUEST_CODE_OPEN_NOTIFICATION = 1
        private var mCurrentPosition = TAB_INVALID

        @JvmStatic
        fun launch(tab: Int, tabData: String? = null) {
            ActivityUtils.startActivity(getLaunchIntentForTab(tab, tabData))
        }

        private fun getLaunchIntentForTab(tabIndex: Int, tabData: String? = null): Intent {
            val intent = Intent(App.getAppContext(), MainActivity::class.java)
            intent.putExtra(KEY_TAB_INDEX, tabIndex)
            intent.putExtra(KEY_TAB_DATA, tabData)
            return intent
        }
    }

    private val mFragmentTags = arrayOf(
            HomepageFragment::class.java.simpleName,
            DataFragment::class.java.simpleName,
            DoctorFragment::class.java.simpleName,
            MeFragment::class.java.simpleName)
    private var mLaunchTabData: String? = null
    private var mIsResume = false

    private val mVersionDelegate: VersionDelegate  by lazy {
        VersionDelegate.init()
    }

    private val mDeviceVersionDialog: SumianAlertDialog by lazy {
        SumianAlertDialog(this@MainActivity)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onStart() {
        super.onStart()
        mVersionDelegate.checkVersion(this)
        updateNotificationUnreadCount()
        EventBusUtil.register(this)

////        SumianExecutor.runOnBackgroundThread {
//        SumianExecutor.runOnUiThread( {
//            val token = AppManager.getAccountViewModel().token
//            LogUtils.d("token ", token.expired_at)
//            val token1 = Token()
//            token1.token = token.token
//            token1.user = token.user
//            token1.expired_at = token.expired_at + 1
//            AppManager.getAccountViewModel().updateToken(token1)
//            LogUtils.d("token ", AppManager.getAccountViewModel().token.expired_at)
//        })

    }

    override fun onResume() {
        super.onResume()
        mIsResume = true
        AutoSyncDeviceDataUtil.register(this)
    }

    override fun onPause() {
        super.onPause()
        mIsResume = false
        AutoSyncDeviceDataUtil.unRegister(this)
    }

    override fun onStop() {
        super.onStop()
        EventBusUtil.unregister(this)
    }

    override fun initWidget() {
        super.initWidget()
        showOpenNotificationDialogIfNeeded()
        nav_tab.setOnSelectedTabChangeListener { navigationItem, position -> changeSelectFragment(position) }
        changeSelectTab(TAB_0)
        AppManager.getVersionModel().registerShowDotCallback(this)
        ViewModelProviders.of(this@MainActivity)
                .get(NotificationViewModel::class.java)
                .unreadCount
                .observe(this, Observer<Int> { unreadCount ->
                    tb_me?.showDot(if (unreadCount != null && unreadCount > 0) View.VISIBLE else View.GONE)
                })
    }

    override fun initData() {
        super.initData()
        DeviceManager.uploadCacheSn()
        //注册站内信消息接收容器
        HwLeanCloudHelper.addOnAdminMsgCallback(this)
        if (AppManager.getAccountViewModel().isLogin) {
            HwLeanCloudHelper.loginEasemob(null)
            LeanCloudManager.uploadPushId()
        }

        AppManager.getSleepDataUploadManager().checkPendingTaskAndRun()

        UIProvider.getInstance().showDotCallback { msgLength ->
            HwLeanCloudHelper.haveCustomerMsg(msgLength)
            onShowMsgDotCallback(0, 0, msgLength)
        }

        SumianExecutor.runOnUiThread(({
            sendHeartBeats()
            syncUserInfo()
        }), 200)

        SumianExecutor.runOnUiThread(({ HwLeanCloudHelper.haveCustomerMsg(UIProvider.getInstance().isHaveMsgSize) }), 500)
        // 中途医生绑定状态发生改变时，如果处于doctor tab，改变status 颜色
        AppManager.getDoctorViewModel().getDoctorLiveData().observe(this, Observer { doctor ->
            run {
                if (mCurrentPosition == 2) {
                    changeStatusBarTextColor(doctor == null)
                }
            }
        })
    }

    private fun changeStatusBarTextColor(isDark: Boolean) {
        StatusBarUtil.setStatusBarTextColorDark(this@MainActivity, isDark)
    }

    override fun initBundle(bundle: Bundle) {
        var launchTabPosition = bundle.getInt(KEY_TAB_INDEX, mCurrentPosition)
        mLaunchTabData = bundle.getString(KEY_TAB_DATA)
        if (launchTabPosition == TAB_INVALID) {
            launchTabPosition = 0
        }
        changeSelectTab(launchTabPosition)
    }

    /**
     * nav_tab 改变后会回调 {@link #changeSelectFragment}
     * @see changeSelectFragment
     */
    private fun changeSelectTab(position: Int) {
        nav_tab.selectItem(position, true)
    }

    override fun onRelease() {
        super.onRelease()
        HwLeanCloudHelper.removeOnAdminMsgCallback(this)
        AppManager.getVersionModel().unRegisterShowDotCallback(this)
    }

    private fun showOpenNotificationDialogIfNeeded() {
        val previousShowTime = SPUtils.getInstance().getLong(SpKeys.SLEEP_RECORD_PREVIOUS_SHOW_NOTIFICATION_TIME, 0)
        val alreadyShowed = previousShowTime > 0
        if (NotificationUtil.areNotificationsEnabled(this@MainActivity) || alreadyShowed) {
            return
        }
        SumianAlertDialog(this@MainActivity)
                .setCloseIconVisible(true)
                .setTopIconResource(R.mipmap.ic_notification_alert)
                .setTitle(R.string.open_notification)
                .setMessage(R.string.open_notification_and_receive_doctor_response)
                .setRightBtn(R.string.open_notification) { SettingsUtil.launchSettingActivityForResult(this, REQUEST_CODE_OPEN_NOTIFICATION) }
                .show()
        SPUtils.getInstance().put(SpKeys.SLEEP_RECORD_PREVIOUS_SHOW_NOTIFICATION_TIME, System.currentTimeMillis())
    }

    override fun onBackPressed() {
        returnToPhoneLauncher()
    }

    private fun sendHeartBeats() {
        val call = AppManager.getSdHttpService().sendHeartbeats("open_app")
        call.enqueue(object : BaseSdResponseCallback<Any?>() {
            override fun onFailure(errorResponse: ErrorResponse) {

            }

            override fun onSuccess(response: Any?) {

            }
        })
    }

    private fun syncUserInfo() {
        val call = AppManager.getSdHttpService().getUserProfile()
        call.enqueue(object : BaseSdResponseCallback<UserInfo>() {
            override fun onFailure(errorResponse: ErrorResponse) {
            }

            override fun onSuccess(response: UserInfo?) {
                AppManager.getAccountViewModel().updateUserInfo(response)
            }
        })
    }

    private fun returnToPhoneLauncher() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }

    private fun changeStatusBarColorByPosition(position: Int) {
        val isDark = when (position) {
            0 -> true
            1 -> false
            2 -> !AppManager.getAccountViewModel().isBindDoctor
            else -> false
        }
        changeStatusBarTextColor(isDark)
    }

    private fun changeSelectFragment(position: Int) {
        showFragmentByPosition(position)
        changeStatusBarColorByPosition(position)
        mCurrentPosition = position
        when (position) {
            0 -> LogManager.appendUserOperationLog("首页Tab切换 -> $position 首页")
            1 -> LogManager.appendUserOperationLog("首页Tab切换 -> $position 数据")
            2 -> LogManager.appendUserOperationLog("首页Tab切换 -> $position 医生")
            3 -> LogManager.appendUserOperationLog("首页Tab切换 -> $position 我的")
        }
    }

    private fun showFragmentByPosition(position: Int) {
        FragmentUtil.switchFragment(R.id.sd_main_fragment_container, supportFragmentManager!!, mFragmentTags, position,
                object : FragmentUtil.FragmentCreator {
                    override fun createFragmentByPosition(position: Int): Fragment {
                        return when (position) {
                            0 -> HomepageFragment()
                            1 -> DataFragment()
                            2 -> DoctorFragment()
                            3 -> MeFragment()
                            else -> HomepageFragment()
                        }
                    }
                })
    }

    @Subscribe(sticky = true)
    fun onNotificationUnreadCountChangeEvent(event: NotificationUnreadCountChangeEvent) {
        updateNotificationUnreadCount()
        EventBusUtil.removeStickyEvent(event)
    }

    private fun updateNotificationUnreadCount() {
        ViewModelProviders.of(this)
                .get(NotificationViewModel::class.java)
                .updateUnreadCount()
    }

    override fun onShowMsgDotCallback(adminMsgLen: Int, doctorMsgLen: Int, customerMsgLen: Int) {
        onHideMsgCallback(adminMsgLen, doctorMsgLen, customerMsgLen)
    }

    override fun onHideMsgCallback(adminMsgLen: Int, doctorMsgLen: Int, customerMsgLen: Int) {
        SumianExecutor.runOnUiThread({
            this.tb_doctor?.showDot(if (adminMsgLen > 0 || doctorMsgLen > 0 || customerMsgLen > 0) android.view.View.VISIBLE else android.view.View.GONE)
            // this.tb_me?.showDot(if (adminMsgLen > 0 || doctorMsgLen > 0 || customerMsgLen > 0) android.view.View.VISIBLE else android.view.View.GONE)
        })
    }

    override fun showDot(isShowAppDot: Boolean, isShowMonitorDot: Boolean, isShowSleepyDot: Boolean) {

        if (mIsResume && DeviceManager.isConnected()) {
            var titleResId = R.string.monitor_version_title
            var message = R.string.monitor_version_message

            if (isShowSleepyDot) {
                titleResId = R.string.sleep_version_title
                message = R.string.sleep_version_message
            }
            if (isShowMonitorDot) {
                titleResId = R.string.monitor_version_title
                message = R.string.monitor_version_message
            }
            if (mDeviceVersionDialog.isShowing) {
                mDeviceVersionDialog.hide()
            }
            mDeviceVersionDialog
                    .setCancelable(true)
                    .setCloseIconVisible(false)
                    .hideTopIcon(true)
                    .setTitle(titleResId)
                    .setMessage(message)
                    .whitenLeft()
                    .setLeftBtn(R.string.cancel, null)
                    .setRightBtn(R.string.sure) {
                        mDeviceVersionDialog.hide()
                        DeviceVersionNoticeActivity.show(this@MainActivity)
                    }
                    .show()
        }
    }

    private fun invalidToken() {
        val viewModel = AppManager.getAccountViewModel()
        val token = viewModel.token
        token.token = "123"
        viewModel.updateToken(token)
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onChangeTabEvent(event: ChangeMainTabEvent) {
        EventBusUtil.removeStickyEvent(event)
        changeSelectTab(event.tabIndex)
    }
}
