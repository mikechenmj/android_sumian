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
        View.inflate(context, R.layout.lay_cbti_part_view, this)
//        val params = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//        itemView.layoutParams = params
        setBackgroundColor(resources.getColor(R.color.b2_color))
    }

    fun invalid(cbtiPart: CBTIPart?) {
        if (cbtiPart == null) {
            hide()
        } else {
            tv_title.text = cbtiPart.title
            tv_status.text = when (cbtiPart.status) {
                0 -> {
                    "请先完成上周课程"
                }
                1 -> {
                    "进度 45%"
                }
                2 -> {
                    "已完成"
                }
                else -> {
                    "请先完成上周课程"
                }
            }
            show()
        }
    }

    fun show() {
        visibility = View.VISIBLE
    }

    fun hide() {
        visibility = View.GONE
    }
}