package com.sumian.sleepdoctor.improve.advisory.fragment

import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.base.BaseFragment
import com.sumian.sleepdoctor.base.BasePresenter

/**
 *
 *Created by sm
 * on 2018/6/4 17:32
 * desc:
 **/
class UnusedAdvisoryFragment : BaseFragment<BasePresenter<Any>>() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_main_unused_advisory
    }
}