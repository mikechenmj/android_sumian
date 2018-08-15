package com.sumian.hw.improve.widget;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumian.sd.R;

import butterknife.ButterKnife;

/**
 * Created by sm
 * on 2018/3/6.
 * desc:
 */

public class TabIndicatorItemView extends FrameLayout implements View.OnClickListener {

    TextView mTvTabText;
    View mIvTabDot;
    ImageView mIvCalendar;

    private OnSelectTabCallback mOnSelectTabCallback;

    public TabIndicatorItemView(@NonNull Context context) {
        this(context, null);
    }

    public TabIndicatorItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabIndicatorItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(this);
        initView(context);
    }

    private void initView(Context context) {
        View inflate = inflate(context, R.layout.hw_lay_tab_dot, this);
        ButterKnife.bind(inflate);
        mTvTabText = inflate.findViewById(R.id.tv_tab_text);
        mIvTabDot = inflate.findViewById(R.id.v_tab_dot);
        mIvCalendar = inflate.findViewById(R.id.iv_calendar);

        mTvTabText.setVisibility(GONE);
        mIvCalendar.setVisibility(GONE);
        mIvTabDot.setVisibility(GONE);
    }

    public void setOnSelectTabCallback(OnSelectTabCallback onSelectTabCallback) {
        mOnSelectTabCallback = onSelectTabCallback;
    }

    @Override
    public void onClick(View v) {
        if (mOnSelectTabCallback != null) {
            mTvTabText.setActivated(true);
            mOnSelectTabCallback.onSelect(v, mTvTabText.isActivated());
        }
    }

    public void unSelect() {
        mTvTabText.setActivated(false);
    }

    public void select() {
        mTvTabText.setActivated(true);
    }

    public void setIndicatorText(String text) {
        mTvTabText.setText(text);
        mTvTabText.setVisibility(!TextUtils.isEmpty(text) ? VISIBLE : GONE);
    }

    public void setCalendarIcon(@DrawableRes int drawable) {
        mIvCalendar.setImageResource(drawable);
        mIvCalendar.setVisibility(drawable > -1 ? VISIBLE : GONE);
    }

    public void showDot() {
        mIvTabDot.setVisibility(VISIBLE);
    }

    public void hideDot() {
        mIvTabDot.setVisibility(GONE);
    }


    public interface OnSelectTabCallback {

        void onSelect(View v, boolean isSelect);
    }

}
