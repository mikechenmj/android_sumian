package com.sumian.app.improve.widget.base;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;

import com.sumian.sleepdoctor.R;

import butterknife.ButterKnife;

/**
 * Created by sm
 * on 2018/3/8.
 * desc:
 */

public abstract class BaseBlueCardView extends CardView {

    public BaseBlueCardView(@NonNull Context context) {
        this(context, null);
    }

    public BaseBlueCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseBlueCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCardBackgroundColor(getResources().getColor(R.color.light_content_bg_color));
        setRadius(getResources().getDimension(R.dimen.space_10));
        setElevation(getResources().getDimension(R.dimen.space_2));
        setContentPadding(getResources().getDimensionPixelOffset(R.dimen.space_20), getResources().getDimensionPixelOffset(R.dimen.space_20), getResources().getDimensionPixelOffset(R.dimen.space_20), getResources().getDimensionPixelOffset(R.dimen.space_20));
        View inflate = inflate(context, getLayoutRes(), this);
        initView(inflate);
    }

    protected void initView(View inflate) {

    }

    @LayoutRes
    protected abstract int getLayoutRes();

}
