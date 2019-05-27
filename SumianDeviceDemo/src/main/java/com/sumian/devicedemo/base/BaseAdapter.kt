package com.sumian.devicedemo.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/6 13:37
 * desc   :
 * version: 1.0
 */
abstract class BaseAdapter<T>(host: AdapterHost<T>? = null, itemLayoutId: Int) :
        RecyclerView.Adapter<BaseViewHolder>() {
    val mData = ArrayList<T>()
    var mHost = host
    private val mItemLayoutId = itemLayoutId

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(mItemLayoutId, parent, false)
        return BaseViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    fun addData(data: T) {
        if (!allowDuplicateData() && mData.contains(data)) {
            return
        }
        mData.add(data)
        notifyItemInserted(mData.size - 1)
    }

    fun clear() {
        mData.clear()
        notifyDataSetChanged()
    }

    open fun allowDuplicateData(): Boolean {
        return true
    }
}

