package com.sumian.sd.diary

import android.arch.lifecycle.Observer
import android.support.v4.app.Fragment
import android.view.View
import com.sumian.common.base.BaseFragment
import com.sumian.common.utils.ColorCompatUtil
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.diary.monitorrecord.DailyReportFragmentV2
import com.sumian.sd.diary.sleeprecord.SleepRecordFragment
import kotlinx.android.synthetic.main.fragment_diary.*


/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/10 20:03
 * desc   :
 * version: 1.0
 */
class DiaryFragment : BaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_diary
    }

    companion object {
        private var T0 = DiaryFragment::class.java.simpleName + ".TAB_0"
        private var T1 = DiaryFragment::class.java.simpleName + ".TAB_1"
    }

    override fun initWidget() {
        super.initWidget()
        AppManager.getAccountViewModel().liveDataToken.observe(this, Observer {
            val hasDevice = it?.user?.hasDevice() == true
            vg_tabs.visibility = if (hasDevice) View.VISIBLE else View.GONE
            tv_sleep_diary.visibility = if (!hasDevice) View.VISIBLE else View.GONE
        })
        tab_sleep_diary.setOnClickListener { selectTab(0) }
        tab_monitor_data.setOnClickListener { selectTab(1) }
        selectTab(0)
    }

    private fun switchFragment(tag: String) {
        fragmentManager!!.beginTransaction().replace(R.id.fl_container, getFragment(tag)).commit()
    }

    private fun getFragment(tag: String): Fragment {
        var fragmentByTag = fragmentManager!!.findFragmentByTag(tag)
        if (fragmentByTag == null) {
            fragmentByTag = when (tag) {
                T0 -> SleepRecordFragment()
                else -> DailyReportFragmentV2()
            }
        }
        return fragmentByTag
    }

    private fun selectTab(position: Int) {
        tab_sleep_diary.setTextColor(ColorCompatUtil.getColor(activity!!, if (position == 0) R.color.white else R.color.white_50))
        tab_monitor_data.setTextColor(ColorCompatUtil.getColor(activity!!, if (position != 0) R.color.white else R.color.white_50))
        v_line_0.visibility = if (position == 0) View.VISIBLE else View.GONE
        v_line_1.visibility = if (position != 0) View.VISIBLE else View.GONE
        switchFragment(if (position == 0) T0 else T1)
    }
}