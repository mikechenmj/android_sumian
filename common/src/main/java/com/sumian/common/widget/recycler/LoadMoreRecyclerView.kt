package com.sumian.common.widget.recycler

import android.content.Context
import android.util.AttributeSet
import android.util.Log

/**
 * Created by sm
 * on 2018/3/20.
 * desc:
 */

class LoadMoreRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
        RecyclerView(context, attrs, defStyle) {

    companion object {
        private val TAG = LoadMoreRecyclerView::class.java.simpleName
    }

    private var mOnLoadCallback: OnLoadCallback? = null

    init {
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                //Log.e(TAG, "onScrollStateChanged: newState=$newState")
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val layoutManager = recyclerView!!.layoutManager
                    if (layoutManager is LinearLayoutManager) {
                        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                        if (firstVisibleItemPosition == 0) {
                            //Log.e(TAG, "onScrollStateChanged: --------->$firstVisibleItemPosition")
                            mOnLoadCallback?.loadPre()
                        }

                        val lastCompletelyVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                        if (lastCompletelyVisibleItemPosition > 0 && lastCompletelyVisibleItemPosition == recyclerView.adapter.itemCount - 1) {
                            mOnLoadCallback?.loadMore()
                        }
                    }
                } else {
                    if (recyclerView!!.adapter.itemCount <= 0) {

                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                //Log.e(TAG, "onScrollStateChanged: ------->newState=${recyclerView!!.scrollState}   dx=$dx  dy=$dy")
                if (dy == 0) return
                if (dy > 0) {//向上滑动
                    mOnLoadCallback?.onScrollUp()
                } else {//向下滑动
                    mOnLoadCallback?.onScrollDown()
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

        fun onScrollUp() {}

        fun onScrollDown() {}
    }


}
