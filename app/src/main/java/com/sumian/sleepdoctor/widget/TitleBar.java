package com.sumian.sleepdoctor.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
    @BindView(R.id.iv_more)
    ImageView mIvMore;
    @BindView(R.id.tv_more)
    TextView mTvMore;

    private OnSpannerListener mOnSpannerListener;
    private OnBackListener mOnBackListener;
    private OnMoreListener mOnMoreListener;

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
        String moreText = a.getString(R.styleable.TitleBar_more_text);
        Drawable moreDrawable = a.getDrawable(R.styleable.TitleBar_more_icon);

        a.recycle();

        ButterKnife.bind(inflate(context, R.layout.lay_title_bar, this));

        if (showBack) {
            mIvBack.setOnClickListener(this);
            mIvBack.setVisibility(VISIBLE);
        } else {
            mIvBack.setVisibility(INVISIBLE);
        }

        if (!TextUtils.isEmpty(title)) {
            mTvTitle.setText(title);
        }

        if (showSpanner) {
            mTvTitle.setOnClickListener(this);
        }

        if (!TextUtils.isEmpty(moreText)) {
            mTvMore.setText(moreText);
            mTvMore.setVisibility(VISIBLE);
            mTvMore.setOnClickListener(this);
        }

        if (moreDrawable != null) {
            mIvMore.setImageDrawable(moreDrawable);
            mIvMore.setVisibility(VISIBLE);
            mIvMore.setOnClickListener(this);
        }

        //4.4版本之后沉浸式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //setPadding(0, getResources().getDimensionPixelOffset(R.dimen.space_24), 0, 0);
            //setMinimumHeight(getResources().getDimensionPixelOffset(R.dimen.space_72));
        }

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

    public TitleBar addOnMoreListener(OnMoreListener onMoreListener) {
        mOnMoreListener = onMoreListener;
        return this;
    }

    public TitleBar hideBack() {
        mIvBack.setVisibility(GONE);
        return this;
    }

    public TitleBar showMoreIcon() {
        mIvMore.setVisibility(VISIBLE);
        return this;
    }

    public TitleBar showMoreIcon(int resId) {
        mIvMore.setImageResource(resId);
        mIvMore.setVisibility(VISIBLE);
        return this;
    }

    public TitleBar hideMore() {
        this.mIvMore.setVisibility(GONE);
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

    public TitleBar setTitle(@StringRes int titleRes) {
        if (titleRes <= 0)
            return this;
        mTvTitle.setText(titleRes);
        return this;
    }

    public void setText(String text) {
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
            case R.id.tv_more:
                OnMoreListener onMoreListener = this.mOnMoreListener;
                if (onMoreListener == null) return;
                onMoreListener.onLoadMore(v);
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

    public interface OnMoreListener {
        void onLoadMore(View v);
    }
}
