package com.sumian.hw.widget;

import android.content.Context;
import android.content.res.TypedArray;
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

import com.sumian.sd.R;

/**
 * Created by jzz
 * on 2017/09/30.
 * <p>
 * desc:
 */

public class TitleBar extends FrameLayout implements View.OnClickListener {

    ImageView mIvBack;
    TextView mTvTitle;
    ImageView mIvMore;

    private OnBackListener mOnBackListener;
    private OnMoreListener mOnMoreListener;

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

        String title = a.getString(R.styleable.TitleBar_text);
        a.recycle();

        View inflate = inflate(context, R.layout.hw_lay_title_bar, this);
//        ButterKnife.bind(inflate);
        mIvBack = inflate.findViewById(R.id.iv_back);
        mTvTitle = inflate.findViewById(R.id.tv_title);
        mIvMore = inflate.findViewById(R.id.iv_more);

        mIvBack.setOnClickListener(this);
        mIvMore.setOnClickListener(this);

        if (!TextUtils.isEmpty(title)) {
            mTvTitle.setText(title);
        }

        //4.4版本之后沉浸式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setPadding(0, getResources().getDimensionPixelOffset(R.dimen.space_24), 0, 0);
            setMinimumHeight(getResources().getDimensionPixelOffset(R.dimen.space_72));
        }
        //setBackgroundColor(getResources().getColor(R.color.hw_colorPrimary));
    }

    public void addOnSpannerListener(OnClickListener onSpannerListener) {
        mTvTitle.setOnClickListener(onSpannerListener);
    }

    public TitleBar addOnBackListener(OnBackListener onBackListener) {
        mOnBackListener = onBackListener;
        return this;
    }

    public TitleBar addOnMoreListener(OnMoreListener onMoreListener) {
        mOnMoreListener = onMoreListener;
        return this;
    }

    public void hideBack() {
        mIvBack.setVisibility(GONE);
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

    public void hideMore() {
        this.mIvMore.setVisibility(GONE);
    }

    public TextView getTitle() {
        return mTvTitle;
    }

    public TitleBar setTitle(@StringRes int titleRes) {
        if (titleRes == 0) {
            return this;
        }
        mTvTitle.setText(titleRes);
        return this;
    }

    public void setText(String text) {
        mTvTitle.setText(text);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_back) {
            OnBackListener onBackListener = this.mOnBackListener;
            if (onBackListener == null) {
                return;
            }
            onBackListener.onBack(v);
        } else if (id == R.id.iv_more) {
            OnMoreListener onMoreListener = this.mOnMoreListener;
            if (onMoreListener == null) {
                return;
            }
            onMoreListener.onMore(v);
        }
    }

    public interface OnBackListener {
        void onBack(View v);
    }

    public interface OnMoreListener {
        void onMore(View v);
    }
}
