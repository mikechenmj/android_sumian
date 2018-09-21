package com.sumian.hw.report.widget.tab

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

class TabIndicatorView : FrameLayout, TabIndicatorItemView.OnSelectTabCallback {

    private var mOnSwitchIndicatorCallback: OnSwitchIndicatorCallback? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    fun setOnSwitchIndicatorCallback(onSwitchIndicatorCallback: OnSwitchIndicatorCallback) {
        mOnSwitchIndicatorCallback = onSwitchIndicatorCallback
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.hw_lay_tab_indicator_view, this)

        iv_back.setOnClickListener {
            mOnSwitchIndicatorCallback?.onBack(it)
        }

        day_tab_indicator_item_view.setIndicatorText(resources.getString(R.string.today))
        day_tab_indicator_item_view.setOnSelectTabCallback(this)
        day_tab_indicator_item_view.select()

        week_tab_indicator_item_view.setIndicatorText(resources.getString(R.string.week))
        week_tab_indicator_item_view.setOnSelectTabCallback(this)
        week_tab_indicator_item_view.unSelect()

        calendar_tab_indicator_item_view.setOnSelectTabCallback(this)
    }

    fun selectTabByPosition(position: Int) {
        when (position) {
            0 -> onSelect(day_tab_indicator_item_view, true)
            1 -> onSelect(week_tab_indicator_item_view, true)
        }
    }

    override fun onSelect(v: View, isSelect: Boolean) {
        var position = 0
        when (v.id) {
            R.id.day_tab_indicator_item_view -> {
                week_tab_indicator_item_view.unSelect()
                position = 0
                mOnSwitchIndicatorCallback?.onSwitchIndicator(v, position)

            }
            R.id.week_tab_indicator_item_view -> {
                day_tab_indicator_item_view.unSelect()
                position = 1
                mOnSwitchIndicatorCallback?.onSwitchIndicator(v, position)
            }
            R.id.calendar_tab_indicator_item_view -> mOnSwitchIndicatorCallback?.onShowCalendar(v)
        }
        updateTabsUiBySelectPosition(position)
    }

    fun updateTabsUiBySelectPosition(position: Int) {
        when (position) {
            0 -> {
                calendar_tab_indicator_item_view.visibility = View.VISIBLE
                calendar_tab_indicator_item_view.showCalendar(true)
                day_tab_indicator_item_view.select()
                week_tab_indicator_item_view.unSelect()
            }
            1 -> {
                week_tab_indicator_item_view.select()
                day_tab_indicator_item_view.unSelect()
                calendar_tab_indicator_item_view.showCalendar(false)
                calendar_tab_indicator_item_view.visibility = View.INVISIBLE
            }
        }
    }

    interface OnSwitchIndicatorCallback {

        fun onBack(v: View)

        fun onSwitchIndicator(v: View, position: Int)

        fun onShowCalendar(v: View)
    }

}
