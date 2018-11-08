package com.sumian.common.base

import android.app.Activity

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/8 17:40
 * desc   :
 * version: 1.0
 */
object BaseActivityManager {
    private var mActivityDelegateFactory: IActivityDelegateFactory? = null

    fun setActivityDelegateFactory(factory: IActivityDelegateFactory) {
        mActivityDelegateFactory = factory
    }

    fun createActivityDelegate(activity: Activity): IActivityDelegate {
        return mActivityDelegateFactory?.createActivityDelegate(activity)
                ?: createEmptyActivityDelegate()
    }

    private fun createEmptyActivityDelegate(): IActivityDelegate {
        return object : IActivityDelegate {}
    }
}