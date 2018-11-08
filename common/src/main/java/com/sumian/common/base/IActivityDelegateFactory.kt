package com.sumian.common.base

import android.app.Activity

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/8 17:45
 * desc   :
 * version: 1.0
 */
interface IActivityDelegateFactory {
    fun createActivityDelegate(activity: Activity): IActivityDelegate
}