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
import com.sumian.sleepdoctor.utils.StatusBarUtil
import org.greenrobot.eventbus.Subscribe

class MainActivity : BaseEventActivity() {

    private val mFragmentTagHw = HwMainFragment::class.java.name
    private val mFragmentTagSd = SdMainFragment::class.java.name
    private val mFragmentTags = arrayOf(mFragmentTagHw, mFragmentTagSd)
    private var mLaunchTabName: String? = TAB_HW_DEVICE
    private var mLaunchTabData: String? = null

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

        private const val KEY_HW_PUSH_REPORT_SCHEME = "key_hw_push_report_scheme"
        private const val KEY_TAB_NAME = "key_tab_name"
        private const val KEY_TAB_DATA = "key_tab_data"

        fun launch(tabName: String, tabData: String? = null) {
            ActivityUtils.startActivity(getLaunchIntentForTab(tabName, tabData))
        }

        fun getLaunchIntentForHwPushReport(scheme: String): Intent {
            return getLaunchIntentForTab(TAB_HW_REPORT, scheme)
        }

        fun getLaunchIntentForTab(tabName: String, tabData: String? = null): Intent {
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

    @Subscribe
    fun onSwitchMainEvent(event: SwitchMainActivityEvent) {
        val tag = if (event.type == SwitchMainActivityEvent.TYPE_HW_ACTIVITY) mFragmentTagHw else mFragmentTagSd
        showFragment(tag)
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
                if (fragmentByTag is HwMainFragment) {
                    StatusBarUtil.setStatusBarColor(this, Color.TRANSPARENT, true)
                } else {
                    StatusBarUtil.setStatusBarColor(this, Color.WHITE, false)
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
