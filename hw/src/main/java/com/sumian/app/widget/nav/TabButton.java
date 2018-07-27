package com.sumian.app.widget.nav;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumian.app.R;

/**
 * Created by jzz
 * on 2017/4/28.
 * <p>
 * desc:
 */

public class TabButton extends FrameLayout {

    private static final String TAG = "TabButton";

    private ImageView mIvIcon;
    private TextView mTvText;
    private View mDot;

    public TabButton(@NonNull Context context) {
        super(context);
        init();
    }

    public TabButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TabButton(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View tabContainer = LayoutInflater.from(getContext()).inflate(R.layout.hw_lay_tab_item, this, false);
        this.mIvIcon = (ImageView) tabContainer.findViewById(R.id.iv_tab_icon);
        this.mTvText = (TextView) tabContainer.findViewById(R.id.tv_tab_text);
        this.mDot = tabContainer.findViewById(R.id.tab_dot);
        addView(tabContainer);
    }


    @Override
    public void setActivated(boolean activated) {
        super.setActivated(activated);
        mIvIcon.setActivated(activated);
        mTvText.setActivated(activated);
    }


    public void showDot(int show) {
        mDot.setVisibility(show);
    }

    public void init(@DrawableRes int iconId, @StringRes int textId) {
        ImageView ivIcon = this.mIvIcon;
        ivIcon.setImageResource(iconId);
        TextView tvText = this.mTvText;
        tvText.setText(textId);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIvIcon = null;
        mTvText = null;
        mDot = null;
    }
}
