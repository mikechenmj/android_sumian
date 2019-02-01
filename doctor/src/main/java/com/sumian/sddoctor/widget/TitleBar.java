package com.sumian.sddoctor.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumian.sddoctor.R;

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

    private ImageView mIvBack;
    private TextView mTvTitle;
    private ImageView mIvMenu;
    private TextView mTvMenu;

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
        View inflate = inflate(context, R.layout.lay_title_bar, this);
        mIvBack = inflate.findViewById(R.id.iv_back);
        mTvTitle = inflate.findViewById(R.id.tv_title);
        mIvMenu = inflate.findViewById(R.id.iv_menu);
        mTvMenu = inflate.findViewById(R.id.tv_menu);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TitleBar, defStyleAttr, 0);
        boolean showBack = a.getBoolean(R.styleable.TitleBar_show_back, true);
        String title = a.getString(R.styleable.TitleBar_text);
        boolean showSpanner = a.getBoolean(R.styleable.TitleBar_show_spanner, false);
        String menuText = a.getString(R.styleable.TitleBar_menu_text);
        Drawable moreDrawable = a.getDrawable(R.styleable.TitleBar_menu_icon);
        a.recycle();
        if (showBack) {
            mIvBack.setOnClickListener(this);
            mIvBack.setVisibility(VISIBLE);
        } else {
            mIvBack.setVisibility(INVISIBLE);
        }
        //if (!TextUtils.isEmpty(title)) {
        mTvTitle.setText(title);
        // }
        if (showSpanner) {
            mTvTitle.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.space_10));
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
            mIvMenu.setOnClickListener(this);
        }
        setBackgroundColor(Color.WHITE);
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
        mIvMenu.setVisibility(VISIBLE);
        mIvMenu.setOnClickListener(this);
        return this;
    }

    public TitleBar hideMore() {
        this.mIvMenu.setVisibility(GONE);
        return this;
    }

    @SuppressWarnings("deprecation")
    public void isShow(boolean isShow) {
        if (isShow) {
            mTvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_nav_icon_upper), null);
        } else {
            mTvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_nav_icon_lower), null);
        }
        mIsShow = isShow;
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

    public ImageView getIvMenu() {
        return mIvMenu;
    }

    @SuppressLint("ResourceType")
    public TitleBar setTitle(@StringRes int titleRes) {
        if (titleRes <= 0x00)
            return this;
        setTitle(getResources().getString(titleRes));
        return this;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                OnBackClickListener onBackClickListener = this.mOnBackClickListener;
                if (onBackClickListener == null) return;
                onBackClickListener.onBack(v);
                break;
            case R.id.tv_title:
                if (!mIsShow) {
                    mTvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_nav_icon_upper), null);
                    mIsShow = true;
                } else {
                    mTvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_nav_icon_lower), null);
                    mIsShow = false;
                }
                OnSpannerListener onSpannerListener = this.mOnSpannerListener;
                if (onSpannerListener == null) return;
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
        mTvMenu.setOnClickListener(this);
    }

    public void setSpannerShowStatus(boolean isShow) {
        this.mIsShow = isShow;
    }

    public void setMenuVisibility(int visibility) {
        mTvMenu.setVisibility(visibility);
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
