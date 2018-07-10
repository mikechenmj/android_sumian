package com.sumian.sleepdoctor.widget.cbti

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import com.sumian.sleepdoctor.R
import kotlinx.android.synthetic.main.lay_cbti_part_view.view.*

@Suppress("DEPRECATION")
/**
 * Created by sm
 *
 * on 2018/7/10
 *
 * desc:  CBTI  课程item  自己使用的时候需要自己设置 height
 *
 */
class CBTIPartView : ConstraintLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    private fun initView(context: Context) {
        val itemView = View.inflate(context, R.layout.lay_cbti_part_view, this)
//        val params = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//        itemView.layoutParams = params
        setBackgroundColor(resources.getColor(R.color.b2_color))
    }

    fun invalid(cbti: Any) {

        tv_title.text = "123"
        tv_status.text = "2323"

    }

    fun show() {
        visibility = View.VISIBLE
    }

    fun hide() {
        visibility = View.GONE
    }
}