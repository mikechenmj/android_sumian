package com.sumian.sleepdoctor.widget.nav;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jzz
 * on 2017/4/28.
 * <p>
 * desc:
 */

public class ItemTab extends LinearLayout {

    @SuppressWarnings("unused")
    private static final String TAG = "TabButton";

    @BindView(R.id.iv_tab_icon)
    ImageView mIvIcon;
    @BindView(R.id.tv_tab_text)
    TextView mTvText;
    @BindView(R.id.tab_dot)
    View mDot;

    public ItemTab(@NonNull Context context) {
        this(context, null);
    }

    public ItemTab(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemTab(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        ButterKnife.bind(inflate(context, R.layout.lay_nav_tab_item, this));
        setGravity(Gravity.CENTER);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemTab);
        String text = typedArray.getString(R.styleable.ItemTab_tab_text);
        @DrawableRes int iconId = typedArray.getResourceId(R.styleable.ItemTab_tab_icon, 0);
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
