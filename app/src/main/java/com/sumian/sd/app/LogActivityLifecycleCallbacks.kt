package com.sumian.sd.app

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.sumian.common.lifecycle.EmptyActivityLifecycleCallbacks
import com.sumian.sd.common.log.SdLogManager

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/7 14:00
 * desc   : Log Activity and Fragment open and close
 * version: 1.0
 */
class LogActivityLifecycleCallbacks : EmptyActivityLifecycleCallbacks() {
    companion object {
        fun getClassSimpleName(any: Any?) = any?.javaClass?.simpleName ?: ""
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        super.onActivityCreated(activity, savedInstanceState)
        activity?.let {
            SdLogManager.logPage(getClassSimpleName(activity), true)
            registerFragmentLifecycleCallbacks(activity)
        }
    }

    override fun onActivityDestroyed(activity: Activity?) {
        activity?.let {
            SdLogManager.logPage(getClassSimpleName(activity), false)
        }
        super.onActivityDestroyed(activity)
    }

    private fun registerFragmentLifecycleCallbacks(activity: Activity) {
        if (activity is FragmentActivity) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(LogFragmentLifecycleCallbacks(), true)
        }
    }

    /**
     * Log Fragment open and close
     */
    class LogFragmentLifecycleCallbacks : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
            super.onFragmentCreated(fm, f, savedInstanceState)
            SdLogManager.logPage(getClassSimpleName(f), true)
        }

        override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
            SdLogManager.logPage(getClassSimpleName(f), false)
            super.onFragmentDestroyed(fm, f)
        }
    }
}