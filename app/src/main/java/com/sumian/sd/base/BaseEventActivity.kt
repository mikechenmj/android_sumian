package com.sumian.sd.base

import com.sumian.sd.event.EventBusUtil
import com.sumian.sd.theme.three.base.SkinBaseActivity


/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/10 17:30
 *     desc   :
 *     version: 1.0
 * </pre>
 */
abstract class BaseEventActivity : SkinBaseActivity() {
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