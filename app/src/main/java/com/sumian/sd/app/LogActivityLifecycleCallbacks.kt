package com.sumian.sd.app

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.sumian.common.lifecycle.EmptyActivityLifecycleCallbacks
import com.sumian.sd.log.SdLogManager

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/7 14:00
 * desc   :
 * version: 1.0
 */
class LogActivityLifecycleCallbacks : EmptyActivityLifecycleCallbacks() {
    companion object {
        fun getClassSimpleName(any: Any?) = any?.javaClass?.simpleName ?: ""
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        super.onActivityCreated(activity, savedInstanceState)
        SdLogManager.logPage(getClassSimpleName(activity), true)
        registerFragmentLifecycleCallbacks(activity)
    }

    override fun onActivityDestroyed(activity: Activity?) {
        super.onActivityDestroyed(activity)
        SdLogManager.logPage(getClassSimpleName(activity), false)
    }

    private fun registerFragmentLifecycleCallbacks(activity: Activity?) {
        if (activity != null && activity is FragmentActivity) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(LogFragmentLifecycleCallbacks(), true)
        }
    }

    class LogFragmentLifecycleCallbacks : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
            super.onFragmentCreated(fm, f, savedInstanceState)
            SdLogManager.logPage(getClassSimpleName(f), true)
        }

        override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
            super.onFragmentDestroyed(fm, f)
            SdLogManager.logPage(getClassSimpleName(f), false)
        }
    }
}