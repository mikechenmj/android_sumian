package com.sumian.hw.report.widget.tab

import android.annotation.SuppressLint
import android.content.Context
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

class TabIndicatorItemView : FrameLayout, View.OnClickListener, IVisible {

    private var mOnSelectTabCallback: OnSelectTabCallback? = null
    private var mEnableTab = false

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.TabIndicatorItemView, defStyleAttr, defStyleRes)

        mEnableTab = attributes.getBoolean(R.styleable.TabIndicatorItemView_enable_tab, false)

        attributes.recycle()
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
        tv_tab_text.isActivated = true
        mEnableTab = tv_tab_text.isActivated
        mOnSelectTabCallback?.onSelect(v, tv_tab_text.isActivated)
    }

    fun unSelect() {
        tv_tab_text.isActivated = false
        mEnableTab = tv_tab_text.isActivated
    }

    fun select() {
        tv_tab_text.isActivated = true
        mEnableTab = tv_tab_text.isActivated
    }

    fun setIndicatorText(text: String) {
        tv_tab_text.text = text
        tv_tab_text.visibility = if (!TextUtils.isEmpty(text)) View.VISIBLE else View.GONE
    }

    @SuppressLint("ResourceType")
    fun showCalendar(isShow: Boolean) {
        iv_calendar.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    fun showDot() {
        v_tab_dot.visibility = View.VISIBLE
    }

    fun hideDot() {
        v_tab_dot.visibility = View.GONE
    }

    fun isEnableTab(): Boolean {
        return mEnableTab
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
