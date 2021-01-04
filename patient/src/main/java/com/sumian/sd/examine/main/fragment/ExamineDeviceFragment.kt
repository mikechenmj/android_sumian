package com.sumian.sd.examine.main.fragment

import com.sumian.common.base.BaseFragment
import com.sumian.sd.R
import com.sumian.sd.main.OnEnterListener

class ExamineDeviceFragment: BaseFragment(), OnEnterListener {

    override fun getLayoutId(): Int {
        return R.layout.examine_device_fragment
    }

    override fun onEnter(data: String?) {
    }
}