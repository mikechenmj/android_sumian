package com.sumian.sd.anxiousandbelief.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.sumian.sd.R
import com.sumian.sd.utils.getString
import kotlinx.android.synthetic.main.view_anxious_belief_item.view.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/26 11:17
 * desc   :
 * version: 1.0
 */
class AnxiousBeliefItemView(context: Context, attributeSet: AttributeSet? = null) : LinearLayout(context, attributeSet) {
    init {
        LayoutInflater.from(context).inflate(R.layout.view_anxious_belief_item, this, true)
    }

    fun setEmotion() {
        tv_emotion.text = getString(getEmotionTvRes())
        tv_emotion.setCompoundDrawablesRelativeWithIntrinsicBounds(getEmotionIcRes(), 0, 0, 0)
    }

    private fun getEmotionTvRes(): Int {
        return 0
    }

    private fun getEmotionIcRes(): Int {
        return 1
    }

}