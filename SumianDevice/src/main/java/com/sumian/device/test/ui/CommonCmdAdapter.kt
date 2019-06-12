package com.sumian.device.test.ui

import android.annotation.SuppressLint
import com.sumian.device.R
import com.sumian.devicedemo.base.AdapterHost
import com.sumian.devicedemo.base.BaseAdapter
import com.sumian.devicedemo.base.BaseViewHolder
import kotlinx.android.synthetic.main.list_item_common_cmd.view.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/6 13:37
 * desc   :
 * version: 1.0
 */
class CommonCmdAdapter(host: AdapterHost<CommonCmd>) : BaseAdapter<CommonCmd>(host, R.layout.list_item_common_cmd) {

    init {
        mData.add(CommonCmd("query monitor battery", "aa44"))
        mData.add(CommonCmd("query sleeper battery", "aa45"))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val itemView = holder.itemView
        val data = mData[position]
        itemView.setOnClickListener { mHost?.onItemClick(data) }
        itemView.tv_text.text = "${data.desc}: ${data.cmd}"
    }
}

data class CommonCmd(var desc: String, var cmd: String)


