@file:Suppress("unused", "UNUSED_ANONYMOUS_PARAMETER")

package com.sumian.sd.main

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cn.leancloud.chatkit.LCIMManager
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.sumian.common.base.BaseActivity
import com.sumian.common.buz.kefu.KefuManager
import com.sumian.common.notification.NotificationUtil
import com.sumian.common.statistic.StatUtil
import com.sumian.common.utils.SettingsUtil
import com.sumian.common.utils.Sha1Util
import com.sumian.sd.R
import com.sumian.sd.app.App
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.devicemanager.AutoSyncDeviceDataUtil
import com.sumian.sd.buz.diary.DataFragment
import com.sumian.sd.buz.homepage.H5HomepageFragment
import com.sumian.sd.buz.notification.NotificationUnreadCountChangeEvent
import com.sumian.sd.buz.notification.NotificationViewModel
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.buz.tab.DoctorFragment
import com.sumian.sd.buz.tab.MeFragment
import com.sumian.sd.buz.version.VersionManager
import com.sumian.sd.common.log.LogManager
import com.sumian.sd.common.utils.EventBusUtil
import com.sumian.sd.common.utils.FragmentUtil
import com.sumian.sd.common.utils.StatusBarUtil
import com.sumian.sd.main.event.ChangeMainTabEvent
import com.sumian.sd.widget.dialog.SumianAlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : BaseActivity() {

    companion object {
        const val TAB_INVALID = -1
        const val TAB_0 = 0
        const val TAB_1 = 1
        const val TAB_2 = 2
        const val TAB_3 = 3

        private var mH5Fragment: H5HomepageFragment? = null

        private const val SLEEP_RECORD_PREVIOUS_SHOW_NOTIFICATION_TIME = "SLEEP_RECORD_PREVIOUS_SHOW_NOTIFICATION_TIME"
        private const val KEY_TAB_INDEX = "key_tab_name"
        private const val KEY_TAB_DATA = "key_tab_data"
        private const val KEY_H5_URL = "key_h5_url"
        private const val REQUEST_CODE_OPEN_NOTIFICATION = 1

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

        fun getLaunchIntentForH5(url: String): Intent {
            val intent = Intent(App.getAppContext(), MainActivity::class.java)
            intent.putExtra(KEY_TAB_INDEX, 0)
            intent.putExtra(KEY_H5_URL, url)
            return intent
        }
    }

    private val mFragmentTags = arrayOf(
            H5HomepageFragment::class.java.simpleName,
            DataFragment::class.java.simpleName,
            DoctorFragment::class.java.simpleName,
            MeFragment::class.java.simpleName)
    private var mLaunchTabData: String? = null
    private var mIsResume = false
    var mCurrentPosition = TAB_INVALID

    private val mDeviceVersionDialog: SumianAlertDialog by lazy {
        SumianAlertDialog(this@MainActivity)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppManager.onMainActivityCreate()
        requestPermission()
        LogUtils.d("app sha1: ${Sha1Util.getCertificateSHA1Fingerprint(this)}")
    }

    private fun requestPermission() {
        val readPhoneStatePermission = Manifest.permission.READ_PHONE_STATE
        val hasPermissions = EasyPermissions.hasPermissions(this, readPhoneStatePermission)
        if (!hasPermissions) {
            EasyPermissions.requestPermissions(this, "获取手机状态信息", 100, readPhoneStatePermission)
        }
    }

    override fun onStart() {
        super.onStart()
        VersionManager.queryAppVersion(true)
        updateNotificationUnreadCount()
        EventBusUtil.register(this)
    }

    override fun onResume() {
        super.onResume()
        mIsResume = true
        if (mCurrentPosition == 0) {
            AutoSyncDeviceDataUtil.autoSyncSleepData()
        }
    }

    override fun onPause() {
        super.onPause()
        mIsResume = false
    }

    override fun onStop() {
        super.onStop()
        EventBusUtil.unregister(this)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        AppManager.onMainActivityRestore()
    }

    private val mNotificationViewModel by lazy {
        ViewModelProviders.of(this@MainActivity).get(NotificationViewModel::class.java)
    }

    override fun initWidget() {
        super.initWidget()
        showOpenNotificationDialogIfNeeded()
        nav_tab.setOnSelectedTabChangeListener { navigationItem, position -> changeSelectFragment(position) }
        changeSelectTab(TAB_0)
        mNotificationViewModel.unreadCount.observe(this, Observer { updateTabDot() })
        KefuManager.mUnreadCountLiveData.observe(this, Observer { updateTabDot() })
        LCIMManager.getInstance().unreadCountLiveData.observe(this, Observer { updateTabDot() })
    }

    private fun updateTabDot() {
        val hasNotification = (mNotificationViewModel.unreadCount.value ?: 0) > 0
        val hasImMsg = LCIMManager.getInstance().unreadMessageCount > 0
        tb_me?.showDot(hasNotification || hasImMsg)

        val hasKefuMsg = (KefuManager.mUnreadCountLiveData.value ?: 0) > 0
        tb_doctor.showDot(hasKefuMsg)
    }

    override fun initData() {
        super.initData()
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

    fun setNavTabVisible(visible: Boolean) {
        if (visible) {
            nav_tab.isVisible = true
        } else {
            if (mCurrentPosition === TAB_0) {
                nav_tab.isVisible = false
            }
        }

    }

    override fun initBundle(bundle: Bundle) {
        if (KefuManager.isFromUnicorn(bundle)) {
            intent = Intent()   // 将intent清掉，以免从堆栈恢复时又打开客服窗口
            KefuManager.launchKefuActivity(this)
        } else {
            switchTabByBundle(bundle)
        }
    }

    private fun switchTabByBundle(bundle: Bundle) {
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
        if (position > 0) {
            nav_tab.isVisible = true
        }
        nav_tab.selectItem(position, true)
    }

    private fun showOpenNotificationDialogIfNeeded() {
        val previousShowTime = SPUtils.getInstance().getLong(SLEEP_RECORD_PREVIOUS_SHOW_NOTIFICATION_TIME, 0)
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
        SPUtils.getInstance().put(SLEEP_RECORD_PREVIOUS_SHOW_NOTIFICATION_TIME, System.currentTimeMillis())
    }

    override fun onBackPressed() {
        if (mCurrentPosition == TAB_0 && canWebViewGoBack()) {
        } else {
            returnToPhoneLauncher()
        }
    }

    private fun loadRequestUrl(url: String) {
        var fragment: Fragment?
        if (mH5Fragment == null) {
            mH5Fragment = supportFragmentManager.findFragmentByTag(H5HomepageFragment::class.java.simpleName) as H5HomepageFragment?
        }
        fragment = mH5Fragment
        if (fragment != null) {
            fragment?.loadRequestUrl(url)
        }
    }

    private fun getH5Url(): String {
        var fragment: Fragment?
        if (mH5Fragment == null) {
            mH5Fragment = supportFragmentManager.findFragmentByTag(H5HomepageFragment::class.java.simpleName) as H5HomepageFragment?
        }
        fragment = mH5Fragment
        if (fragment != null) {
            return fragment?.getH5Url()
        }
        return ""
    }

    private fun isCurrentH5HomeUrl(): Boolean {
        var fragment: Fragment?
        if (mH5Fragment == null) {
            mH5Fragment = supportFragmentManager.findFragmentByTag(H5HomepageFragment::class.java.simpleName) as H5HomepageFragment?
        }
        fragment = mH5Fragment
        if (fragment != null && fragment.isAdded) {
            return fragment?.isCurrentH5HomeUrl()
        }
        return true
    }

    private fun canWebViewGoBack(): Boolean {
        var fragment: Fragment?
        if (mH5Fragment == null) {
            mH5Fragment = supportFragmentManager.findFragmentByTag(H5HomepageFragment::class.java.simpleName) as H5HomepageFragment?
        }
        fragment = mH5Fragment
        if (fragment != null) {
            return fragment.goBack()
        }
        return false
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
            1 -> true
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
            0 -> {
                LogManager.appendUserOperationLog("首页Tab切换 -> $position 首页")
                StatUtil.trackBeginPage(this, StatConstants.page_home_tab)
                if (mIsResume) {
                    setNavTabVisible(isCurrentH5HomeUrl())
                }
            }
            1 -> {
                LogManager.appendUserOperationLog("首页Tab切换 -> $position 数据")
                StatUtil.trackBeginPage(this, StatConstants.page_data_tab)
                if (mIsResume) {
                    setNavTabVisible(true)
                }
            }
            2 -> {
                LogManager.appendUserOperationLog("首页Tab切换 -> $position 医生")
                StatUtil.trackBeginPage(this, StatConstants.page_doctor_tab)
                if (mIsResume) {
                    setNavTabVisible(true)
                }
            }
            3 -> {
                LogManager.appendUserOperationLog("首页Tab切换 -> $position 我的")
                StatUtil.trackBeginPage(this, StatConstants.page_me_tab)
                if (mIsResume) {
                    setNavTabVisible(true)
                }
            }
        }
    }

    private fun showFragmentByPosition(position: Int) {
        FragmentUtil.switchFragment(R.id.sd_main_fragment_container, supportFragmentManager!!, mFragmentTags, position,
                object : FragmentUtil.FragmentCreator {
                    override fun createFragmentByPosition(position: Int): Fragment {
                        return when (position) {
                            0 -> H5HomepageFragment()
                            1 -> DataFragment()
                            2 -> DoctorFragment()
                            3 -> MeFragment()
                            else -> H5HomepageFragment()
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
        mNotificationViewModel
                .updateUnreadCount()
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onChangeTabEvent(event: ChangeMainTabEvent) {
        EventBusUtil.removeStickyEvent(event)
        changeSelectTab(event.tabIndex)
    }
}
