package com.sumian.hw.report.widget

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log

import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager

/**
 * Created by sm
 * on 2018/3/20.
 * desc:
 */

class LoadViewPagerRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
        RecyclerViewPager(context, attrs, defStyle) {

    companion object {
        private val TAG = LoadViewPagerRecyclerView::class.java.simpleName
    }

    private var mOnLoadCallback: OnLoadCallback? = null

    init {
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val layoutManager = recyclerView!!.layoutManager
                    if (layoutManager is LinearLayoutManager) {
                        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                        if (firstVisibleItemPosition == 0) {
                            Log.e(TAG, "onScrollStateChanged: --------->$firstVisibleItemPosition")
                            mOnLoadCallback?.loadPre()
                        }

                        val lastCompletelyVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                        if (lastCompletelyVisibleItemPosition == recyclerView.adapter.itemCount - 1) {
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
