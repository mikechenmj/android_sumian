package com.sumian.sleepdoctor.cbti.fragment

import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.base.BaseFragment
import com.sumian.sleepdoctor.base.BasePresenter

/**
 * Created by sm
 *
 * on 2018/7/11
 *
 * desc: CBTI 练习tab
 *
 */
class ExerciseFragment : BaseFragment<BasePresenter<*>>() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_tab_practice
    }
}