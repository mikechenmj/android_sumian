package com.sumian.hw.improve.widget.report;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;

/**
 * Created by sm
 * on 2018/3/20.
 * desc:
 */

public class LoadViewPagerRecyclerView extends RecyclerViewPager {

    private static final String TAG = LoadViewPagerRecyclerView.class.getSimpleName();

    private OnLoadCallback mOnLoadCallback;

    public LoadViewPagerRecyclerView(Context context) {
        this(context, null);
    }

    public LoadViewPagerRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadViewPagerRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    LayoutManager layoutManager = recyclerView.getLayoutManager();
                    if (layoutManager instanceof LinearLayoutManager) {
                        int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                        if (firstVisibleItemPosition == 0) {
                            if (mOnLoadCallback != null) {
                                Log.e(TAG, "onScrollStateChanged: --------->" + firstVisibleItemPosition);
                                mOnLoadCallback.loadPre();
                            }
                        }

                        int lastCompletelyVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                        if (lastCompletelyVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1) {
                            if (mOnLoadCallback != null) {
                                mOnLoadCallback.loadMore();
                            }
                        }
                    }
                }
            }
        });
    }

    public void setOnLoadCallback(OnLoadCallback onLoadCallback) {
        mOnLoadCallback = onLoadCallback;
    }

    public interface OnLoadCallback {

        default void loadMore() {
        }

        default void loadPre() {
        }
    }
}
