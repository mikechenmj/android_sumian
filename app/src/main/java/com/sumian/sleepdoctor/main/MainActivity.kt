package com.sumian.sleepdoctor.main

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.hw.push.ReportPushManager
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.app.App
import com.sumian.sleepdoctor.base.BaseEventActivity
import com.sumian.sleepdoctor.event.SwitchMainActivityEvent
import com.sumian.sleepdoctor.main.widget.SwitchAnimationView
import com.sumian.sleepdoctor.utils.ColorCompatUtil
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.Subscribe

class MainActivity : BaseEventActivity() {

    private val mFragmentTagHw = HwMainFragment::class.java.name
    private val mFragmentTagSd = SdMainFragment::class.java.name
    private val mFragmentTags = arrayOf(mFragmentTagHw, mFragmentTagSd)
    private var mLaunchTabName: String? = TAB_HW_DEVICE
    private var mLaunchTabData: String? = null
    private val mDarkPrimaryColor: Int by lazy { ColorCompatUtil.getColor(this, R.color.hw_colorPrimary) }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    companion object {
        const val TAB_HW_DEVICE = "hw_device"
        const val TAB_HW_REPORT = "hw_report"
        const val TAB_HW_ME = "hw_me"
        const val TAB_SD_HOMEPAGE = "sd_homepage"
        const val TAB_SD_DOCTOR = "sd_doctor"
        const val TAB_SD_ME = "sd_me"

        private const val KEY_TAB_NAME = "key_tab_name"
        private const val KEY_TAB_DATA = "key_tab_data"

        fun launch(tabName: String, tabData: String? = null) {
            ActivityUtils.startActivity(getLaunchIntentForTab(tabName, tabData))
        }

        fun getLaunchIntentForHwPushReport(scheme: String): Intent {
            return getLaunchIntentForTab(TAB_HW_REPORT, scheme)
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
            TAB_HW_REPORT -> ReportPushManager.getInstance().setPushReportByUriStr(mLaunchTabData)
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
            TAB_HW_DEVICE, TAB_HW_REPORT, TAB_HW_ME -> showFragment(mFragmentTagHw)
            TAB_SD_HOMEPAGE, TAB_SD_DOCTOR, TAB_SD_ME -> showFragment(mFragmentTagSd)
        }
        mLaunchTabName = null
    }

    override fun openEventBus(): Boolean {
        return true
    }

    @Suppress("unused")
    @Subscribe
    fun onSwitchMainEvent(event: SwitchMainActivityEvent) {
        val isSwitchToHwFragment = event.type == SwitchMainActivityEvent.TYPE_HW_ACTIVITY
        val tag = if (isSwitchToHwFragment) mFragmentTagHw else mFragmentTagSd
        val startColor = if (isSwitchToHwFragment) Color.WHITE else mDarkPrimaryColor
        val endColor = if (isSwitchToHwFragment) mDarkPrimaryColor else Color.WHITE
        val startStatusBarColor = if (isSwitchToHwFragment) Color.WHITE else Color.TRANSPARENT
        val endStatusBarColor = if (isSwitchToHwFragment) Color.TRANSPARENT else Color.WHITE
        switch_animation_view.startSwitchAnimation(this, startColor, endColor,
                startStatusBarColor, endStatusBarColor, isSwitchToHwFragment,
                object : SwitchAnimationView.AnimationListener {
                    override fun onFullScreenCovered() {
                        showFragment(tag)
                    }
                })
    }

    private fun showFragment(targetTag: String) {
        for (tag in mFragmentTags) {
            var fragmentByTag = supportFragmentManager.findFragmentByTag(tag)
            if (fragmentByTag == null) {
                fragmentByTag = createFragmentByTag(tag)
            }
            if (tag == targetTag) {
                if (fragmentByTag.isAdded) {
                    supportFragmentManager.beginTransaction().show(fragmentByTag).commit()
                } else {
                    supportFragmentManager.beginTransaction().add(R.id.fl_content, fragmentByTag, targetTag).commit()
                }
            } else {
                supportFragmentManager.beginTransaction().hide(fragmentByTag).commit()
            }
        }
    }

    private fun createFragmentByTag(tag: String): Fragment {
        return when (tag) {
            mFragmentTagHw -> HwMainFragment()
            mFragmentTagSd -> SdMainFragment()
            else -> HwMainFragment()
        }
    }
}
