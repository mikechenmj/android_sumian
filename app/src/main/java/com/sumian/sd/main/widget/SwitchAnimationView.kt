package com.sumian.sd.main.widget

import android.animation.*
import android.app.Activity
import android.content.Context
import android.support.v4.graphics.ColorUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.FrameLayout
import com.blankj.utilcode.util.ScreenUtils
import com.sumian.sd.R
import com.sumian.sd.utils.StatusBarUtil
import kotlinx.android.synthetic.main.view_switch_animation.view.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/8/13 20:52
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class SwitchAnimationView(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet) {
    private val vBg = this

    init {
        LayoutInflater.from(context).inflate(R.layout.view_switch_animation, this, true)
    }

    fun startSwitchAnimation(activity: Activity, startBgColor: Int, endBgColor: Int,
                             startStatusBarColor: Int, endStatusBarColor: Int, isEndStatusBarColorDark: Boolean,
                             animationListener: AnimationListener? = null) {
        val screenWidth = ScreenUtils.getScreenWidth()
        val screenHeight = ScreenUtils.getScreenHeight()
        val centerX = screenWidth
        val centerY = screenHeight
        val minRadius = 0f
        val maxRadius = screenHeight.toFloat() * 1.2f
        vBg.visibility = View.VISIBLE
        vBg.setBackgroundColor(startBgColor)
        val scaleBigAnimator = ViewAnimationUtils.createCircularReveal(vBg, centerX, centerY, minRadius, maxRadius)
        scaleBigAnimator.duration = 800
        val colorAnimator = ValueAnimator.ofFloat(0f, 1f)
        colorAnimator.duration = 680
        colorAnimator.addUpdateListener {
            val fraction = it.animatedValue as Float
            val bgColor = evaluate(fraction, startBgColor, endBgColor)
            val statusColor = evaluate(fraction, startStatusBarColor, endStatusBarColor)
            vBg.setBackgroundColor(bgColor)
            StatusBarUtil.setStatusBarColor(activity, statusColor, if (fraction > 0.5f) isEndStatusBarColorDark else !isEndStatusBarColorDark)
        }
        val scaleSmallAnimator = ViewAnimationUtils.createCircularReveal(vBg, centerX, centerY, maxRadius, minRadius)
        scaleSmallAnimator.duration = 520
        val animatorSet = AnimatorSet()
        scaleBigAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                animationListener?.onFullScreenCovered()
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }
        })
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                vBg.visibility = View.GONE
                animationListener?.onAnimationEnded()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
                val logoVisibleAnimator = ObjectAnimator.ofFloat(iv_logo, "alpha", 0f, 1f)
                logoVisibleAnimator.startDelay = 400
                logoVisibleAnimator.duration = 200
                val logoGoneAnimator = ObjectAnimator.ofFloat(iv_logo, "alpha", 1f, 0f)
                logoGoneAnimator.startDelay = 980
                logoGoneAnimator.duration = 200
                val logoAnimatorSet = AnimatorSet()
                logoAnimatorSet.playSequentially(logoVisibleAnimator, logoGoneAnimator)
                logoAnimatorSet.start()
            }
        })
        animatorSet.playSequentially(scaleBigAnimator, colorAnimator, scaleSmallAnimator)
        animatorSet.start()
    }

    interface AnimationListener {
        fun onFullScreenCovered()
        fun onAnimationEnded() {}
    }

    /**
     * Copy from android.support.graphics.drawable.ArgbEvaluator since ArgbEvaluator is @RestrictTo(LIBRARY_GROUP)
     */
    private fun evaluate(fraction: Float, startValue: Any, endValue: Any): Int {
        val startInt = startValue as Int
        val startA = (startInt shr 24 and 0xff) / 255.0f
        var startR = (startInt shr 16 and 0xff) / 255.0f
        var startG = (startInt shr 8 and 0xff) / 255.0f
        var startB = (startInt and 0xff) / 255.0f

        val endInt = endValue as Int
        val endA = (endInt shr 24 and 0xff) / 255.0f
        var endR = (endInt shr 16 and 0xff) / 255.0f
        var endG = (endInt shr 8 and 0xff) / 255.0f
        var endB = (endInt and 0xff) / 255.0f

        // convert from sRGB to linear
        startR = Math.pow(startR.toDouble(), 2.2).toFloat()
        startG = Math.pow(startG.toDouble(), 2.2).toFloat()
        startB = Math.pow(startB.toDouble(), 2.2).toFloat()

        endR = Math.pow(endR.toDouble(), 2.2).toFloat()
        endG = Math.pow(endG.toDouble(), 2.2).toFloat()
        endB = Math.pow(endB.toDouble(), 2.2).toFloat()

        // compute the interpolated color in linear space
        var a = startA + fraction * (endA - startA)
        var r = startR + fraction * (endR - startR)
        var g = startG + fraction * (endG - startG)
        var b = startB + fraction * (endB - startB)

        // convert back to sRGB in the [0..255] range
        a *= 255.0f
        r = Math.pow(r.toDouble(), 1.0 / 2.2).toFloat() * 255.0f
        g = Math.pow(g.toDouble(), 1.0 / 2.2).toFloat() * 255.0f
        b = Math.pow(b.toDouble(), 1.0 / 2.2).toFloat() * 255.0f

        return Math.round(a) shl 24 or (Math.round(r) shl 16) or (Math.round(g) shl 8) or Math.round(b)
    }
}