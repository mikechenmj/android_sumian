package com.sumian.sddoctor.service.cbti.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.common.utils.ColorCompatUtil
import com.sumian.sddoctor.R
import com.sumian.sddoctor.service.cbti.bean.CbtiChapterData
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.lay_cbti_item_introduction.*


/**
 * Created by jzz
 *
 * on 2018-10-26.
 *
 * desc: cbti  介绍页的 adapter
 *
 */
class CBTIIntroductionAdapter(context: Context) : BaseRecyclerAdapter<CbtiChapterData>(context) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: CbtiChapterData, position: Int) {
        (holder as? ViewHolder)?.initView(cbtiChapterData = item)
    }

    override fun onCreateDefaultViewHolder(parent: ViewGroup?, type: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.lay_cbti_item_introduction, parent, false))
    }

    class ViewHolder constructor(itemView: View, override val containerView: View? = itemView) :
            RecyclerView.ViewHolder(itemView), LayoutContainer {

        @SuppressLint("SetTextI18n")
        fun initView(cbtiChapterData: CbtiChapterData) {
            tv_title.setTextColor(ColorCompatUtil.getColor(itemView.context, R.color.t1_color_day))
            tv_title.text = cbtiChapterData.title
            tv_progress.setTextColor(ColorCompatUtil.getColor(itemView.context, R.color.b3_color_day))
            tv_progress.text = cbtiChapterData.formatProgress()
            iv_nav.setColorFilter(ColorCompatUtil.getColor(itemView.context, R.color.b5_color_day))
            cbti_progress_view.setProgress(cbtiChapterData.finished_percent, false)
            if (cbtiChapterData.finished_percent == 0) {
                v_divider_line.visibility = View.INVISIBLE
                tv_progress.visibility = View.INVISIBLE
                cbti_progress_view.visibility = View.INVISIBLE
            } else {
                v_divider_line.visibility = View.VISIBLE
                tv_progress.visibility = View.VISIBLE
                cbti_progress_view.visibility = View.VISIBLE
            }
            tv_video_count.text = "${cbtiChapterData.totalCount}个视频"
        }
    }

}