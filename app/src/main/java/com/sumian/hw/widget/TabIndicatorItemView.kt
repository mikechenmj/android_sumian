package com.sumian.hw.widget

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.DrawableRes
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.sumian.common.widget.voice.IVisible
import com.sumian.sd.R
import kotlinx.android.synthetic.main.hw_lay_tab_dot.view.*

/**
 * Created by sm
 * on 2018/3/6.
 * desc:
 */

class TabIndicatorItemView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr), View.OnClickListener, IVisible {


    private var mOnSelectTabCallback: OnSelectTabCallback? = null

    init {
        setOnClickListener(this)
        initView(context)
    }

    private fun initView(context: Context) {
        setPadding(0, resources.getDimensionPixelSize(R.dimen.space_32), 0, resources.getDimensionPixelSize(R.dimen.space_14))
        View.inflate(context, R.layout.hw_lay_tab_dot, this)
        tv_tab_text.visibility = View.GONE
        iv_calendar.visibility = View.GONE
        v_tab_dot.visibility = View.GONE
    }

    fun setOnSelectTabCallback(onSelectTabCallback: OnSelectTabCallback) {
        mOnSelectTabCallback = onSelectTabCallback
    }

    override fun onClick(v: View) {
        if (mOnSelectTabCallback != null) {
            tv_tab_text.isActivated = true
            mOnSelectTabCallback!!.onSelect(v, tv_tab_text.isActivated)
        }
    }

    fun unSelect() {
        tv_tab_text.isActivated = false
    }

    fun select() {
        tv_tab_text.isActivated = true
    }

    fun setIndicatorText(text: String) {
        tv_tab_text.text = text
        tv_tab_text.visibility = if (!TextUtils.isEmpty(text)) View.VISIBLE else View.GONE
    }

    @SuppressLint("ResourceType")
    fun setCalendarIcon(@DrawableRes drawable: Int) {
        iv_calendar.setImageResource(drawable)
        iv_calendar.visibility = if (drawable > -1) View.VISIBLE else View.GONE
    }

    fun showDot() {
        v_tab_dot.visibility = View.VISIBLE
    }

    fun hideDot() {
        v_tab_dot.visibility = View.GONE
    }

    override fun show() {
        visibility = View.VISIBLE
    }

    override fun hide() {
        visibility = View.GONE
    }


    interface OnSelectTabCallback {

        fun onSelect(v: View, isSelect: Boolean)

    }

}
