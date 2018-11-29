package com.sumian.hw.utils

import androidx.annotation.IdRes
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

        @JvmStatic
        fun switchFragment(@IdRes containerViewId: Int, fragmentManager: androidx.fragment.app.FragmentManager,
                           tags: Array<String>, position: Int, fragmentCreator: FragmentCreator,
                           runOnCommitCallback: RunOnCommitCallback? = DefaultRunOnCommitCallbackImpl()) {
            var tag: String
            var selectFragment: androidx.fragment.app.Fragment
            var fragmentByTag: androidx.fragment.app.Fragment?
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
                        fragmentManager.beginTransaction().show(fragmentByTag).runOnCommit(runOnCommitRunnable).commitAllowingStateLoss()
                    } else {
                        fragmentManager.beginTransaction().add(containerViewId, fragmentByTag, tag).runOnCommit(runOnCommitRunnable).commitAllowingStateLoss()
                    }
                } else {
                    if (fragmentByTag != null) {
                        fragmentManager.beginTransaction().hide(fragmentByTag).commitAllowingStateLoss()
                    }
                }
            }
        }
    }

    interface FragmentCreator {
        fun createFragmentByPosition(position: Int): androidx.fragment.app.Fragment
    }

    interface RunOnCommitCallback {
        fun runOnCommit(selectFragment: androidx.fragment.app.Fragment)
    }

    open class DefaultRunOnCommitCallbackImpl : FragmentUtil.RunOnCommitCallback {
        override fun runOnCommit(selectFragment: androidx.fragment.app.Fragment) {
            if (selectFragment is OnEnterListener) {
                selectFragment.onEnter(null)
            }
        }
    }
}