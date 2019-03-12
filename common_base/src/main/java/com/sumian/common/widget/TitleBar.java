package com.sumian.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumian.common.R;
import com.sumian.common.utils.ColorCompatUtil;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

/**
 * Created by jzz
 * on 2017/09/30.
 * <p>
 * desc:
 */

public class TitleBar extends FrameLayout implements View.OnClickListener {

    public ImageView mIvBack;
    public TextView mTvTitle;
    public ImageView mIvMenu;
    public TextView mTvMenu;

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

        View rootView = inflate(context, R.layout.common_lay_title_bar, this);
        mIvBack = rootView.findViewById(R.id.iv_back);
        if (showBack) {
            mIvBack.setOnClickListener(this);
            mIvBack.setVisibility(VISIBLE);
        } else {
            mIvBack.setVisibility(INVISIBLE);
        }
        this.mTvTitle = rootView.findViewById(R.id.tv_title);
        if (!TextUtils.isEmpty(title)) {
            mTvTitle.setText(title);
        }

        if (showSpanner) {
            mTvTitle.setOnClickListener(this);
        }
        this.mTvMenu = rootView.findViewById(R.id.tv_menu);
        if (!TextUtils.isEmpty(menuText)) {
            mTvMenu.setText(menuText);
            mTvMenu.setVisibility(VISIBLE);
            mTvMenu.setOnClickListener(this);
        }
        this.mIvMenu = rootView.findViewById(R.id.iv_menu);
        if (moreDrawable != null) {
            mIvMenu.setImageDrawable(moreDrawable);
            mIvMenu.setVisibility(VISIBLE);
            mIvMenu.setOnClickListener(this);
        }
        setIsDarkTheme(isDarkTheme);
        setBackgroundColor(ColorCompatUtil.Companion.getColor(getContext(), R.color.b3_color));
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
        mIvMenu.setOnClickListener(this);
        return this;
    }

    public TitleBar showMoreIcon(int resId) {
        mIvMenu.setImageResource(resId);
        showMoreIcon();
        return this;
    }

    public TitleBar hideMore() {
        this.mIvMenu.setVisibility(GONE);
        return this;
    }

    public TextView getTitle() {
        return mTvTitle;
    }

    public TextView getMore() {
        return mTvMenu;
    }

    public ImageView getIvMenu() {
        return mIvMenu;
    }

    @SuppressLint("ResourceType")
    public TitleBar setTitle(@StringRes int titleRes) {
        if (titleRes <= 0x00) {
            return this;
        }
        setTitle(getResources().getString(titleRes));
        return this;
    }

    public void setTitle(String text) {
        mTvTitle.setText(text);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_back) {
            OnBackClickListener onBackClickListener = this.mOnBackClickListener;
            if (onBackClickListener == null) {
                return;
            }
            onBackClickListener.onBack(v);
        } else if (id == R.id.tv_title) {
            OnSpannerListener onSpannerListener = this.mOnSpannerListener;
            if (onSpannerListener == null) {
                return;
            }
            onSpannerListener.onSpanner(v, mIsShow);
        } else if (id == R.id.iv_menu || id == R.id.tv_menu) {
            if (mOnMenuClickListener != null) {
                mOnMenuClickListener.onMenuClick(v);
            }
        }
    }

    public void setMenuText(String menuText) {
        mTvMenu.setText(menuText);
        setMenuVisibility(VISIBLE);
    }

    public void setMenuVisibility(int visibility) {
        mTvMenu.setVisibility(visibility);
        mTvMenu.setOnClickListener(this);
    }

    public void setBgColor(int color) {
        setBackgroundColor(color);
    }

    public void setTvAndIvColor(int color) {
        mTvTitle.setTextColor(color);
        mTvMenu.setTextColor(color);
        mIvBack.setColorFilter(color);
    }

    public void openTopPadding(boolean open) {
        setPadding(0, open ? (int) getResources().getDimension(R.dimen.status_bar_height) : 0, 0, 0);
    }

    public void showTitle(boolean show) {
        mTvTitle.setVisibility(show ? VISIBLE : GONE);
    }

    public void showBackArrow(boolean show) {
        mIvBack.setVisibility(show ? VISIBLE : GONE);
    }

    public void setIsDarkTheme(boolean isDarkTheme) {
        setBgColor(ColorCompatUtil.getColor(getContext(), isDarkTheme ? R.color.hw_colorPrimary : R.color.colorPrimary));
        setTvAndIvColor(ColorCompatUtil.getColor(getContext(), isDarkTheme ? R.color.bt_hole_color : R.color.white));
    }

    public interface OnMenuClickListener {
        void onMenuClick(View v);
    }

    public interface OnSpannerListener {
        void onSpanner(View v, boolean isShow);
    }

    public interface OnBackClickListener {
        void onBack(View v);
    }

}
