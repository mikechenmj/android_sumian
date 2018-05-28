package com.sumian.sleepdoctor.widget.nav;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jzz
 * on 2017/4/28.
 * <p>
 * desc:
 */

public class NavTabV2 extends LinearLayout implements View.OnClickListener {

    private static final int DEFAULT_TAB_COUNT = 3;
    private OnSelectedTabChangeListener mOnSelectedTabChangeListener;
    private List<ItemTab> mItemTabs = new ArrayList<>(DEFAULT_TAB_COUNT);

    public NavTabV2(Context context) {
        this(context, null);
    }

    public NavTabV2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavTabV2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onViewAdded(View child) {
        LogUtils.d("onViewAdded");
        super.onViewAdded(child);
        initView();
    }

    private void initView() {
        int childCount = getChildCount();
        // add tab to list
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof ItemTab) {
                ItemTab itemTab = (ItemTab) childAt;
                if (!mItemTabs.contains(itemTab)) {
                    mItemTabs.add(itemTab);
                }
            }
        }
        // update tab activate state
        if (mItemTabs.size() > 0) {
            for (int i = 0; i < mItemTabs.size(); i++) {
                ItemTab itemTab = mItemTabs.get(i);
                itemTab.setActivated(i == 0);
                itemTab.setTag(i);
                itemTab.setOnClickListener(this);
            }
        }
    }

    public void setOnSelectedTabChangeListener(OnSelectedTabChangeListener listener) {
        mOnSelectedTabChangeListener = listener;
    }

    @Override
    public void onClick(View v) {
        int itemPosition = (int) v.getTag();
        for (int i = 0, size = mItemTabs.size(); i < size; i++) {
            mItemTabs.get(i).setActivated(i == itemPosition);
        }
        if (mOnSelectedTabChangeListener != null) {
            mOnSelectedTabChangeListener.onSelectedTabChange((ItemTab) v, itemPosition);
        }
    }

    public interface OnSelectedTabChangeListener {
        void onSelectedTabChange(ItemTab itemTab, int position);
    }
}
