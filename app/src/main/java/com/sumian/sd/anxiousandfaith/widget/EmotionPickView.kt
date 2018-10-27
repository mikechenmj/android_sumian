@file:Suppress("UNUSED_ANONYMOUS_PARAMETER")

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
import com.sumian.sd.anxiousandfaith.bean.AnxietyFaithItemViewData
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

    init {
        LayoutInflater.from(context).inflate(R.layout.recycler_view, this, true)
        recycler_view.layoutManager = GridLayoutManager(context, 4)
        recycler_view.adapter = mAdapter
        mAdapter.setNewData(AnxietyFaithItemViewData.EMOTION_LIST)
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

    fun setSelectedEmotion(emotion: Int) {
        mSelectedPosition = emotion
        mAdapter.notifyDataSetChanged()
    }
}