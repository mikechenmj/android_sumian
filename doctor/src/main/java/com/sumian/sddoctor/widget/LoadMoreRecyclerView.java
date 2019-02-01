package com.sumian.sddoctor.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by sm
 * on 2018/3/20.
 * desc:
 */

@SuppressWarnings("ConstantConditions")
public class LoadMoreRecyclerView extends RecyclerView {

    private static final String TAG = LoadMoreRecyclerView.class.getSimpleName();

    private OnLoadCallback mOnLoadCallback;

    public LoadMoreRecyclerView(Context context) {
        this(context, null);
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NotNull RecyclerView recyclerView, int newState) {
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
