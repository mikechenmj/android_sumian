package com.sumian.sd.anxiousandfaith.widget

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sumian.common.utils.ColorCompatUtil
import com.sumian.sd.R
import com.sumian.sd.anxiousandfaith.event.EmotionData
import kotlinx.android.synthetic.main.recycler_view.view.*

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/26 19:33
 * desc   :
 * version: 1.0
 */
class EmotionPickView(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet) {
    private var mSelectedPosition = -1
    private var mAdapter = EmotionAdapter()
    private val emotions = listOf(
            EmotionData(0, R.string.emotion_0, R.drawable.belief_icon_facial1_default, R.drawable.belief_icon_facial1_selected),
            EmotionData(1, R.string.emotion_1, R.drawable.belief_icon_facial2_default, R.drawable.belief_icon_facial2_selected),
            EmotionData(2, R.string.emotion_2, R.drawable.belief_icon_facial3_default, R.drawable.belief_icon_facial3_selected),
            EmotionData(3, R.string.emotion_3, R.drawable.belief_icon_facial4_default, R.drawable.belief_icon_facial4_selected),
            EmotionData(4, R.string.emotion_4, R.drawable.belief_icon_facial5_default, R.drawable.belief_icon_facial5_selected),
            EmotionData(5, R.string.emotion_5, R.drawable.belief_icon_facial6_default, R.drawable.belief_icon_facial6_selected)
    )

    init {
        LayoutInflater.from(context).inflate(R.layout.recycler_view, this, true)
        recycler_view.layoutManager = GridLayoutManager(context, 4)
        recycler_view.adapter = mAdapter
        mAdapter.setNewData(emotions)
        mAdapter.setOnItemClickListener { adapter, view, position -> onItemClick(position) }
    }

    private fun onItemClick(position: Int) {
        mAdapter.notifyItemChanged(mSelectedPosition)
        mSelectedPosition = position
        mAdapter.notifyItemChanged(mSelectedPosition)
    }

    inner class EmotionAdapter : BaseQuickAdapter<EmotionData, BaseViewHolder>(R.layout.list_item_emotion) {
        override fun convert(helper: BaseViewHolder, item: EmotionData) {
            val position = helper.adapterPosition
            val isSelected = position == mSelectedPosition
            helper.setText(R.id.tv_emotion, item.textRes)
            helper.setTextColor(R.id.tv_emotion, ColorCompatUtil.getColor(mContext, (if (isSelected) R.color.b3_color else R.color.t2_color)))
            helper.setImageResource(R.id.iv_emotion, if (isSelected) item.selectedImageRes else item.imageRes)
        }
    }

    fun getSelectedEmotion(): Int {
        return mSelectedPosition
    }
}