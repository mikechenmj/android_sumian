package com.sumian.sd.diary

import android.arch.lifecycle.Observer
import android.support.v4.app.Fragment
import android.view.View
import com.sumian.common.base.BaseFragment
import com.sumian.common.utils.ColorCompatUtil
import com.sumian.hw.utils.FragmentUtil
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.device.DeviceManager
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
        private var TAGS = arrayOf(T0, T1)
    }

    private var mCurrentPosition = -1;

    override fun initWidget() {
        super.initWidget()
        DeviceManager.getMonitorLiveData().observe(this, Observer {
            val hasDevice = it != null || AppManager.getAccountViewModel().liveDataToken.value?.user?.hasDevice() == true
            switchTabs(hasDevice)
        })
        tab_sleep_diary.setOnClickListener { selectTab(0) }
        tab_monitor_data.setOnClickListener { selectTab(1) }
        selectTab(0)
    }

    private fun switchTabs(hasDevice: Boolean) {
        vg_tabs.visibility = if (hasDevice) View.VISIBLE else View.GONE
        tv_sleep_diary.visibility = if (!hasDevice) View.VISIBLE else View.GONE
    }

    private fun switchFragment(position: Int) {
        FragmentUtil.switchFragment(R.id.fl_container, fragmentManager!!, TAGS, position,object:FragmentUtil.FragmentCreator{
            override fun createFragmentByPosition(position: Int): Fragment {
                return when (position) {
                    0 -> SleepRecordFragment()
                    else -> DailyReportFragmentV2()
                }
            }
        })
    }

    private fun selectTab(position: Int) {
        if (mCurrentPosition == position) {
            return
        }
        updateTopTabUI(position)
        switchFragment(position)
        mCurrentPosition = position
    }

    private fun updateTopTabUI(position: Int) {
        tab_sleep_diary.setTextColor(ColorCompatUtil.getColor(activity!!, if (position == 0) R.color.white else R.color.white_50))
        tab_monitor_data.setTextColor(ColorCompatUtil.getColor(activity!!, if (position != 0) R.color.white else R.color.white_50))
        v_line_0.visibility = if (position == 0) View.VISIBLE else View.GONE
        v_line_1.visibility = if (position != 0) View.VISIBLE else View.GONE
    }
}