package com.sumian.sddoctor.widget.nav;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.sddoctor.R;

import androidx.annotation.AttrRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * Created by jzz
 * on 2017/4/28.
 * <p>
 * desc:
 */

public class NavigationItem extends LinearLayout {

    @SuppressWarnings("unused")
    private static final String TAG = "TabButton";

    ImageView mIvIcon;
    TextView mTvText;
    View mDot;

    public NavigationItem(@NonNull Context context) {
        this(context, null);
    }

    public NavigationItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationItem(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        View inflate = inflate(context, R.layout.lay_nav_tab_item, this);
        mIvIcon = inflate.findViewById(R.id.iv_tab_icon);
        mTvText = inflate.findViewById(R.id.tv_tab_text);
        mDot = inflate.findViewById(R.id.tab_dot);
        setGravity(Gravity.CENTER);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NavigationItem);
        String text = typedArray.getString(R.styleable.NavigationItem_tab_text);
        @DrawableRes int iconId = typedArray.getResourceId(R.styleable.NavigationItem_tab_icon, 0);
        typedArray.recycle();
        mIvIcon.setImageResource(iconId);
        mTvText.setText(text);
    }

    @Override
    public void setActivated(boolean activated) {
        super.setActivated(activated);
        mIvIcon.setActivated(activated);
        mTvText.setActivated(activated);
    }


    @SuppressWarnings("unused")
    public void showDot(int show) {
        mDot.setVisibility(show);
    }

    @Override
    protected void onDetachedFromWindow() {
        setActivated(false);
        mIvIcon = null;
        mTvText = null;
        mDot = null;
        super.onDetachedFromWindow();
    }
}
