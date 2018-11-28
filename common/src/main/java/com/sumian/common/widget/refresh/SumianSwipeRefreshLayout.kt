package com.sumian.common.widget.refresh

import android.content.Context
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.util.AttributeSet

import com.sumian.common.R


/**
 * Created by jzz
 * on 2018/2/1.
 * desc:
 */

class SumianSwipeRefreshLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : androidx.swiperefreshlayout.widget.SwipeRefreshLayout(context, attrs) {
    private val mDismissRunnable = Runnable { this.hideRefreshAnim() }

    init {
        initView()
    }

    private fun initView() {
        this.setColorSchemeResources(R.color.b3_color, R.color.b7_color)
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

        private val DELAY_MILLS = 6 * 1000L
    }
}
