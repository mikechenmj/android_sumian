package com.sumian.sd.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumian.common.utils.ColorCompatUtil;
import com.sumian.sd.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jzz
 * on 2017/09/30.
 * <p>
 * desc:
 */

public class TitleBar extends FrameLayout implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.iv_menu)
    ImageView mIvMenu;
    @BindView(R.id.tv_menu)
    TextView mTvMenu;
    @BindView(R.id.v_root)
    View mRootView;

    private OnSpannerListener mOnSpannerListener;
    private OnBackClickListener mOnBackClickListener;
    private OnMenuClickListener mOnMenuClickListener;

    private boolean mIsShow;

    public TitleBar(@NonNull Context context) {
        super(context);
        init(context, null, 0);
    }

    public TitleBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public TitleBar(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @SuppressWarnings("deprecation")
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TitleBar, defStyleAttr, 0);
        boolean showBack = a.getBoolean(R.styleable.TitleBar_show_back, true);
        String title = a.getString(R.styleable.TitleBar_text);
        boolean showSpanner = a.getBoolean(R.styleable.TitleBar_show_spanner, false);
        String menuText = a.getString(R.styleable.TitleBar_menu_text);
        Drawable moreDrawable = a.getDrawable(R.styleable.TitleBar_menu_icon);
        boolean isDarkTheme = a.getBoolean(R.styleable.TitleBar_tb_dark_theme, false);
        a.recycle();
        ButterKnife.bind(inflate(context, R.layout.lay_title_bar, this));
        if (showBack) {
            mIvBack.setOnClickListener(this);
            mIvBack.setVisibility(VISIBLE);
        } else {
            mIvBack.setVisibility(INVISIBLE);
        }
        mTvTitle.setText(title);
        if (showSpanner) {
            mTvTitle.setOnClickListener(this);
        }
        if (!TextUtils.isEmpty(menuText)) {
            mTvMenu.setText(menuText);
            mTvMenu.setVisibility(VISIBLE);
            mTvMenu.setOnClickListener(this);
        }
        if (moreDrawable != null) {
            mIvMenu.setImageDrawable(moreDrawable);
            mIvMenu.setVisibility(VISIBLE);
        }
        mIvMenu.setOnClickListener(this);
//        setPadding(0, getResources().getDimensionPixelOffset(R.dimen.space_24), 0, 0);
//        setMinimumHeight(getResources().getDimensionPixelOffset(R.dimen.space_72));
        setIsDarkTheme(isDarkTheme);
    }

    public TitleBar addOnSpannerListener(OnSpannerListener onSpannerListener) {
        mOnSpannerListener = onSpannerListener;
        return this;
    }

    public TitleBar setOnBackClickListener(OnBackClickListener onBackClickListener) {
        mOnBackClickListener = onBackClickListener;
        return this;
    }

    public TitleBar setOnMenuClickListener(OnMenuClickListener onMenuClickListener) {
        mOnMenuClickListener = onMenuClickListener;
        return this;
    }

    public TitleBar hideBack() {
        mIvBack.setVisibility(GONE);
        return this;
    }

    public TitleBar showMoreIcon() {
        mIvMenu.setVisibility(VISIBLE);
        return this;
    }

    public TitleBar showMoreIcon(int resId) {
        mIvMenu.setImageResource(resId);
        mIvMenu.setVisibility(VISIBLE);
        return this;
    }

    public TitleBar hideMore() {
        this.mIvMenu.setVisibility(GONE);
        return this;
    }

    public TextView getTitle() {
        return mTvTitle;
    }

    public void setTitle(String text) {
        mTvTitle.setText(text);
    }

    public TextView getMore() {
        return mTvMenu;
    }

    @SuppressLint("ResourceType")
    public TitleBar setTitle(@StringRes int titleRes) {
        if (titleRes <= 0x00) {
            return this;
        }
        setTitle(getResources().getString(titleRes));
        return this;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_back:
                OnBackClickListener onBackClickListener = this.mOnBackClickListener;
                if (onBackClickListener == null) {
                    return;
                }
                onBackClickListener.onBack(v);
                break;
            case R.id.tv_title:
                OnSpannerListener onSpannerListener = this.mOnSpannerListener;
                if (onSpannerListener == null) {
                    return;
                }
                onSpannerListener.onSpanner(v, mIsShow);
                break;
            case R.id.iv_menu:
            case R.id.tv_menu:
                if (mOnMenuClickListener != null) {
                    mOnMenuClickListener.onMenuClick(v);
                }
                break;
            default:
                break;
        }
    }

    public void setMenuText(String menuText) {
        mTvMenu.setText(menuText);
        setMenuVisibility(View.VISIBLE);
    }

    public void setMenuVisibility(int visibility) {
        mTvMenu.setVisibility(visibility);
        mTvMenu.setOnClickListener(this);
    }

    public void setBgColor(int color) {
        mRootView.setBackgroundColor(color);
    }

    public void setTextColor(int color) {
        mTvTitle.setTextColor(color);
        mTvMenu.setTextColor(color);
        mIvBack.setColorFilter(color);
    }

    public void setIsDarkTheme(boolean isDarkTheme) {
        setBgColor(ColorCompatUtil.Companion.getColor(getContext(), isDarkTheme ? R.color.hw_colorPrimary : R.color.colorPrimary));
        setTextColor(ColorCompatUtil.Companion.getColor(getContext(), isDarkTheme ? R.color.bt_hole_color : R.color.white));
    }

    public void openTopPadding(boolean open) {
        mRootView.setPadding(0, open ? (int) getResources().getDimension(R.dimen.status_bar_height) : 0, 0, 0);
    }

    public void showBackArrow(boolean show) {
        mIvBack.setVisibility(show ? VISIBLE : GONE);
    }

    public void showTitle(boolean show) {
        mTvTitle.setVisibility(show ? VISIBLE : GONE);
    }

    public interface OnSpannerListener {
        void onSpanner(View v, boolean isShow);
    }

    public interface OnBackClickListener {
        void onBack(View v);
    }

    public interface OnMenuClickListener {
        void onMenuClick(View v);
    }
}
