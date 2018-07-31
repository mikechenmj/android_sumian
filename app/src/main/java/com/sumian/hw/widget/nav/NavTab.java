package com.sumian.hw.widget.nav;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.sumian.sleepdoctor.BuildConfig;
import com.sumian.sleepdoctor.R;

/**
 * Created by jzz
 * on 2017/4/28.
 * <p>
 * desc:
 */

public class NavTab extends LinearLayout implements View.OnClickListener {

    private OnTabChangeListener mOnTabChangeListener;

    public NavTab(Context context) {
        super(context);
        init();
    }

    public NavTab(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NavTab(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);
    }

    public void setOnTabChangeListener(OnTabChangeListener OnTabChangeListener) {
        mOnTabChangeListener = OnTabChangeListener;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child instanceof TabButton) {
                TabButton tabButton = (TabButton) child;
                tabButton.setOnClickListener(this);
                switch (i) {
                    case 0:
                        tabButton.init(R.drawable.tab_device_icon, R.string.tab_device_hint);
                        break;
                    case 1:
                        tabButton.init(R.drawable.tab_report_icon, R.string.tab_report_hint);
                        if (BuildConfig.IS_CLINICAL_VERSION) {
                            tabButton.setVisibility(GONE);
                        } else {
                            tabButton.setVisibility(VISIBLE);
                        }
                        break;
                    case 2:
                        tabButton.init(R.drawable.tab_consultant_icon, R.string.tab_consultant_hint);
                        break;
                    case 3:
                        tabButton.init(R.drawable.tab_me_icon, R.string.tab_me_hint);
                        break;
                }
            }
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mOnTabChangeListener = null;
    }

    /**
     * 与 viewPager 联动
     *
     * @param position viewPager updateTabsUiBySelectPosition position
     */
    public void pageSelected(int position) {
        for (int i = 0; i < getChildCount(); i++) {
            TabButton child = (TabButton) getChildAt(i);
            if (i == position) {
                child.setActivated(true);
            } else {
                child.setActivated(false);
            }
        }
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < getChildCount(); i++) {
            TabButton child = (TabButton) getChildAt(i);
            if (v.getId() == child.getId()) {
                child.setActivated(true);
                OnTabChangeListener onTabChangeListener = this.mOnTabChangeListener;
                if (onTabChangeListener != null)
                    onTabChangeListener.tab((TabButton) v, i);
            } else {
                child.setActivated(false);
            }
        }
    }

    public void showDot(int position, int show) {
        for (int i = 0; i < getChildCount(); i++) {
            if (position == i) {
                View child = getChildAt(i);
                TabButton tabButton = (TabButton) child;
                tabButton.showDot(show);
            }
        }
    }

    /**
     * 自己主动向外联动
     */
    public interface OnTabChangeListener {
        void tab(TabButton tabButton, int position);
    }

}
