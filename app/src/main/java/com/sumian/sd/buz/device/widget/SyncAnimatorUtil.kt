package com.sumian.sd.buz.device.widget

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/15 18:38
 * desc   :
 * version: 1.0
 */
object SyncAnimatorUtil {
    fun createSyncRotateAnimator(rotateView: View): ObjectAnimator? {
        val animator = ObjectAnimator.ofFloat(rotateView, "rotation", 0f, 360f)
        animator?.duration = 2000
        animator?.repeatCount = ValueAnimator.INFINITE
        animator?.repeatMode = ValueAnimator.RESTART
        animator?.interpolator = null
        return animator
    }
}