package com.sumian.device.test.ui

import com.sumian.device.R
import com.sumian.devicedemo.base.AdapterHost
import com.sumian.devicedemo.base.BaseAdapter
import com.sumian.devicedemo.base.BaseViewHolder
import kotlinx.android.synthetic.main.list_item_text.view.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/6 13:37
 * desc   :
 * version: 1.0
 */
class TextListAdapter(host: AdapterHost<String>? = null) : BaseAdapter<String>(host, R.layout.list_item_text) {
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val itemView = holder.itemView
        val data = mData[position]
        itemView.setOnClickListener { mHost?.onItemClick(data) }
        itemView.tv_text.text = data
    }
}

