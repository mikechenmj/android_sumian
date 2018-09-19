package com.sumian.hw.widget.base;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;

import com.sumian.sd.R;
import com.sumian.sd.theme.ViewAttributeUtil;

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
        //通过该方式读取申明的 attr 值 动态的设置属性
        if (attrs != null) {
            ViewAttributeUtil.applyCardViewBackgroundDrawable(this, context.getTheme(), ViewAttributeUtil.getBackgroundAttribute(attrs));
            ViewAttributeUtil.applyCardViewBgColor(this, context.getTheme(), ViewAttributeUtil.getAttributeValue(attrs, android.support.v7.cardview.R.attr.cardBackgroundColor));
        }

        //setCardBackgroundColor(getResources().getColor(R.color.light_content_bg_color));
        setRadius(getResources().getDimension(R.dimen.space_4));
        setElevation(getResources().getDimension(R.dimen.divider_px_1));
        setContentPadding(getResources().getDimensionPixelOffset(R.dimen.space_20), getResources().getDimensionPixelOffset(R.dimen.space_20), getResources().getDimensionPixelOffset(R.dimen.space_20), getResources().getDimensionPixelOffset(R.dimen.space_20));
        View inflate = inflate(context, getLayoutRes(), this);
        initView(inflate);
    }

    protected void initView(View inflate) {

    }

    @LayoutRes
    protected abstract int getLayoutRes();

}
