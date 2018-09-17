package com.sumian.hw.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.sumian.sd.R
import kotlinx.android.synthetic.main.hw_lay_tab_indicator_view.view.*

/**
 * Created by sm
 * on 2018/3/6.
 * desc:
 */

class TabIndicatorView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr), TabIndicatorItemView.OnSelectTabCallback {

    private var mOnSwitchIndicatorCallback: OnSwitchIndicatorCallback? = null

    companion object {
        private val TAG = TabIndicatorView::class.java.simpleName
    }

    init {
        initView(context)
    }

    fun setOnSwitchIndicatorCallback(onSwitchIndicatorCallback: OnSwitchIndicatorCallback) {
        mOnSwitchIndicatorCallback = onSwitchIndicatorCallback
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.hw_lay_tab_indicator_view, this)

        day_tab_indicator_item_view.setIndicatorText("日")
        day_tab_indicator_item_view.setOnSelectTabCallback(this)
        day_tab_indicator_item_view.select()
        week_tab_indicator_item_view.setIndicatorText("周")
        week_tab_indicator_item_view.setOnSelectTabCallback(this)
        week_tab_indicator_item_view.unSelect()
        calendar_tab_indicator_item_view.setCalendarIcon(R.mipmap.report_calendar)
        calendar_tab_indicator_item_view.setOnSelectTabCallback(this)
    }

    fun selectTabByPosition(position: Int) {
        when (position) {
            0 -> onSelect(day_tab_indicator_item_view, true)
            1 -> onSelect(week_tab_indicator_item_view, true)
            else -> {
            }
        }
    }

    override fun onSelect(v: View, isSelect: Boolean) {
        var position = 0
        val i = v.id
        if (i == R.id.day_tab_indicator_item_view) {
            week_tab_indicator_item_view.unSelect()
            position = 0
            if (mOnSwitchIndicatorCallback != null) {
                mOnSwitchIndicatorCallback!!.onSwitchIndicator(v, position)
            }

        } else if (i == R.id.week_tab_indicator_item_view) {
            day_tab_indicator_item_view.unSelect()
            position = 1
            if (mOnSwitchIndicatorCallback != null) {
                mOnSwitchIndicatorCallback!!.onSwitchIndicator(v, position)
            }

        } else if (i == R.id.calendar_tab_indicator_item_view) {
            if (mOnSwitchIndicatorCallback != null) {
                mOnSwitchIndicatorCallback!!.onShowCalendar(v)
            }

        }
        updateTabsUiBySelectPosition(position)
    }

    fun updateTabsUiBySelectPosition(position: Int) {
        when (position) {
            0 -> {
                calendar_tab_indicator_item_view.visibility = View.VISIBLE
                day_tab_indicator_item_view.select()
                week_tab_indicator_item_view.unSelect()
            }
            1 -> {
                week_tab_indicator_item_view.select()
                day_tab_indicator_item_view.unSelect()
                calendar_tab_indicator_item_view.visibility = View.INVISIBLE
            }
            else -> {
            }
        }
    }

    interface OnSwitchIndicatorCallback {

        fun onSwitchIndicator(v: View, position: Int)

        fun onShowCalendar(v: View)
    }

}
