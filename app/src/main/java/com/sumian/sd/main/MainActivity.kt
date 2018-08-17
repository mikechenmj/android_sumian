package com.sumian.sd.main

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.hw.push.ReportPushManager
import com.sumian.hw.utils.FragmentUtil
import com.sumian.sd.R
import com.sumian.sd.app.App
import com.sumian.sd.base.BaseEventActivity
import com.sumian.sd.event.SwitchMainActivityEvent
import com.sumian.sd.main.widget.SwitchAnimationView
import com.sumian.sd.setting.version.delegate.VersionDelegate
import com.sumian.sd.utils.ColorCompatUtil
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.Subscribe

class MainActivity : BaseEventActivity() {

    private val mFragmentPositionHw = 0
    private val mFragmentPositionSd = 1
    private val mFragmentTags = arrayOf(HwMainFragment::class.java.name, SdMainFragment::class.java.name)
    private var mLaunchTabName: String? = TAB_HW_0
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
        MainTabHelper.mPendingTabName = mLaunchTabName
        when (mLaunchTabName) {
            TAB_HW_1 -> ReportPushManager.getInstance().setPushReportByUriStr(mLaunchTabData)
        }
    }

    override fun initWidget() {
        super.initWidget()
        showFragmentAccordingToData()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        showFragmentAccordingToData()
    }

    private fun showFragmentAccordingToData() {
        when (mLaunchTabName) {
            TAB_HW_0, TAB_HW_1, TAB_HW_2 -> showFragmentByPosition(mFragmentPositionHw)
            TAB_SD_0, TAB_SD_1, TAB_SD_2 -> showFragmentByPosition(mFragmentPositionSd)
        }
        mLaunchTabName = null
    }

    override fun onResume() {
        super.onResume()
        mVersionDelegate.checkVersion(this)
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
        switch_animation_view.startSwitchAnimation(this, startColor, endColor,
                startStatusBarColor, endStatusBarColor, isSwitchToHwFragment,
                object : SwitchAnimationView.AnimationListener {
                    override fun onFullScreenCovered() {
                        showFragmentByPosition(position)
                    }
                })
    }

    private fun showFragmentByPosition(position: Int) {
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
                })
    }
}
