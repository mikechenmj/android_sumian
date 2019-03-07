package com.sumian.common.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.sumian.common.R
import kotlinx.android.synthetic.main.common_view_empty.view.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/12 13:43
 * desc   :
 * version: 1.0
 */
class CommonEmptyView(context: Context, attributeSet: AttributeSet? = null) : FrameLayout(context) {
    init {
        LayoutInflater.from(context).inflate(R.layout.common_view_empty, this, true)
        val a = context.obtainStyledAttributes(attributeSet, R.styleable.CommonEmptyView)
        val imageRes = a.getResourceId(R.styleable.CommonEmptyView_cev_image, 0)
        val titleStr = a.getString(R.styleable.CommonEmptyView_cev_title)
        val contentStr = a.getString(R.styleable.CommonEmptyView_cev_content)
        a.recycle()
        if (imageRes != 0) {
            iv_top.setImageResource(imageRes)
        }
        setTv(tv_title, titleStr)
        setTv(tv_content, contentStr)
    }

    private fun setTv(tv: TextView, text: String?) {
        tv.text = text
    }

    fun setImage(imageRes: Int): CommonEmptyView {
        iv_top.setImageResource(imageRes)
        return this
    }

    fun setTitle(titleRes: Int): CommonEmptyView {
        tv_title.setText(titleRes)
        return this
    }

    fun setMessage(message: Int): CommonEmptyView {
        tv_content.setText(message)
        return this
    }
}