package com.sumian.common.widget;

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

import com.sumian.common.R;


/**
 * Created by jzz
 * on 2017/09/30.
 * <p>
 * desc:
 */

public class TitleBar extends FrameLayout implements View.OnClickListener {

    private static int EXT_PADDING_TOP;

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
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TitleBar, defStyleAttr, 0);
        boolean showBack = a.getBoolean(R.styleable.TitleBar_show_back, true);
        String title = a.getString(R.styleable.TitleBar_text);
        boolean showSpanner = a.getBoolean(R.styleable.TitleBar_show_spanner, false);
        String menuText = a.getString(R.styleable.TitleBar_menu_text);
        Drawable moreDrawable = a.getDrawable(R.styleable.TitleBar_menu_icon);
        a.recycle();

        View rootView = inflate(context, R.layout.lay_title_bar, this);
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

//        setPadding(0, getResources().getDimensionPixelOffset(R.dimen.space_24), 0, 0);
        setMinimumHeight(getResources().getDimensionPixelOffset(R.dimen.space_48));
        setBackgroundColor(getResources().getColor(R.color.b3_color));
        // Init padding
        //setPadding(getLeft(), getTop() + getExtPaddingTop(getResources()), getRight(), getBottom());
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

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        float d = getResources().getDisplayMetrics().density;
//        int minH = (int) (d * 36 + getExtPaddingTop(getResources()));
//
//        heightMeasureSpec = MeasureSpec.makeMeasureSpec(minH, MeasureSpec.EXACTLY);
//
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }
//
//    public static int getExtPaddingTop(Resources resources) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && EXT_PADDING_TOP == 0) {
//            try {
//                @SuppressLint("PrivateApi") Class<?> clazz = Class.forName("com.android.internal.R$dimen");
//                Object object = clazz.newInstance();
//                int height = Integer.parseInt(clazz.getField("status_bar_height")
//                        .get(object).toString());
//                EXT_PADDING_TOP = resources.getDimensionPixelSize(height);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return EXT_PADDING_TOP;
//    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_back) {
            OnBackClickListener onBackClickListener = this.mOnBackClickListener;
            if (onBackClickListener == null) return;
            onBackClickListener.onBack(v);

        } else if (i == R.id.tv_title) {//                if (!mIsShow) {
//                    //   mTvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.show_less), null);
//                    mIsShow = true;
//                } else {
//                    // mTvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.mipmap.show_more), null);
//                    mIsShow = false;
//                }
            OnSpannerListener onSpannerListener = this.mOnSpannerListener;
            if (onSpannerListener == null) return;
            onSpannerListener.onSpanner(v, mIsShow);

        } else if (i == R.id.iv_menu || i == R.id.tv_menu) {
            if (mOnMenuClickListener != null) {
                mOnMenuClickListener.onMenuClick(v);
            }
        }
    }

    public interface OnSpannerListener {
        void onSpanner(View v, boolean isShow);
    }

    public interface OnBackClickListener {
        void onBack(View v);
    }

    public void setMenuText(String menuText) {
        mTvMenu.setText(menuText);
    }

    public void setMenuVisibility(int visibility) {
        mTvMenu.setVisibility(visibility);
        mTvMenu.setOnClickListener(this);
    }

    public interface OnMenuClickListener {
        void onMenuClick(View v);
    }
}
