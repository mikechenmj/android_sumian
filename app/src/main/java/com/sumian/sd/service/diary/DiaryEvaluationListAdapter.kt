package com.sumian.sd.service.diary

import android.content.Context
import android.support.annotation.StringRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseViewHolder
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.sd.R
import com.sumian.sd.service.tel.bean.TelBooking

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/9/14 9:19
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class DiaryEvaluationListAdapter(context: Context) : BaseRecyclerAdapter<TelBooking>(context) {

    override fun onCreateDefaultViewHolder(parent: ViewGroup?, type: Int): RecyclerView.ViewHolder {
        val viewHolder = ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.lay_advisory_item, parent, false))
        viewHolder.itemView.tag = viewHolder
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, item: TelBooking, position: Int) {
        (holder as ViewHolder).init(item, mItems.size - 1 == position)
    }

    inner class ViewHolder constructor(itemView: View) : BaseViewHolder(itemView) {

        fun init(item: TelBooking, isGoneDivider: Boolean = false) {
            setText(R.id.tv_title, item.formatOrderContent())
                    .setText(R.id.tv_advisory_time, item.formatOrderCreateTime())
                    .setText(R.id.tv_advisory_action_status, item.formatStatus())
                    .setVisible(R.id.tv_advisory_action_status, true)
                    .setGone(R.id.tv_timer, false)
                    .setVisible(R.id.divider, !isGoneDivider)
        }

        fun getString(@StringRes textId: Int = 0): String {
            return itemView.resources.getString(textId)
        }
    }
}