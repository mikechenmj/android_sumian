package com.sumian.sd.buz.anxiousandfaith.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.sumian.common.utils.TimeUtilV2
import com.sumian.sd.R
import kotlinx.android.synthetic.main.view_anxious_mood_diary_item.view.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/26 11:17
 * desc   :
 * version: 1.0
 */
class AnxiousMoodDiaryItemView(context: Context, attributeSet: AttributeSet? = null) : LinearLayout(context, attributeSet) {
    init {
        LayoutInflater.from(context).inflate(R.layout.view_anxious_mood_diary_item, this, true)
    }

    fun setData(title: String, time: Long, emotion: Int?, listener: EditAnxietyBottomSheetDialog.OnItemClickListener) {
        tv_title.text = title
        tv_time.text = TimeUtilV2.formatYYYYMMDDHHMMss(time)
        iv_more.setOnClickListener { EditAnxietyBottomSheetDialog(context, listener).show() }
        if (emotion != null) {
            iv_emotion.visibility = View.VISIBLE
            iv_emotion.setImageResource(emotion)
        }
    }

    fun setTextMaxLines(noLimit: Boolean) {
        tv_title.maxLines = if (noLimit) Integer.MAX_VALUE else 1
    }

    fun showUnHandleTip(text: String) {
        tv_un_handle_tip.text = text
        tv_un_handle_tip.isVisible = true
    }

    fun hideUnHandleTip() {
        tv_un_handle_tip.isVisible = false
    }

}