package com.sumian.common.widget.refresh

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sumian.common.R


/**
 * Created by jzz
 * on 2018/2/1.
 * desc:
 */

@Suppress("DEPRECATION")
open class SumianSwipeRefreshLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : SwipeRefreshLayout(context, attrs) {

    private val mDismissRunnable = Runnable { this.hideRefreshAnim() }

    init {
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.SumianSwipeRefreshLayout)
        val progressColor = attributes.getColor(R.styleable.SumianSwipeRefreshLayout_ssrl_progress_color, resources.getColor(R.color.b3_color))
        val progressBgColor = attributes.getColor(R.styleable.SumianSwipeRefreshLayout_ssrl_progress_bg_color, resources.getColor(R.color.b2_color))
        attributes.recycle()
        setColorSchemeColors(progressColor)
        setProgressBackgroundColorSchemeColor(progressBgColor)
    }

    fun showRefreshAnim() {
        isRefreshing = true
        removeCallbacks(mDismissRunnable)
        postDelayed(mDismissRunnable, DELAY_MILLS)
    }

    fun hideRefreshAnim() {
        removeCallbacks(mDismissRunnable)
        isRefreshing = false
    }

    companion object {

        private const val DELAY_MILLS = 6 * 1000L
    }
}
