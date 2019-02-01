package com.sumian.sddoctor.patient.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import com.sumian.sddoctor.R
import com.sumian.sddoctor.util.ResUtils
import kotlinx.android.synthetic.main.lay_patient_group_tag_tips_view.view.*

/**
 * Created by dq
 *
 * on 2018/8/30
 *
 * desc:  患者分组 上面的 tag tips
 */
class GroupTagTipsView : LinearLayout, View.OnClickListener {

    private var onTagTipsCallback: OnTagTipsCallback? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        gravity = Gravity.CENTER
        orientation = HORIZONTAL
        initView(context)
        setBackgroundColor(ResUtils.getColor(R.color.b2_color))
        setOnClickListener(this)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.lay_patient_group_tag_tips_view, this)
    }

    fun setOnTagTipsCallback(onTagTipsCallback: OnTagTipsCallback) {
        this.onTagTipsCallback = onTagTipsCallback
    }

    override fun onClick(v: View) {
        if (v.tag == null) {
            iv_label.rotation = 180.0f
            v.tag = true
            onTagTipsCallback?.showGroup()
        } else {
            iv_label.rotation = 0.0f
            v.tag = null
            onTagTipsCallback?.hideGroup()
        }
    }

    fun setTagTips(tagTips: String) {
        tv_label.text = tagTips
    }

    fun setIsShow(isShow: Boolean) {
        if (isShow) {
            iv_label.rotation = 180.0f
            tag = true
        } else {
            iv_label.rotation = 0.0f
            tag = null
        }
    }

    interface OnTagTipsCallback {

        fun showGroup()

        fun hideGroup()
    }
}