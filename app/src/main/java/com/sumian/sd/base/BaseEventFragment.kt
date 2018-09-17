package com.sumian.sd.base

import com.sumian.common.base.BaseFragment
import com.sumian.sd.event.EventBusUtil

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/10 16:30
 *     desc   :
 *     version: 1.0
 * </pre>
 */
abstract class BaseEventFragment : BaseFragment() {

    override fun onStart() {
        super.onStart()
        if (openEventBus()) {
            EventBusUtil.register(this)
        }
    }

    override fun onStop() {
        super.onStop()
        if (openEventBus()) {
            EventBusUtil.unregister(this)
        }
    }

    protected open fun openEventBus(): Boolean {
        return false
    }
}