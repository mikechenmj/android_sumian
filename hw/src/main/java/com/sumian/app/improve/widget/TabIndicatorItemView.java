package com.sumian.app.improve.widget;

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

import com.sumian.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sm
 * on 2018/3/6.
 * desc:
 */

public class TabIndicatorItemView extends FrameLayout implements View.OnClickListener {

    @BindView(R.id.tv_tab_text)
    TextView mTvTabText;
    @BindView(R.id.v_tab_dot)
    View mIvTabDot;

    @BindView(R.id.iv_calendar)
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
        ButterKnife.bind(inflate(context, R.layout.hw_lay_tab_dot, this));
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
