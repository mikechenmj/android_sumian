package com.sumian.sd.service.cbti.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.common.utils.ColorCompatUtil
import com.sumian.sd.R
import com.sumian.sd.homepage.bean.CbtiChapterData
import com.sumian.sd.service.cbti.widget.CBTIProgressView


/**
 * Created by jzz
 *
 * on 2018-10-26.
 *
 * desc: cbti  介绍页的 adapter
 *
 */
class CBTIIntroductionAdapter(context: Context) : BaseRecyclerAdapter<CbtiChapterData>(context) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, item: CbtiChapterData, position: Int) {
        (holder as? ViewHolder)?.initView(cbtiChapterData = item)
    }

    override fun onCreateDefaultViewHolder(parent: ViewGroup?, type: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.lay_cbti_item_introduction, parent, false))
    }

    inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvTitle: TextView  by lazy {
            itemView.findViewById<TextView>(R.id.tv_title)
        }

        private val tvProgress: TextView by lazy {
            itemView.findViewById<TextView>(R.id.tv_progress)
        }

        private val cbtiProgressView: CBTIProgressView by lazy {
            itemView.findViewById<CBTIProgressView>(R.id.cbti_progress_view)
        }

        fun initView(cbtiChapterData: CbtiChapterData) {
            tvTitle.text = cbtiChapterData.title
            val isLock = cbtiChapterData.isLock
            when (cbtiChapterData.chapterProgress) {
                0 -> {
                    if (isLock) {
                        tvTitle.setTextColor(ColorCompatUtil.getColor(itemView.context, R.color.t2_color_day))
                        tvProgress.setTextColor(ColorCompatUtil.getColor(itemView.context, R.color.t2_color_day))
                    } else {
                        tvTitle.setTextColor(ColorCompatUtil.getColor(itemView.context, R.color.t1_color_day))
                        tvProgress.setTextColor(ColorCompatUtil.getColor(itemView.context, R.color.b3_color_day))
                    }
                }
                else -> {
                    tvTitle.setTextColor(ColorCompatUtil.getColor(itemView.context, R.color.t1_color_day))
                    tvProgress.setTextColor(ColorCompatUtil.getColor(itemView.context, R.color.b3_color_day))
                }
            }
            tvProgress.text = cbtiChapterData.formatProgress()
            cbtiProgressView.setProgress(cbtiChapterData.chapterProgress, isLock)
        }


    }

}