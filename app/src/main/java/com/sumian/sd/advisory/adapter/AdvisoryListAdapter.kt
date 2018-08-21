package com.sumian.sd.advisory.adapter

import android.content.Context
import android.support.annotation.StringRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseViewHolder
import com.sumian.common.base.BaseRecyclerAdapter
import com.sumian.sd.R
import com.sumian.sd.advisory.bean.Advisory
import com.sumian.sd.utils.TimeUtil
import java.util.*

/**
 *
 *Created by sm
 * on 2018/6/5 13:43
 * desc:咨询列表 adapter, 包含未使用,已使用2部分
 **/
class AdvisoryListAdapter(context: Context) : BaseRecyclerAdapter<Advisory>(context) {

    override fun onCreateDefaultViewHolder(parent: ViewGroup?, type: Int): RecyclerView.ViewHolder {
        val viewHolder = ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.lay_advisory_item, parent, false))
        viewHolder.itemView.tag = viewHolder
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, item: Advisory?, position: Int) {
        (holder as ViewHolder).init(item!!)
    }

    inner class ViewHolder constructor(itemView: View) : BaseViewHolder(itemView) {

        fun init(item: Advisory) {

            //咨询状态 0: 待回复 1：已回复 2：已结束 3：已关闭，4：已取消，5：待提问
            val advisoryTitle: String = when (item.status) {
                5 -> String.format(Locale.getDefault(), "%s", item.description)
                else ->
                    item.description
            }

            setText(R.id.tv_title, advisoryTitle)
                    .setText(R.id.tv_advisory_time, if (item.start_at > 0) {
                        TimeUtil.formatYYYYMMDDHHMM(item.start_at)
                    } else {
                        TimeUtil.formatYYYYMMDDHHMM(item.created_at)
                    })
                    .setText(R.id.tv_advisory_action_status, item.formatStatus()).setVisible(R.id.tv_advisory_action_status, true)
        }

        fun getString(@StringRes textId: Int = 0): String {
            return itemView.resources.getString(textId)
        }
    }
}