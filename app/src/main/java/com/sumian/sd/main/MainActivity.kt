@file:Suppress("unused")

package com.sumian.sd.main

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import com.hyphenate.helpdesk.easeui.UIProvider
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.SettingsUtil
import com.sumian.hw.leancloud.HwLeanCloudHelper
import com.sumian.hw.upgrade.activity.DeviceVersionNoticeActivity
import com.sumian.hw.upgrade.model.VersionModel
import com.sumian.hw.utils.FragmentUtil
import com.sumian.sd.R
import com.sumian.sd.account.bean.UserInfo
import com.sumian.sd.app.App
import com.sumian.sd.app.AppManager
import com.sumian.sd.base.BaseEventActivity
import com.sumian.sd.constants.SpKeys
import com.sumian.sd.diary.DiaryFragment
import com.sumian.sd.event.EventBusUtil
import com.sumian.sd.event.NotificationUnreadCountChangeEvent
import com.sumian.sd.homepage.HomepageFragment
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

class MainActivity : BaseEventActivity(), HwLeanCloudHelper.OnShowMsgDotCallback, VersionModel.ShowDotCallback {

    companion object {
        const val TAB_INVALID = -1
        const val TAB_0 = 0
        const val TAB_1 = 1
        const val TAB_2 = 2
        const val TAB_3 = 3

        private const val KEY_TAB_INDEX = "key_tab_name"
        private const val KEY_TAB_DATA = "key_tab_data"
        private const val REQUEST_CODE_OPEN_NOTIFICATION = 1
        private var mCurrentPosition = -1

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
            DiaryFragment::class.java.simpleName,
            DoctorFragment::class.java.simpleName,
            MeFragment::class.java.simpleName)
    private var mLaunchTab = TAB_INVALID
    private var mLaunchTabData: String? = null

    private val mVersionDelegate: VersionDelegate  by lazy {
        VersionDelegate.init()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onStart() {
        super.onStart()
        mVersionDelegate.checkVersion(this)
        updateNotificationUnreadCount()
    }

    override fun initWidget() {
        super.initWidget()
        showOpenNotificationDialogIfNeeded()
        nav_tab.setOnSelectedTabChangeListener { navigationItem, position -> changeSelectTab(position) }
        nav_tab.selectItem(TAB_0, true)

        //注册站内信消息接收容器
        HwLeanCloudHelper.addOnAdminMsgCallback(this)
        AppManager.getVersionModel().registerShowDotCallback(this)
    }

    override fun initData() {
        super.initData()
        //注册站内信消息接收容器
        HwLeanCloudHelper.addOnAdminMsgCallback(this)
        if (AppManager.getAccountViewModel().isLogin) {
            HwLeanCloudHelper.loginLeanCloud()
            HwLeanCloudHelper.registerPushService()
        }

        AppManager.getJobScheduler().checkJobScheduler()

        UIProvider.getInstance().showDotCallback { msgLength ->
            HwLeanCloudHelper.haveCustomerMsg(msgLength)
            onShowMsgDotCallback(0, 0, msgLength)
        }

        SumianExecutor.runOnUiThread(({
            syncUserInfo()
            sendHeartBeats()
        }), 200)

        SumianExecutor.runOnUiThread(({ HwLeanCloudHelper.haveCustomerMsg(UIProvider.getInstance().isHaveMsgSize) }), 500)
    }

    override fun initBundle(bundle: Bundle) {
        mLaunchTab = bundle.getInt(KEY_TAB_INDEX)
        mLaunchTabData = bundle.getString(KEY_TAB_DATA)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // mLaunchTab会在 initBundle中赋值
        nav_tab.selectItem(mLaunchTab, true)
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
        val call = AppManager.getHwHttpService().sendHeartbeats("open_app")
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
        if (position == 0 || position == 2) {
            StatusBarUtil.setStatusBarTextColorDark(this, true)
        } else {
            StatusBarUtil.setStatusBarTextColorDark(this, false)
        }
    }

    private fun changeSelectTab(position: Int) {
        if (mCurrentPosition == position) {
            return
        }
        showFragmentByPosition(position)
        changeStatusBarColorByPosition(position)
        mCurrentPosition = position
    }

    private fun showFragmentByPosition(position: Int) {
        FragmentUtil.switchFragment(R.id.sd_main_fragment_container, supportFragmentManager!!, mFragmentTags, position,
                object : FragmentUtil.FragmentCreator {
                    override fun createFragmentByPosition(position: Int): Fragment {
                        return when (position) {
                            0 -> HomepageFragment()
                            1 -> DiaryFragment()
                            2 -> DoctorFragment()
                            3 -> MeFragment()
                            else -> HomepageFragment()
                        }
                    }
                })
    }

    override fun openEventBus(): Boolean {
        return true
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
            this.tb_me?.showDot(if (adminMsgLen > 0 || doctorMsgLen > 0 || customerMsgLen > 0) android.view.View.VISIBLE else android.view.View.GONE)
        })
    }

    override fun showDot(isShowAppDot: Boolean, isShowMonitorDot: Boolean, isShowSleepyDot: Boolean) {

        var title = 0
        var message = ""

        if (isShowSleepyDot) {
            title = R.string.sleep_version_title
            message = "速眠仪固件新版本发布，为了保证给您的最佳体验，请及时升级"
        }

        if (isShowMonitorDot) {
            title = R.string.monitor_version_title
            message = "监测仪固件新版本发布，为了保证给您的最佳体验，请及时升级"
        }

        runOnUiThread {
            SumianAlertDialog(this@MainActivity)
                    .setCancelable(true)
                    .setCloseIconVisible(false)
                    .hideTopIcon(true)
                    .setTitle(title)
                    .setMessage(message)
                    .whitenLeft()
                    .setLeftBtn(R.string.cancel, null)
                    .setRightBtn(R.string.sure) {
                        DeviceVersionNoticeActivity.show(this@MainActivity)
                    }
                    .show()
        }
    }
}
