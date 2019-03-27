package com.sumian.common.widget.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sumian.common.R

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/27 14:50
 * desc   :
 * version: 1.0
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseAdapter<T> private constructor(recyclerView: RecyclerView) : RecyclerView.Adapter<BaseViewHolder>() {
    companion object {
        const val ITEM_TYPE_DATA = 0
        const val ITEM_TYPE_HEADER = -1
        const val ITEM_TYPE_LOAD_MORE = -2
        const val ITEM_TYPE_FOOTER = -3
    }

    private var mContext: Context = recyclerView.context
    private var mRecyclerView = recyclerView
    private var mData: MutableList<T> = ArrayList()
    private var mHeader: View? = null
    @SuppressLint("InflateParams")
    private var mFooter: View? = null
    private var mLoadMoreView: View? =
            LayoutInflater.from(mContext).inflate(R.layout.base_adapter_defaul_load_more_view, mRecyclerView, false)
    private var mIsLoadingMore = false
    private var mIsLoadMoreEnable = true
    private var mViewTypeLayoutMap: MutableMap<Int, Int> = HashMap()
    private var mLoadMoreListener: LoadMoreListener? = null
    private var mOnItemClickListener: OnItemClickListener<T>? = null

    init {
        showLoadMore(false)
        println("init")
    }

    constructor(recyclerView: RecyclerView, layout: Int) : this(recyclerView) {
        mViewTypeLayoutMap[ITEM_TYPE_DATA] = layout
        println("constructor")
    }

    constructor(recyclerView: RecyclerView, viewTypeLayout: Map<Int, Int>) : this(recyclerView) {
        mViewTypeLayoutMap.putAll(viewTypeLayout)
        println("constructor 2")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        mContext = parent.context
        return when (viewType) {
            ITEM_TYPE_HEADER -> BaseViewHolder(mHeader!!)
            ITEM_TYPE_LOAD_MORE -> BaseViewHolder(mLoadMoreView!!)
            ITEM_TYPE_FOOTER -> BaseViewHolder(mFooter!!)
            else -> BaseViewHolder(inflate(parent, mViewTypeLayoutMap.getValue(viewType)))
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (position >= getHeaderCount() && position <= itemCount - getFooterCount() - getLoadMoreCount() - 1) {
            val data = getData(position) ?: return
            onBindDataViewHolder(holder, data)
            mOnItemClickListener?.onItemClick(position, holder, data)
        } else if (position == getLoadMoreItemPosition() && getDataSize() > 0) {
            if (mIsLoadMoreEnable && !mIsLoadingMore) {
                showLoadMore(true)
                mLoadMoreListener?.loadMore()
            }
        }
    }

    abstract fun onBindDataViewHolder(holder: BaseViewHolder, data: T)

    override fun getItemCount(): Int {
        val dataSize = getDataSize()
        val headerCount = getHeaderCount()
        val footerCount = getFooterCount()
        val loadMoreCount = getLoadMoreCount()
        return dataSize + headerCount + footerCount + loadMoreCount
    }

    private fun getDataSize() = mData.size

    private fun getFooterCount() = if (mFooter == null) 0 else 1

    private fun getHeaderCount() = if (mHeader == null) 0 else 1

    private fun getLoadMoreCount() = 1

    private fun inflate(parent: ViewGroup, layout: Int): View {
        return LayoutInflater.from(parent.context).inflate(layout, parent, false)
    }

    fun setHeaderView(layout: Int) {
        mHeader = LayoutInflater.from(mContext).inflate(layout, null, false)
    }

    fun setHeaderView(view: View?) {
        mHeader = view
    }

    fun setFooterView(layout: Int) {
        mFooter = LayoutInflater.from(mContext).inflate(layout, null, false)
    }

    fun setFooterView(view: View?) {
        mFooter = view
    }

    fun setLoadMoreView(layout: Int) {
        mLoadMoreView = LayoutInflater.from(mContext).inflate(layout, null, false)
    }

    fun setLoadMoreView(view: View?) {
        mLoadMoreView = view
    }

    fun getDataPosition(position: Int): Int {
        return position - getHeaderCount()
    }

    fun getData(position: Int): T? {
        return mData.get(getDataPosition(position))
    }

    override fun getItemViewType(position: Int): Int {
        if (getHeaderCount() > 0 && position == 0) {
            return ITEM_TYPE_HEADER
        } else if (getLoadMoreCount() > 0 && position == getLoadMoreItemPosition()) {
            return ITEM_TYPE_LOAD_MORE
        } else if (getFooterCount() > 0 && position == getFooterItemPosition()) {
            return ITEM_TYPE_FOOTER
        }
        return getDataItemViewType(position, getData(position)!!)
    }

    private fun getFooterItemPosition() = itemCount - 1

    private fun getLoadMoreItemPosition() = itemCount - 1 - getFooterCount()

    open fun getDataItemViewType(position: Int, data: T): Int {
        return ITEM_TYPE_DATA
    }

    fun showLoadMore(show: Boolean) {
        mLoadMoreView?.visibility = if (show) View.VISIBLE else View.GONE
        mIsLoadingMore = show
    }

    fun enableLoadMore(enable: Boolean) {
        mIsLoadMoreEnable = enable
        if (!enable) {
            showLoadMore(false)
        }
    }

    fun addData(data: List<T>) {
        mData.addAll(data)
        notifyDataSetChanged()
    }

    fun setData(data: List<T>?) {
        mData.clear()
        if (data != null) {
            mData.addAll(data)
        }
        notifyDataSetChanged()
    }

    interface LoadMoreListener {
        fun loadMore()
    }

    interface OnItemClickListener<T> {
        fun onItemClick(position: Int, viewHolder: BaseViewHolder, data: T)
    }

    fun setLoadMoreListener(listener: LoadMoreListener?) {
        mLoadMoreListener = listener
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener<T>?) {
        mOnItemClickListener = onItemClickListener
    }

}

class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)