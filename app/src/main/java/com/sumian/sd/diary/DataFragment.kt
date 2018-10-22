package com.sumian.sd.diary

import android.arch.lifecycle.Observer
import android.support.v4.app.Fragment
import android.view.View
import com.sumian.common.base.BaseFragment
import com.sumian.common.utils.ColorCompatUtil
import com.sumian.hw.log.LogManager
import com.sumian.hw.utils.FragmentUtil
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.device.DeviceManager
import com.sumian.sd.diary.monitorrecord.DailyReportFragmentV2
import com.sumian.sd.diary.sleeprecord.SleepRecordFragment
import kotlinx.android.synthetic.main.fragment_data.*


/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/10 20:03
 * desc   :
 * version: 1.0
 */
class DataFragment : BaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_data
    }

    companion object {
        private var T0 = DataFragment::class.java.simpleName + ".TAB_0"
        private var T1 = DataFragment::class.java.simpleName + ".TAB_1"
        private var TAGS = arrayOf(T0, T1)
    }

    private var mCurrentPosition = -1

    override fun initWidget() {
        super.initWidget()
        DeviceManager.getMonitorLiveData().observe(this, Observer {
            val hasDevice = it != null || AppManager.getAccountViewModel().liveDataToken.value?.user?.hasDevice() == true
            switchTabUI(hasDevice)
        })
        tab_sleep_diary.setOnClickListener { selectTab(0) }
        tab_monitor_data.setOnClickListener { selectTab(1) }
        selectTab(0, true)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            val findFragmentByTag = fragmentManager?.findFragmentByTag(TAGS[mCurrentPosition])
            if (findFragmentByTag is DailyReportFragmentV2) {
                findFragmentByTag.updateCurrentTimeData()
            }
        }
    }

    private fun switchTabUI(hasDevice: Boolean) {
        vg_tabs.visibility = if (hasDevice) View.VISIBLE else View.GONE
        tv_sleep_diary.visibility = if (!hasDevice) View.VISIBLE else View.GONE
    }

    private fun switchFragment(position: Int) {
        FragmentUtil.switchFragment(R.id.fl_container, fragmentManager!!, TAGS, position, object : FragmentUtil.FragmentCreator {
            override fun createFragmentByPosition(position: Int): Fragment {
                return when (position) {
                    0 -> SleepRecordFragment()
                    else -> DailyReportFragmentV2()
                }
            }
        })
    }

    private fun selectTab(position: Int, isInit: Boolean = false) {
        updateTopTabUI(position)
        switchFragment(position)
        if (!isInit) {
            if (position == 0) {
                LogManager.appendUserOperationLog("点击TAB '睡眠日记'")
            } else {
                LogManager.appendUserOperationLog("点击TAB '检测数据'")
            }
        }
        mCurrentPosition = position
    }

    private fun updateTopTabUI(position: Int) {
        tab_sleep_diary.setTextColor(ColorCompatUtil.getColor(activity!!, if (position == 0) R.color.white else R.color.white_50))
        tab_monitor_data.setTextColor(ColorCompatUtil.getColor(activity!!, if (position != 0) R.color.white else R.color.white_50))
        v_line_0.visibility = if (position == 0) View.VISIBLE else View.GONE
        v_line_1.visibility = if (position != 0) View.VISIBLE else View.GONE
    }
}