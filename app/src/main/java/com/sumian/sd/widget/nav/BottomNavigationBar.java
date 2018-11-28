package com.sumian.sd.widget.nav;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by jzz
 * on 2017/4/28.
 * <p>
 * desc:
 */

public class BottomNavigationBar extends LinearLayout implements View.OnClickListener {

    private static final int DEFAULT_TAB_COUNT = 3;
    private OnSelectedTabChangeListener mOnSelectedTabChangeListener;
    private List<NavigationItem> mNavigationItems = new ArrayList<>(DEFAULT_TAB_COUNT);
    private int mActivateItemPosition;
    private boolean mInitFinished = false;
    private boolean mInitTriggerListener;

    public BottomNavigationBar(Context context) {
        this(context, null);
    }

    public BottomNavigationBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomNavigationBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initNavigationItems();
    }

    private void initNavigationItems() {
        int childCount = getChildCount();
        // add tab to list
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof NavigationItem) {
                NavigationItem navigationItem = (NavigationItem) childAt;
                if (!mNavigationItems.contains(navigationItem)) {
                    mNavigationItems.add(navigationItem);
                }
            }
        }
        // update tab activate state
        if (mNavigationItems.size() > 0) {
            for (int i = 0; i < mNavigationItems.size(); i++) {
                NavigationItem navigationItem = mNavigationItems.get(i);
                navigationItem.setActivated(i == 0);
                navigationItem.setTag(i);
                navigationItem.setOnClickListener(this);
            }
        }
        mInitFinished = true;
        onInitFinish();
    }

    private void onInitFinish() {
        selectItem(mActivateItemPosition, mInitTriggerListener);
    }

    public void setOnSelectedTabChangeListener(OnSelectedTabChangeListener listener) {
        mOnSelectedTabChangeListener = listener;
    }

    @Override
    public void onClick(View v) {
        int itemPosition = (int) v.getTag();
        selectItem(itemPosition, true);
    }

    public void selectItem(int itemPosition, boolean triggerListener) {
        // 初始化未完成的时候会记录itemPosition，等到初始化完成自动
        if (!mInitFinished) {
            mActivateItemPosition = itemPosition;
            mInitTriggerListener = triggerListener;
            return;
        }
        if (itemPosition >= mNavigationItems.size() || itemPosition < 0) {
            String format = String.format(Locale.getDefault(),
                    "Invalid item position: %d", itemPosition);
            throw new RuntimeException(format);
        }
        for (int i = 0, size = mNavigationItems.size(); i < size; i++) {
            mNavigationItems.get(i).setActivated(i == itemPosition);
        }
        if (triggerListener && mOnSelectedTabChangeListener != null) {
            mOnSelectedTabChangeListener.onSelectedTabChange(mNavigationItems.get(itemPosition), itemPosition);
        }
    }

    public interface OnSelectedTabChangeListener {
        void onSelectedTabChange(NavigationItem navigationItem, int position);
    }
}
