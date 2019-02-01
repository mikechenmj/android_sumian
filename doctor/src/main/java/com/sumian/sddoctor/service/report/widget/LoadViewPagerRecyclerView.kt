package com.sumian.sddoctor.service.report.widget

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager

/**
 * Created by sm
 * on 2018/3/20.
 * desc:
 */
class LoadViewPagerRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
        RecyclerViewPager(context, attrs, defStyle) {

    private var mOnLoadCallback: OnLoadCallback? = null

    init {
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val layoutManager = recyclerView.layoutManager
                    if (layoutManager is LinearLayoutManager) {
                        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                        if (firstVisibleItemPosition == 0) {
                            mOnLoadCallback?.loadPre()
                        }

                        val lastCompletelyVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                        if (lastCompletelyVisibleItemPosition == recyclerView.adapter!!.itemCount - 1) {
                            mOnLoadCallback?.loadMore()
                        }
                    }
                }
            }
        })
    }

    fun setOnLoadCallback(onLoadCallback: OnLoadCallback) {
        mOnLoadCallback = onLoadCallback
    }

    interface OnLoadCallback {

        fun loadMore() {}

        fun loadPre() {}
    }


}
