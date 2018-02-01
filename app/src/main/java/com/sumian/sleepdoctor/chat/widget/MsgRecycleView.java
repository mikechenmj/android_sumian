package com.sumian.sleepdoctor.chat.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by jzz
 * on 2018/1/31.
 * desc:
 */

public class MsgRecycleView extends RecyclerView {


    private OnLoadDataCallback mOnLoadDataCallback;


    public MsgRecycleView(Context context) {
        this(context, null);
    }

    public MsgRecycleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MsgRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int position = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                // Log.e(TAG, "onScrollStateChanged: ------->" + newState + "  position=" + position);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && position == 0) {
                    if (mOnLoadDataCallback != null) {
                        mOnLoadDataCallback.onLoadPre();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Log.e(TAG, "onScrolled: ------->dx=" + dx + "   dy=" + dy);
            }
        });
    }

    public void setOnLoadDataCallback(OnLoadDataCallback onLoadDataCallback) {
        mOnLoadDataCallback = onLoadDataCallback;
    }

    public interface OnLoadDataCallback {

        void onLoadPre();
    }
}
