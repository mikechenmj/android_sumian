package com.sumian.sleepdoctor.widget;

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

import com.sumian.sleepdoctor.R;

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

    private OnSpannerListener mOnSpannerListener;
    private OnBackListener mOnBackListener;
    private OnMoreListener mOnMenuClickListener;

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

        a.recycle();

        ButterKnife.bind(inflate(context, R.layout.lay_title_bar, this));

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
        setPadding(0, getResources().getDimensionPixelOffset(R.dimen.space_24), 0, 0);
        setMinimumHeight(getResources().getDimensionPixelOffset(R.dimen.space_72));
        setBackgroundResource(R.color.colorPrimary);
    }

    public TitleBar addOnSpannerListener(OnSpannerListener onSpannerListener) {
        mOnSpannerListener = onSpannerListener;
        return this;
    }

    public TitleBar addOnBackListener(OnBackListener onBackListener) {
        mOnBackListener = onBackListener;
        return this;
    }

    public TitleBar setMenuOnClickListener(OnMoreListener onMoreListener) {
        mOnMenuClickListener = onMoreListener;
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

//    @SuppressWarnings("deprecation")
//    public void isShow(boolean isShow) {
//        if (isShow) {
//          //  mTvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.show_more), null);
//        } else {
//           // mTvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.show_less), null);
//        }
//       // mIsShow = isShow;
//    }

    public TextView getTitle() {
        return mTvTitle;
    }

    public TextView getMore() {
        return mTvMenu;
    }

    @SuppressLint("ResourceType")
    public TitleBar setTitle(@StringRes int titleRes) {
        if (titleRes <= 0x00)
            return this;
        setTitle(getResources().getString(titleRes));
        return this;
    }

    public void setTitle(String text) {
        mTvTitle.setText(text);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_back:
                OnBackListener onBackListener = this.mOnBackListener;
                if (onBackListener == null) return;
                onBackListener.onBack(v);
                break;
            case R.id.tv_title:
//                if (!mIsShow) {
//                    //   mTvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.show_less), null);
//                    mIsShow = true;
//                } else {
//                    // mTvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.show_more), null);
//                    mIsShow = false;
//                }
                OnSpannerListener onSpannerListener = this.mOnSpannerListener;
                if (onSpannerListener == null) return;
                onSpannerListener.onSpanner(v, mIsShow);
                break;
            case R.id.iv_more:
            case R.id.tv_menu:
                if (mOnMenuClickListener != null) {
                    mOnMenuClickListener.onMenuClick(v);
                }
                break;
            default:
                break;
        }
    }

    public interface OnSpannerListener {
        void onSpanner(View v, boolean isShow);
    }

    public interface OnBackListener {
        void onBack(View v);
    }

    public void setMenuText(String menuText) {
        mTvMenu.setText(menuText);
    }

    public void setMenuVisibility(int visibility) {
        mTvMenu.setVisibility(visibility);
    }

    public interface OnMoreListener {
        void onMenuClick(View v);
    }
}
