package com.sumian.sleepdoctor.main

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.base.BaseEventActivity
import com.sumian.sleepdoctor.event.SwitchMainActivityEvent
import com.sumian.sleepdoctor.utils.StatusBarUtil
import org.greenrobot.eventbus.Subscribe

class MainActivity : BaseEventActivity() {

    private val mFragmentTags = arrayOf("hw_fragment", "sd_fragment")

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun initWidget() {
        super.initWidget()
        showFragment(mFragmentTags[0])
    }

    override fun openEventBus(): Boolean {
        return true
    }

    @Subscribe
    fun onSwitchMainEvent(event: SwitchMainActivityEvent) {
        val tag = if (event.type == SwitchMainActivityEvent.TYPE_HW_ACTIVITY) mFragmentTags[0] else mFragmentTags[1]
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
            mFragmentTags[0] -> HwMainFragment()
            mFragmentTags[1] -> SdMainFragment()
            else -> HwMainFragment()
        }
    }
}
