package com.sumian.sd.base

import android.app.Activity
import com.sumian.common.base.IActivityDelegate
import com.sumian.common.base.IActivityDelegateFactory

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/8 17:59
 * desc   :
 * version: 1.0
 */
class ActivityDelegateFactory : IActivityDelegateFactory {
    override fun createActivityDelegate(activity: Activity): IActivityDelegate {
        return BaseActivityDelegate(activity)
    }
}