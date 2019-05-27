package com.sumian.sd.buz.diary

import android.view.View
import com.sumian.common.base.BaseFragment
import com.sumian.common.statistic.StatUtil
import com.sumian.common.utils.ColorCompatUtil
import com.sumian.device.manager.DeviceManager
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.devicemanager.AutoSyncDeviceDataUtil
import com.sumian.sd.buz.diary.event.ChangeDataFragmentTabEvent
import com.sumian.sd.buz.diary.monitorrecord.MonitorDataVpFragment
import com.sumian.sd.buz.diary.sleeprecord.SleepDiaryVpFragment
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.common.log.LogManager
import com.sumian.sd.common.utils.EventBusUtil
import com.sumian.sd.common.utils.FragmentUtil
import kotlinx.android.synthetic.main.fragment_data.*
import org.greenrobot.eventbus.Subscribe


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
        const val TAB_0 = 0
        const val TAB_1 = 1
        private var T0 = DataFragment::class.java.simpleName + ".TAB_0"
        private var T1 = DataFragment::class.java.simpleName + ".TAB_1"
        private var TAGS = arrayOf(T0, T1)
    }

    private var mCurrentPosition = -1

    override fun initWidget() {
        super.initWidget()
        tab_sleep_diary.setOnClickListener { selectTab(0) }
        tab_monitor_data.setOnClickListener { selectTab(1) }
        selectTab(0, true)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            val findFragmentByTag = fragmentManager?.findFragmentByTag(TAGS[mCurrentPosition])
            if (findFragmentByTag is MonitorDataVpFragment) {
                findFragmentByTag.updateCurrentTimeData()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        EventBusUtil.register(this)
        switchTabUI(DeviceManager.hasBoundDevice() || AppManager.getAccountViewModel().liveDataToken.value?.user?.hasDevice() == true)
    }

    override fun onStop() {
        super.onStop()
        EventBusUtil.unregister(this)
    }

    private fun switchTabUI(hasDevice: Boolean) {
        vg_tabs.visibility = if (hasDevice) View.VISIBLE else View.GONE
        tv_sleep_diary.visibility = if (!hasDevice) View.VISIBLE else View.GONE
    }

    private fun switchFragment(position: Int) {
        FragmentUtil.switchFragment(R.id.fl_container, fragmentManager!!, TAGS, position, object : FragmentUtil.FragmentCreator {
            override fun createFragmentByPosition(position: Int): androidx.fragment.app.Fragment {
                return when (position) {
                    0 -> {
                        StatUtil.trackBeginPage(activity!!, StatConstants.page_data_page_sleep_data_tab)
                        StatUtil.event(StatConstants.enter_sleep_diary_detail_tab)
                        SleepDiaryVpFragment()
                    }
                    else -> {
                        StatUtil.trackBeginPage(activity!!, StatConstants.page_data_page_monitor_data_tab)
                        MonitorDataVpFragment()
                    }

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
                AutoSyncDeviceDataUtil.autoSyncSleepData()
                LogManager.appendUserOperationLog("点击TAB '监测数据'")
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

    @Subscribe(sticky = true)
    fun onChangeTabEvent(event: ChangeDataFragmentTabEvent) {
        EventBusUtil.removeStickyEvent(event)
        selectTab(event.tabIndex)
    }
}