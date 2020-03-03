package com.sumian.sd.buz.diary

import com.sumian.common.base.BaseFragment
import com.sumian.sd.R
import com.sumian.sd.buz.diary.monitorrecord.MonitorDataVpFragment


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

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            val findFragmentByTag = fragmentManager?.findFragmentById(R.id.device_monitor_data_card_view)
            if (findFragmentByTag is MonitorDataVpFragment) {
                findFragmentByTag.updateCurrentTimeData()
            }
        }
    }
}