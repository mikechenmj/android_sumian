package com.sumian.sd.anxiousandfaith.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.sumian.common.utils.TimeUtilV2
import com.sumian.sd.R
import com.sumian.sd.anxiousandfaith.bean.AnxietyFaithItemViewData
import com.sumian.sd.utils.getString
import kotlinx.android.synthetic.main.view_anxious_belief_item.view.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/26 11:17
 * desc   :
 * version: 1.0
 */
class AnxiousFaithItemView(context: Context, attributeSet: AttributeSet? = null) : LinearLayout(context, attributeSet) {
    init {
        LayoutInflater.from(context).inflate(R.layout.view_anxious_belief_item, this, true)
    }

    fun setData(data: AnxietyFaithItemViewData, listener: EditAnxietyBottomSheetDialog.OnItemClickListener) {
        tv_title.text = data.title
        tv_message.text = data.message
        tv_time.text = TimeUtilV2.formatTimeYYYYMMDD_HHMM(data.time)
        iv_more.setOnClickListener { EditAnxietyBottomSheetDialog(context, listener).show() }
        if (data.type == AnxietyFaithItemViewData.TYPE_BELIEF) {
            vg_emotion.visibility = View.VISIBLE
            iv_emotion.setImageResource(data.getEmotionImageRes())
            tv_emotion.setText(data.getEmotionTextRes())
        }
    }

    fun setTextMaxLines(noLimit: Boolean) {
        tv_title.maxLines = if (noLimit) Integer.MAX_VALUE else 1
        tv_message.maxLines = if (noLimit) Integer.MAX_VALUE else 1
    }

}