package com.sumian.sd.service.coupon.adpater

import android.content.Context
import android.support.annotation.StringRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseViewHolder
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.sd.R
import com.sumian.sd.service.coupon.bean.Coupon

/**
 *
 *Created by sm
 * on 2018/10/18 13:43
 * desc:兑换码 兑换列表 adapter
 **/
class CouponListAdapter(context: Context) : BaseRecyclerAdapter<Coupon>(context) {

    override fun onCreateDefaultViewHolder(parent: ViewGroup?, type: Int): RecyclerView.ViewHolder {
        val viewHolder = ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.lay_advisory_item, parent, false))
        viewHolder.itemView.tag = viewHolder
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, item: Coupon, position: Int) {
        (holder as ViewHolder).init(item, mItems.size - 1 == position)
    }

    inner class ViewHolder constructor(itemView: View) : BaseViewHolder(itemView) {

        fun init(item: Coupon, isGoneDivider: Boolean = false) {
            setText(R.id.tv_title, item.Title)
                    .setText(R.id.tv_advisory_time, item.formatTime())
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