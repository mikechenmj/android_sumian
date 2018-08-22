package com.sumian.hw.utils

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.sumian.sd.main.OnEnterListener

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/14 13:27
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class FragmentUtil {
    companion object {
        fun switchFragment(@IdRes containerViewId: Int, fragmentManager: FragmentManager,
                           tags: Array<String>, position: Int, fragmentCreator: FragmentCreator,
                           runOnCommitCallback: RunOnCommitCallback? = DefaultRunOnCommitCallbackImpl()) {
            var tag: String
            var selectFragment: Fragment
            var fragmentByTag: Fragment?
            for (i in 0 until tags.size) {
                tag = tags[i]
                fragmentByTag = fragmentManager.findFragmentByTag(tag)

                if (position == i) {
                    if (fragmentByTag == null) {
                        fragmentByTag = fragmentCreator.createFragmentByPosition(i)
                    }
                    selectFragment = fragmentByTag
                    val runOnCommitRunnable: () -> Unit = { runOnCommitCallback?.runOnCommit(selectFragment) }
                    if (fragmentByTag.isAdded) {
                        fragmentManager.beginTransaction().show(fragmentByTag).runOnCommit(runOnCommitRunnable).commit()
                    } else {
                        fragmentManager.beginTransaction().add(containerViewId, fragmentByTag, tag).runOnCommit(runOnCommitRunnable).commit()
                    }
                } else {
                    if (fragmentByTag != null) {
                        fragmentManager.beginTransaction().hide(fragmentByTag).commit()
                    }
                }
            }
        }
    }

    interface FragmentCreator {
        fun createFragmentByPosition(position: Int): Fragment
    }

    interface RunOnCommitCallback {
        fun runOnCommit(selectFragment: Fragment)
    }

    open class DefaultRunOnCommitCallbackImpl : FragmentUtil.RunOnCommitCallback {
        override fun runOnCommit(selectFragment: Fragment) {
            if (selectFragment is OnEnterListener) {
                (selectFragment as OnEnterListener).onEnter(null)
            }
        }
    }
}