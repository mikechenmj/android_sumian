package com.sumian.hw.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;

/**
 * Created by jzz
 * on 2018/1/10.
 * desc:
 */

public class VersionInfoView extends LinearLayout {

    TextView mTvDesc;
    View mUpgradeDot;
    TextView mTvSn;
    View mLine;

    public VersionInfoView(Context context) {
        this(context, null);
    }

    public VersionInfoView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VersionInfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VersionInfoView, defStyleAttr, 0);
        String upgradeLabel = typedArray.getString(R.styleable.VersionInfoView_upgrade_label);
        typedArray.recycle();
        View inflate = inflate(context, R.layout.hw_lay_version_info_item, this);
        mUpgradeDot = inflate.findViewById(R.id.v_upgrade_dot);
        mLine = inflate.findViewById(R.id.line);
        mTvDesc = inflate.findViewById(R.id.tv_desc);
        mTvSn = inflate.findViewById(R.id.tv_sn);

        mTvDesc.setText(upgradeLabel);
        setVisibility(GONE);
    }

    public void updateUpgradeInfo(boolean isShowDot, String sn) {
        mTvSn.setText(sn);
        mUpgradeDot.setVisibility(isShowDot ? VISIBLE : GONE);
        setVisibility(isShowDot ? VISIBLE : GONE);
    }

    public void hide() {
        setVisibility(GONE);
    }

    public void show() {
        setVisibility(VISIBLE);
    }

}
