package com.sumian.sleepdoctor.base

import com.sumian.common.base.BaseActivity
import com.sumian.sleepdoctor.event.EventBusUtil


/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/10 17:30
 *     desc   :
 *     version: 1.0
 * </pre>
 */
abstract class BaseEventActivity : BaseActivity() {
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