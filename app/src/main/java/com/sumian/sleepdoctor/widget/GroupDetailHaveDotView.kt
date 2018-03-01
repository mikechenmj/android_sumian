package com.sumian.sleepdoctor.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.qmuiteam.qmui.widget.QMUIRadiusImageView
import com.sumian.sleepdoctor.R
import kotlinx.android.synthetic.main.lay_group_detail_have_dot.view.*

/**
 * Created by sm
 * on 2018/2/24.
 * desc:
 */
class GroupDetailHaveDotView : FrameLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initLayout(context)
    }

    private fun initLayout(context: Context) {
        View.inflate(context, R.layout.lay_group_detail_have_dot, this)
    }


    fun showOrHideDot(isShow: Boolean = false) {
        msg_dot.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    fun getImageView(): QMUIRadiusImageView {
        return iv_group_icon
    }
}