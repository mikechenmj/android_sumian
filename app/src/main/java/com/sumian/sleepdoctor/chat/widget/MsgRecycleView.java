package com.sumian.sleepdoctor.chat.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by jzz
 * on 2018/1/31.
 * desc:
 */

public class MsgRecycleView extends RecyclerView implements RecyclerView.OnItemTouchListener {

    private static final String TAG = MsgRecycleView.class.getSimpleName();

    private OnLoadDataCallback mOnLoadDataCallback;

    private OnCloseKeyboardCallback mOnCloseKeyboardCallback;

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
                Log.e(TAG, "onScrollStateChanged: ------->" + newState);

                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        int position = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                        if (position == 0) {
                            if (mOnLoadDataCallback != null) {
                                mOnLoadDataCallback.onLoadPre();
                            }
                        }
                        break;
                    default:
                        if (mOnCloseKeyboardCallback != null) {
                            mOnCloseKeyboardCallback.onCloseKeyboard();
                        }
                        break;
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Log.e(TAG, "onScrolled: ------->dx=" + dx + "   dy=" + dy);
            }
        });

        addOnItemTouchListener(this);
    }

    public void setOnLoadDataCallback(OnLoadDataCallback onLoadDataCallback) {
        mOnLoadDataCallback = onLoadDataCallback;
    }

    public void setOnCloseKeyboardCallback(OnCloseKeyboardCallback OnCloseKeyboardCallback) {
        mOnCloseKeyboardCallback = OnCloseKeyboardCallback;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        if (rv.getScrollState() == RecyclerView.SCROLL_STATE_IDLE && e.getActionMasked() == MotionEvent.ACTION_DOWN) {
            if (mOnCloseKeyboardCallback != null) {
                mOnCloseKeyboardCallback.onCloseKeyboard();
            }
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public interface OnLoadDataCallback {

        void onLoadPre();
    }

    public interface OnCloseKeyboardCallback {

        void onCloseKeyboard();
    }
}
