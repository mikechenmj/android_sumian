package com.sumian.sd.main

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import com.sumian.common.utils.SettingsUtil
import com.sumian.hw.push.ReportPushManager
import com.sumian.hw.utils.FragmentUtil
import com.sumian.sd.R
import com.sumian.sd.app.App
import com.sumian.sd.base.BaseEventActivity
import com.sumian.sd.constants.SpKeys
import com.sumian.sd.event.EventBusUtil
import com.sumian.sd.event.SwitchMainActivityEvent
import com.sumian.sd.main.widget.SwitchAnimationView
import com.sumian.sd.notification.NotificationListActivity.REQUEST_CODE_OPEN_NOTIFICATION
import com.sumian.sd.setting.version.delegate.VersionDelegate
import com.sumian.sd.theme.three.loader.SkinManager
import com.sumian.sd.utils.ColorCompatUtil
import com.sumian.sd.utils.NotificationUtil
import com.sumian.sd.widget.dialog.SumianAlertDialog
import com.sumian.sd.widget.dialog.theme.BlackTheme
import com.sumian.sd.widget.dialog.theme.ITheme
import com.sumian.sd.widget.dialog.theme.LightTheme
import com.sumian.sd.widget.dialog.theme.ThemeFactory
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.Subscribe

class MainActivity : BaseEventActivity() {

    private val mFragmentPositionHw = 0
    private val mFragmentPositionSd = 1
    private val mFragmentTags = arrayOf(HwMainFragment::class.java.name, SdMainFragment::class.java.name)
    private var mLaunchTabName: String? = TAB_HW_0
    //private var mLaunchTabName: String? = if (BuildConfig.DEBUG) TAB_SD_0 else TAB_HW_0
    private var mLaunchTabData: String? = null

    private val mDarkPrimaryColor: Int by lazy {
        ColorCompatUtil.getColor(this, R.color.hw_colorPrimary)
    }

    var mIsBlackTheme = true

    private val mVersionDelegate: VersionDelegate  by lazy {
        VersionDelegate.init()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    companion object {
        const val TAB_HW_0 = "TAB_HW_0"
        const val TAB_HW_1 = "TAB_HW_1"
        const val TAB_HW_2 = "TAB_HW_2"
        const val TAB_SD_0 = "TAB_SD_0"
        const val TAB_SD_1 = "TAB_SD_1"
        const val TAB_SD_2 = "TAB_SD_2"

        private const val KEY_TAB_NAME = "key_tab_name"
        private const val KEY_TAB_DATA = "key_tab_data"

        fun launch(tabName: String, tabData: String? = null) {
            ActivityUtils.startActivity(getLaunchIntentForTab(tabName, tabData))
        }

        fun getLaunchIntentForHwPushReport(scheme: String): Intent {
            return getLaunchIntentForTab(TAB_HW_1, scheme)
        }

        private fun getLaunchIntentForTab(tabName: String, tabData: String? = null): Intent {
            val intent = Intent(App.getAppContext(), MainActivity::class.java)
            intent.putExtra(KEY_TAB_NAME, tabName)
            intent.putExtra(KEY_TAB_DATA, tabData)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun initBundle(bundle: Bundle) {
        mLaunchTabName = bundle.getString(KEY_TAB_NAME)
        mLaunchTabData = bundle.getString(KEY_TAB_DATA)
        when (mLaunchTabName) {
            TAB_HW_1 -> ReportPushManager.getInstance().setPushReportByUriStr(mLaunchTabData)
        }
    }

    override fun initWidget() {
        super.initWidget()
        mVersionDelegate.checkVersion(this)
        showFragmentAccordingToData()
        showOpenNotificationDialogIfNeeded()
        showUserGuidDialogIfNeed()
        //目前因为默认launch 的是黑色主题  所以必须加这行.保证app 每次初始化都是黑色
        SkinManager.getInstance().nightMode()
    }

    private fun showUserGuidDialogIfNeed() {
        val hasShow = SPUtils.getInstance().getBoolean(SpKeys.HOME_PAGE_FIRST_LAUNCH_GUIDE_DIALOG_HAS_SHOWN, false)
        if (hasShow) {
            return
        }
        HomepageUserGuidDialog(this, View.OnClickListener {
            EventBusUtil.postEvent(SwitchMainActivityEvent(SwitchMainActivityEvent.TYPE_SD_ACTIVITY))
        })
                .show()
        SPUtils.getInstance().put(SpKeys.HOME_PAGE_FIRST_LAUNCH_GUIDE_DIALOG_HAS_SHOWN, true)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        showFragmentAccordingToData()
    }

    override fun openEventBus(): Boolean {
        return true
    }

    @Suppress("unused")
    @Subscribe
    fun onSwitchMainEvent(event: SwitchMainActivityEvent) {
        val isSwitchToHwFragment = event.type == SwitchMainActivityEvent.TYPE_HW_ACTIVITY
        val position = if (isSwitchToHwFragment) mFragmentPositionHw else mFragmentPositionSd
        val startColor = if (isSwitchToHwFragment) Color.WHITE else mDarkPrimaryColor
        val endColor = if (isSwitchToHwFragment) mDarkPrimaryColor else Color.WHITE
        val startStatusBarColor = if (isSwitchToHwFragment) Color.WHITE else Color.TRANSPARENT
        val endStatusBarColor = if (isSwitchToHwFragment) Color.TRANSPARENT else Color.WHITE
        val subFragmentName = if (isSwitchToHwFragment) TAB_HW_0 else TAB_SD_0
        switch_animation_view.startSwitchAnimation(this, startColor, endColor,
                startStatusBarColor, endStatusBarColor, isSwitchToHwFragment,
                object : SwitchAnimationView.AnimationListener {
                    override fun onFullScreenCovered() {
                        if (isSwitchToHwFragment) {
                            SkinManager.getInstance().nightMode()
                        } else {
                            SkinManager.getInstance().restoreDefaultTheme()
                        }
                        showFragmentByPosition(position, subFragmentName)
                    }
                })
    }

    private fun showFragmentByPosition(position: Int, subFragmentName: String? = null) {
        this.mIsBlackTheme = (position == mFragmentPositionHw)
        FragmentUtil.switchFragment(R.id.main_fragment_container, supportFragmentManager!!, mFragmentTags, position,
                object : FragmentUtil.FragmentCreator {
                    override fun createFragmentByPosition(position: Int): Fragment {
                        return when (position) {
                            mFragmentPositionHw -> HwMainFragment()
                            mFragmentPositionSd -> SdMainFragment()
                            else -> throw RuntimeException("Illegal tab position")
                        }
                    }
                },
                object : FragmentUtil.RunOnCommitCallback {
                    override fun runOnCommit(selectFragment: Fragment) {
                        if (selectFragment is OnEnterListener) {
                            (selectFragment as OnEnterListener).onEnter(subFragmentName)
                        }
                    }
                })
    }

    private fun showOpenNotificationDialogIfNeeded() {
        val previousShowTime = SPUtils.getInstance().getLong(SpKeys.SLEEP_RECORD_PREVIOUS_SHOW_NOTIFICATION_TIME, 0)
        val alreadyShowed = previousShowTime > 0

        if (NotificationUtil.areNotificationsEnabled(this@MainActivity) || alreadyShowed) {
            return
        }

        SumianAlertDialog(this@MainActivity)
                .setCloseIconVisible(true)
                .setTheme(createTheme())
                .setTopIconResource(R.mipmap.ic_notification_alert)
                .setTitle(R.string.open_notification)
                .setMessage(R.string.open_notification_and_receive_doctor_response)
                .setRightBtn(R.string.open_notification) { SettingsUtil.launchSettingActivityForResult(this, REQUEST_CODE_OPEN_NOTIFICATION) }
                .show()
        SPUtils.getInstance().put(SpKeys.SLEEP_RECORD_PREVIOUS_SHOW_NOTIFICATION_TIME, System.currentTimeMillis())
    }

    private fun createTheme(): ITheme {
        return if (mIsBlackTheme)
            ThemeFactory.create(BlackTheme::class.java)
        else
            ThemeFactory.create(LightTheme::class.java)
    }

    private fun showFragmentAccordingToData() {
        when (mLaunchTabName) {
            TAB_HW_0, TAB_HW_1, TAB_HW_2 -> showFragmentByPosition(mFragmentPositionHw, mLaunchTabName)
            TAB_SD_0, TAB_SD_1, TAB_SD_2 -> showFragmentByPosition(mFragmentPositionSd, mLaunchTabName)
        }
        mLaunchTabName = null
    }
}
