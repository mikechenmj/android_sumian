package com.sumian.sd.base

import com.sumian.hw.base.HwBasePresenter
import com.sumian.sd.theme.three.base.SkinBaseFragment

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/10 16:30
 *     desc   :
 *     version: 1.0
 * </pre>
 */
abstract class BaseEventFragment<BasePresenter : HwBasePresenter> : SkinBaseFragment<BasePresenter>() {

    override fun onStart() {
        super.onStart()
        if (openEventBus()) {
            // EventBusUtil.register(this)
        }
    }

    override fun onStop() {
        super.onStop()
        if (openEventBus()) {
            //  EventBusUtil.unregister(this)
        }
    }

    override fun openEventBus(): Boolean {
        return false
    }
}